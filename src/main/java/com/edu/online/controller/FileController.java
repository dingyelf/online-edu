package com.edu.online.controller;

import com.edu.online.common.Result;
import com.edu.online.exception.BusinessException;
import com.edu.online.service.impl.LocalFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "文件上传", description = "图片和视频文件的上传，支持格式校验")
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Resource
    private LocalFileService localFileService;

    @Operation(summary = "图片上传", description = "上传课程封面等图片文件，支持jpg/jpeg/png/gif/webp格式")
    @PostMapping("/upload/image")
    public Result<String> uploadImage(@RequestParam(name = "file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new BusinessException("文件不能为空");
        }
        String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        if (!".jpg.jpeg.png.gif.webp".contains(suffix)) {
            throw new BusinessException("仅支持jpg,jpeg,png,gif,webp图片格式");
        }
        try {
            String url = localFileService.upload(file, "image");
            return Result.success(url);
        } catch (IOException e) {
            throw new BusinessException("图片上传失败，请检查目录权限");
        }
    }


    @Operation(summary = "视频上传", description = "上传课程视频文件，支持mp4/mov格式")
    @PostMapping("/upload/video")
    public Result<String> uploadVideo(@RequestParam(name = "file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new BusinessException("文件不能为空");
        }
        String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        if (!".mp4.mov".contains(suffix)) {
            throw new BusinessException("仅支持mp4、mov视频格式");
        }
        try {
            String url = localFileService.upload(file, "video");
            return Result.success(url);
        } catch (IOException e) {
            throw new BusinessException("视频上传失败，请检查目录权限");
        }
    }
}
