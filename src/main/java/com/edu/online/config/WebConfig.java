package com.edu.online.config;

import com.edu.online.properties.FileProperties;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Resource
    private FileProperties fileProperties;

    public WebConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 虚拟路径 /files/** 映射到本地磁盘目录
        registry.addResourceHandler(fileProperties.getAccessPrefix() + "/**")
                .addResourceLocations("file:" + fileProperties.getUploadPath() + "/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                // 拦截所有接口
                .addPathPatterns("/api/**")
                // 放行公开接口
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",
                        "/api/course/list",
                        "/api/course/detail");
    }

}
