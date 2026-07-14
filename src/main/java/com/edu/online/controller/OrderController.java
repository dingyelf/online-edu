package com.edu.online.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 创建订单
     *
     * @param courseId
     * @param session
     * @return
     */
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

    /**
     * 获取我的订单列表
     */
    @GetMapping("/myList")
    public Result<List<EduOrder>> myOrderList(HttpSession session) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        LambdaQueryWrapper<EduOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduOrder::getUserId, loginUser.getId());
        wrapper.orderByDesc(EduOrder::getCreateTime);
        List<EduOrder> list = orderService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 订单详情
     */
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

    /**
     * 取消未支付订单
     */
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

    /**
     * 模拟支付成功（测试使用！正式环境删除）
     */
    @PostMapping("/mockPay")
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
