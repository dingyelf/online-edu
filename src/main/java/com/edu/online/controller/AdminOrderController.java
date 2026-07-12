package com.edu.online.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.online.common.Result;
import com.edu.online.entity.EduOrder;
import com.edu.online.service.EduOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/order")
public class AdminOrderController {

    private final EduOrderService orderService;

    public AdminOrderController(EduOrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 管理员查询全部订单
     */
    @GetMapping("/list")
    public Result<List<EduOrder>> getAllOrder() {
        LambdaQueryWrapper<EduOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(EduOrder::getCreateTime);
        List<EduOrder> list = orderService.list(wrapper);
        return Result.success(list);
    }

}
