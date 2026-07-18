package com.edu.online.common;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    /**
     * 当前页数据
     */
    private List<T> records;
    /**
     * 总条数
     */
    private Long total;
    /**
     * 当前页码
     */
    private Long current;
    /**
     * 每页条数
     */
    private Long size;

    public static <T> PageResult<T> build(com.baomidou.mybatisplus.core.metadata.IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(page.getRecords());
        result.setTotal(page.getTotal());
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        return result;
    }

    /**
     * 内存List分页构建（用于课程列表缓存分页）
     */
    public static <T> PageResult<T> buildFromList(List<T> allList, Long current, Long size) {
        PageResult<T> result = new PageResult<>();
        long total = allList.size();
        // 计算起始下标
        long start = (current - 1) * size;
        long end = Math.min(start + size, total);
        List<T> pageData = allList.subList((int) start, (int) end);

        result.setRecords(pageData);
        result.setTotal(total);
        result.setCurrent(current);
        result.setSize(size);
        return result;
    }

}
