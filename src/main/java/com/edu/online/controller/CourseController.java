package com.edu.online.controller;

import com.edu.online.common.Result;
import com.edu.online.entity.EduCourse;
import com.edu.online.service.EduCourseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    private final EduCourseService courseService;

    public CourseController(EduCourseService courseService) {
        this.courseService = courseService;
    }

    // 公开课程列表
    @GetMapping("/list")
    public Result<List<EduCourse>> getCourseList() {
        List<EduCourse> list = courseService.list();
        return Result.success(list);
    }

    // 公开课程详情
    @GetMapping("/detail")
    public Result<EduCourse> getCourseDetail(@RequestParam("id") Long id) {
        EduCourse course = courseService.getById(id);
        if (course == null) {
            return Result.fail(404, "课程不存在");
        }
        return Result.success(course);
    }

}
