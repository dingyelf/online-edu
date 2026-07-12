package com.edu.online.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.online.entity.EduUserCourse;
import com.edu.online.mapper.EduUserCourseMapper;
import com.edu.online.service.EduUserCourseService;
import org.springframework.stereotype.Service;

@Service
public class EduUserCourseServiceImpl extends ServiceImpl<EduUserCourseMapper, EduUserCourse> implements EduUserCourseService {
    @Override
    public boolean userHasBuyCourse(Long userId, Long courseId) {
        LambdaQueryWrapper<EduUserCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduUserCourse::getUserId, userId);
        wrapper.eq(EduUserCourse::getCourseId, courseId);
        return this.exists(wrapper);
    }
}
