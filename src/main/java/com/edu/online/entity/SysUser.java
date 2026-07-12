package com.edu.online.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.IdType.AUTO;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = AUTO)
    private Long id;
    private String username;
    private String password;
    private Integer role;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
