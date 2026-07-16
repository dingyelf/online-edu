package com.edu.online.service.impl;

import com.edu.online.properties.FileProperties;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class LocalFileService {

    @Resource
    private FileProperties fileProperties;

    public String upload(MultipartFile file, String folder) throws IOException {
        String originName = file.getOriginalFilename();
        String suffix = originName.substring(originName.lastIndexOf("."));
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuidName = UUID.randomUUID() + suffix;

        File targetDir = new File(fileProperties.getUploadPath() + File.separator + folder + File.separator + dateStr);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        File saveFile = new File(targetDir, uuidName);
        file.transferTo(saveFile);

        // 返回前端可直接访问地址
        return fileProperties.getAccessPrefix() + "/" + folder + "/" + dateStr + "/" + uuidName;
    }

    /**
     * 根据数据库url删除本地磁盘文件
     * @param fileUrl
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null && fileUrl.isBlank()) {
            return;
        }
        String prefix = fileProperties.getAccessPrefix() + "/";
        String relativePath = fileUrl.replace(prefix, "");
        File realFile = new File(fileProperties.getUploadPath() + File.separator + relativePath);
        if (realFile.exists()) {
            realFile.delete();
        }
    }


}
