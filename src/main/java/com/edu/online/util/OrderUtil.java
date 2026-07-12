package com.edu.online.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Component;

@Component
public class OrderUtil {

    // 雪花算法，workId/dataCenterId开发环境固定
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

    /**
     * 生成唯一订单号
     *
     * @return
     */
    public String generateOrderNo() {
        return String.valueOf(SNOWFLAKE.nextId());
    }

}
