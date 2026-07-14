package com.edu.online.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.online.common.PageResult;
import com.edu.online.common.Result;
import com.edu.online.entity.EduCourse;
import com.edu.online.service.EduCourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/course")
public class AdminCourseController {

    private final EduCourseService courseService;

    public AdminCourseController(EduCourseService courseService) {
        this.courseService = courseService;
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
    public Result<String> updateCourse(@RequestBody EduCourse course) {
        courseService.saveOrUpdate(course);
        courseService.refreshCourseCache();
        return Result.success("修改成功");
    }

    // 删除课程
    @DeleteMapping("/delete")
    public Result<String> deleteCourse(@RequestParam("id") Long id) {
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
