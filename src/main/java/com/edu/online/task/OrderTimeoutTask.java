package com.edu.online.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.edu.online.entity.EduOrder;
import com.edu.online.mapper.EduOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderTimeoutTask {

    @Resource
    private EduOrderMapper orderMapper;

    /**
     * 每3分钟执行一次
     * 取消创建时间超过15分钟、状态为待支付(0)的订单
     */
    @Scheduled(fixedRate = 3 * 60 * 1000)
    public void closeTimeoutOrder() {
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(15);

        LambdaUpdateWrapper<EduOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(EduOrder::getStatus, 2)
                .eq(EduOrder::getStatus, 0)
                .lt(EduOrder::getCreateTime, expireTime);

        int count = orderMapper.update(null, updateWrapper);
        if (count > 0) {
            System.out.println("【订单定时任务】自动关闭超时订单数量：" + count);
        }
    }
}
