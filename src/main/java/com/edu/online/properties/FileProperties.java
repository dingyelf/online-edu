package com.edu.online.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileProperties {

    /**
     * 文件磁盘根目录
     */
    private String uploadPath;

    /**
     * 浏览器访问虚拟前缀 固定 files/
     */
    private String accessPrefix;

}
