package com.edu.online.controller;

import com.edu.online.common.Result;
import com.edu.online.entity.EduCourse;
import com.edu.online.entity.SysUser;
import com.edu.online.service.EduCourseService;
import com.edu.online.service.EduUserCourseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    private final EduCourseService courseService;
    private final EduUserCourseService userCourseService;

    public CourseController(EduCourseService courseService, EduUserCourseService userCourseService) {
        this.courseService = courseService;
        this.userCourseService = userCourseService;
    }

    // 公开课程列表
    @GetMapping("/list")
    public Result<List<EduCourse>> getCourseList() {
        List<EduCourse> list = courseService.listWithCache();
        return Result.success(list);
    }

    // 公开课程详情
    @GetMapping("/detail")
    public Result<EduCourse> getCourseDetail(@RequestParam("id") Long id, HttpSession session) {
        EduCourse course = courseService.getById(id);
        if (course == null) {
            return Result.fail(404, "课程不存在");
        }

        // 免费课程直接完整返回
        BigDecimal zero = BigDecimal.ZERO;
        if (zero.compareTo(course.getPrice()) >= 0) {
            return Result.success(course);
        }

        // 付费课程，判断是否登录+是否已购买
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        boolean hasBuy = false;
        if (loginUser != null) {
            hasBuy = userCourseService.userHasBuyCourse(loginUser.getId(), id);
        }

        // 未购买：清空视频地址，前端不能播放
        if (!hasBuy) {
            course.setVideoUrl("");
        }
        return Result.success(course);
    }

}
