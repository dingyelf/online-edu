package com.edu.online.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.online.entity.EduCourse;

import java.util.List;

public interface EduCourseService extends IService<EduCourse> {

    /**
     * 获取课程缓存
     *
     * @return
     */
    List<EduCourse> listWithCache();

    /**
     * 根据id查询课程（原始完整数据，详情缓存）
     */
    EduCourse getByIdWithCache(Long courseId);

    /**
     * 清理课程缓存（新增/编辑/删除课程调用）
     */
    void refreshCourseCache();

}
