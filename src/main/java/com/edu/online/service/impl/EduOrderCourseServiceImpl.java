package com.edu.online.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.online.entity.EduOrder;
import com.edu.online.mapper.EduOrderMapper;
import com.edu.online.service.EduOrderService;
import org.springframework.stereotype.Service;

@Service
public class EduOrderCourseServiceImpl extends ServiceImpl<EduOrderMapper, EduOrder> implements EduOrderService {

}
