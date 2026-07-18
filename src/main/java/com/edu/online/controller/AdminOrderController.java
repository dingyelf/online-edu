package com.edu.online.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.edu.online.common.PageResult;
import com.edu.online.common.Result;
import com.edu.online.entity.EduOrder;
import com.edu.online.service.EduOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "订单管理（管理员）", description = "管理员查看全部订单，需要管理员权限")
@RestController
@RequestMapping("/api/admin/order")
public class AdminOrderController {

    private final EduOrderService orderService;

    public AdminOrderController(EduOrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "全部订单列表", description = "管理员分页查看所有用户的订单（按创建时间倒序）")
    @GetMapping("/list")
    public Result<PageResult<EduOrder>> orderPage(
            @RequestParam(name = "current", defaultValue = "1") Long current,
            @RequestParam(name = "size", defaultValue = "10") Long size) {
        LambdaQueryWrapper<EduOrder> wrapper = Wrappers.lambdaQuery();
        wrapper.orderByDesc(EduOrder::getCreateTime);
        IPage<EduOrder> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        page = orderService.page(page, wrapper);
        return Result.success(PageResult.build(page));
    }

}
