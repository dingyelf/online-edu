package com.edu.online.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.online.entity.SysUser;
import com.edu.online.service.SysUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitAdminConfig implements CommandLineRunner {

    private final SysUserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public InitAdminConfig(SysUserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        // 启动时检查是否存在管理员账号，不存在则自动创建
        long count = userService.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "admin"));
        if (count == 0) {
            SysUser admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(1);
            userService.save(admin);
            System.out.println("管理员账号初始化完成，账号：admin 密码：admin123");
        }
    }
}
