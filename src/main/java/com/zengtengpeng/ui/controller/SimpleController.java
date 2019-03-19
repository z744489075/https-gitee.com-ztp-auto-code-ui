package com.zengtengpeng.ui.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * 单表的控制器
 */
public class SimpleController {
    private DataSource dataSource;
    public SimpleController(DataSource dataSource){
        this.dataSource=dataSource;
    }

    /**
     * 构建单表
     * @return
     */
    public Object build(HttpServletRequest request, HttpServletResponse response){
        return null;
    }
}
