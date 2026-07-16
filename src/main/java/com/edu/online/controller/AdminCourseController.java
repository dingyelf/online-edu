package com.edu.online.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.online.common.PageResult;
import com.edu.online.common.Result;
import com.edu.online.dto.CourseUpdateDTO;
import com.edu.online.entity.EduCourse;
import com.edu.online.service.EduCourseService;
import com.edu.online.service.impl.LocalFileService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/course")
public class AdminCourseController {

    private final EduCourseService courseService;
    private final LocalFileService localFileService;

    public AdminCourseController(EduCourseService courseService, LocalFileService localFileService) {
        this.courseService = courseService;
        this.localFileService = localFileService;
    }

    // 课程列表
    @GetMapping("/list")
    public Result<PageResult<EduCourse>> coursePage(
            @RequestParam(name = "current", defaultValue = "1") Long current,
            @RequestParam(name = "size", defaultValue = "8") Long size) {
        IPage<EduCourse> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        page = courseService.page(page);
        return Result.success(PageResult.build(page));
    }

    // 新增课程
    @PostMapping("/add")
    public Result<String> addCourse(@RequestBody EduCourse course) {
        courseService.saveOrUpdate(course);
        courseService.refreshCourseCache();
        return Result.success("新增成功");
    }

    // 编辑课程
    @PostMapping("/update")
    public Result<String> updateCourse(@RequestBody CourseUpdateDTO courseUpdateDTO) {
        // 查询原有课程数据
        EduCourse oldCourse = courseService.getById(courseUpdateDTO.getId());
        // 更换封面，删除本地旧图片
        if ("1".equals(courseUpdateDTO.getChangeCover())) {
            localFileService.deleteFile(oldCourse.getCover());
        }
        // 更换视频，删除本地旧视频
        if ("1".equals(courseUpdateDTO.getChangeVideo())) {
            localFileService.deleteFile(oldCourse.getVideoUrl());
        }

        // DTO转数据库实体
        EduCourse course = new EduCourse();
        course.setId(courseUpdateDTO.getId());
        course.setTitle(courseUpdateDTO.getTitle());
        course.setCover(courseUpdateDTO.getCover());
        course.setVideoUrl(courseUpdateDTO.getVideoUrl());
        course.setPrice(courseUpdateDTO.getPrice());
        course.setDescription(courseUpdateDTO.getDescription());
        courseService.saveOrUpdate(course);
        courseService.refreshCourseCache();
        return Result.success("修改成功");
    }

    // 删除课程
    @DeleteMapping("/delete")
    public Result<String> deleteCourse(@RequestParam("id") Long id) {
        EduCourse old = courseService.getById(id);
        if (old.getVideoUrl() != null) {
            localFileService.deleteFile(old.getVideoUrl());
        }
        if (old.getCover() != null) {
            localFileService.deleteFile(old.getCover());
        }
        courseService.removeById(id);
        courseService.refreshCourseCache();
        return Result.success("删除成功");
    }

    // 根据ID查询课程
    @GetMapping("/getById")
    public Result<EduCourse> getById(@RequestParam("id") Long id) {
        return Result.success(courseService.getById(id));
    }

}
