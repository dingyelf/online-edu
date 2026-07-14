package com.edu.online.controller;

import com.edu.online.common.PageResult;
import com.edu.online.common.Result;
import com.edu.online.entity.EduCourse;
import com.edu.online.entity.SysUser;
import com.edu.online.exception.BusinessException;
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
    public Result<PageResult<EduCourse>> getCourseList(
            @RequestParam(value = "current", defaultValue = "1") Long current,
            @RequestParam(value = "size", defaultValue = "8") Long size) {
        List<EduCourse> list = courseService.listWithCache();
        PageResult<EduCourse> pageResult = PageResult.buildFromList(list, current, size);
        return Result.success(pageResult);
    }

    // 公开课程详情
    @GetMapping("/detail")
    public Result<EduCourse> getCourseDetail(@RequestParam("id") Long id, HttpSession session) {
        // 使用带缓存方法获取原始课程
        EduCourse course = courseService.getByIdWithCache(id);
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
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

        // 复制对象，不要修改缓存内原始数据！！
        EduCourse respCourse = new EduCourse();
        respCourse.setId(course.getId());
        respCourse.setTitle(course.getTitle());
        respCourse.setCover(course.getCover());
        respCourse.setDescription(course.getDescription());
        respCourse.setPrice(course.getPrice());
        respCourse.setCreateTime(course.getCreateTime());
        respCourse.setUpdateTime(course.getUpdateTime());
        respCourse.setDeleted(course.getDeleted());

        // 未购买：清空视频地址，前端不能播放
        if (hasBuy) {
            respCourse.setVideoUrl(course.getVideoUrl());
        } else {
            respCourse.setVideoUrl("");
        }
        return Result.success(respCourse);
    }

}
