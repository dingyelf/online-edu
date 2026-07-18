package com.edu.online.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.online.common.PageResult;
import com.edu.online.common.Result;
import com.edu.online.entity.EduCourse;
import com.edu.online.entity.EduOrder;
import com.edu.online.entity.EduUserCourse;
import com.edu.online.entity.SysUser;
import com.edu.online.exception.BusinessException;
import com.edu.online.service.EduCourseService;
import com.edu.online.service.EduOrderService;
import com.edu.online.service.EduUserCourseService;
import com.edu.online.util.OrderUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Tag(name = "订单模块（用户）", description = "用户订单的创建、支付、取消、查询")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final EduCourseService courseService;
    private final EduOrderService orderService;
    private final OrderUtil orderUtil;
    private final EduUserCourseService userCourseService;

    public OrderController(EduCourseService courseService, OrderUtil orderUtil, EduOrderService orderService, EduUserCourseService userCourseService) {
        this.courseService = courseService;
        this.orderService = orderService;
        this.orderUtil = orderUtil;
        this.userCourseService = userCourseService;
    }

    @Operation(summary = "创建订单", description = "为指定课程创建待支付订单，雪花算法生成订单号")
    @RequestMapping("/create")
    public Result<EduOrder> createOrder(@RequestParam("courseId") Long courseId, HttpSession session) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        Long userId = loginUser.getId();

        EduCourse course = courseService.getById(courseId);
        if (course == null) {
            throw new BusinessException(400, "课程不存在");
        }

        if (BigDecimal.ZERO.compareTo(course.getPrice()) >= 0) {
            throw new BusinessException(400, "免费课程无需购买");
        }

        EduOrder order = new EduOrder();
        order.setOrderNo(orderUtil.generateOrderNo());
        order.setUserId(userId);
        order.setCourseId(courseId);
        order.setAmount(course.getPrice());
        order.setStatus(0);
        orderService.save(order);

        return Result.success(order);
    }

    @Operation(summary = "我的订单列表", description = "分页查询当前登录用户的订单，按创建时间倒序")
    @GetMapping("/myList")
    public Result<PageResult<EduOrder>> myOrderList(
            @RequestParam(name = "current", defaultValue = "1") Long current,
            @RequestParam(name = "size", defaultValue = "8") Long size,
            HttpSession session) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");

        LambdaQueryWrapper<EduOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduOrder::getUserId, loginUser.getId());
        wrapper.orderByDesc(EduOrder::getCreateTime);

        IPage<EduOrder> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        page = orderService.page(page, wrapper);

        return Result.success(PageResult.build(page));
    }

    @Operation(summary = "订单详情", description = "根据ID查询单个订单，只能查看自己的订单")
    @GetMapping("/getById")
    public Result<EduOrder> getOrderInfo(@RequestParam("id") Long orderId,
                                         HttpSession session) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        EduOrder order = orderService.getById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        // 只能查看自己的订单
        if (!order.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(403, "无权查看该订单");
        }
        return Result.success(order);
    }

    @Operation(summary = "取消订单", description = "取消待支付状态的订单，状态改为已取消")
    @PostMapping("/cancel")
    public Result<String> cancelOrder(@RequestParam("id") Long orderId,
                                      HttpSession session) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        EduOrder order = orderService.getById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!order.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(403, "无权操作");
        }
        // 只有待支付可以取消
        if (order.getStatus() != 0) {
            throw new BusinessException(400, "仅待支付订单可取消");
        }
        order.setStatus(2);
        orderService.updateById(order);
        return Result.success("取消成功");
    }

    @Operation(summary = "模拟支付 (⚠️ 测试接口)", description = "模拟支付成功，修改订单状态为已支付并创建购买记录。仅用于测试，正式环境请删除")
    @PostMapping("/mockPay")
    @Transactional(rollbackFor = {Exception.class})
    public Result<String> mockPay(@RequestParam("orderId") Long orderId, HttpSession session) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        EduOrder order = orderService.getById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!order.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(403, "无权操作");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(400, "订单状态异常");
        }

        // ========== 事务建议：后续包装 @Transactional ==========
        // 1. 修改订单为已支付
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderService.updateById(order);

        // 2. 新增用户课程购买记录
        EduUserCourse record = new EduUserCourse();
        record.setUserId(order.getUserId());
        record.setCourseId(order.getCourseId());
        record.setOrderId(order.getId());
        userCourseService.save(record);

        return Result.success("模拟支付成功，已获得课程观看权限");
    }

}
