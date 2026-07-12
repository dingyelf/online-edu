package com.edu.online.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.online.common.Result;
import com.edu.online.entity.SysUser;
import com.edu.online.service.SysUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final SysUserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController(SysUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody SysUser sysUser) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, sysUser.getUsername());
        long count = userService.count(queryWrapper);
        if (count > 0) {
            return Result.fail(400, "用户名已存在");
        }
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        sysUser.setRole(0);
        userService.save(sysUser);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody SysUser user, HttpSession session) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, user.getUsername());
        SysUser dbUser = userService.getOne(queryWrapper);
        if (dbUser == null || !passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            return Result.fail(400, "用户名或密码错误");
        }
        // 密码脱敏后存入session
        dbUser.setPassword(null);
        session.setAttribute("loginUser", dbUser);
        return Result.success(dbUser);
    }

    // 获取当前登录用户信息
    @GetMapping("/info")
    public Result<SysUser> getLoginInfo(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("loginUser");
        if (user == null) {
            return Result.fail(401, "未登录");
        }
        return Result.success(user);
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.removeAttribute("loginUser");
        return Result.success("退出成功");
    }

}
