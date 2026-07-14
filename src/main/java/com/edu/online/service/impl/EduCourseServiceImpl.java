package com.edu.online.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.online.entity.EduCourse;
import com.edu.online.mapper.EduCourseMapper;
import com.edu.online.service.EduCourseService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    private static final String COURSE_LIST_KEY = "online:edu:course:list";
    // 课程详情key前缀
    private static final String COURSE_INFO_PREFIX = "online:edu:course:info:";
    private static final long EXPIRE_SECONDS = 10 * 60; // 10分钟

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<EduCourse> listWithCache() {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        // 1. 查询缓存
        Object cacheObject = ops.get(COURSE_LIST_KEY);
        if (cacheObject != null) {
            return (List<EduCourse>) cacheObject;
        }

        // 2. 缓存不存在，查询数据库
        List<EduCourse> courseList = this.list();
        // 3. 写入缓存
        ops.set(COURSE_LIST_KEY, courseList, EXPIRE_SECONDS, TimeUnit.SECONDS);
        return courseList;
    }

    @Override
    public EduCourse getByIdWithCache(Long courseId) {
        String key = COURSE_INFO_PREFIX + courseId;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Object cache = ops.get(key);
        if (cache != null) {
            return (EduCourse) cache;
        }
        // 缓存不存在查询数据库
        EduCourse course = this.getById(courseId);
        if (course != null) {
            ops.set(key, course, EXPIRE_SECONDS, TimeUnit.SECONDS);
        }
        return course;
    }


    @Override
    public void refreshCourseCache() {
        // 删除列表缓存
        redisTemplate.delete(COURSE_LIST_KEY);
        var keys = redisTemplate.keys(COURSE_INFO_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
