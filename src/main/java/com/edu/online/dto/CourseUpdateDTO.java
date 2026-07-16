package com.edu.online.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CourseUpdateDTO {

    private Long id;
    private String title;
    private String cover;
    private String description;
    private String videoUrl;
    private BigDecimal price;

    private String changeCover;
    private String changeVideo;
}
