package com.edu.online.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.online.entity.EduUserCourse;

public interface EduUserCourseService extends IService<EduUserCourse> {

    boolean userHasBuyCourse(Long userId, Long courseId);

}
