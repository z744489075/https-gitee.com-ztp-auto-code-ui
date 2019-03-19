package com.zengtengpeng.ui.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zengtengpeng.autoCode.StartCode;
import com.zengtengpeng.autoCode.config.AutoCodeConfig;
import com.zengtengpeng.autoCode.config.DatasourceConfig;
import com.zengtengpeng.autoCode.config.GlobalConfig;
import com.zengtengpeng.common.bean.DataRes;
import com.zengtengpeng.ui.constant.ParamConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 全局参数配置
 */
@RestController
@RequestMapping("/auto-code-ui/data/global")
public class GlobalController {


    Logger logger = LoggerFactory.getLogger(GlobalController.class);
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ServletContext servletContext;


    /**
     * 查询全局参数
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("queryConfig")
    public DataRes queryConfig(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        AutoCodeConfig autoCodeConfig = (AutoCodeConfig) request.getServletContext().getAttribute(ParamConstant.autoCodeConfig);
        return DataRes.success(autoCodeConfig.getGlobalConfig());
    }
    /**
     * 保存全局参数
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("saveConfig")
    public DataRes saveConfig(HttpServletRequest request, HttpServletResponse response, GlobalConfig globalConfig)  {
        AutoCodeConfig a = (AutoCodeConfig) request.getServletContext().getAttribute(ParamConstant.autoCodeConfig);
        a.setGlobalConfig(globalConfig);
        return DataRes.success("成功");
    }

    @PostConstruct
    public void init(){
        Yaml yaml = new Yaml();
        AutoCodeConfig autoCodeConfig = yaml.loadAs(StartCode.class.getClassLoader().getResourceAsStream("auto-code.yaml"), AutoCodeConfig.class);

        try {
            Connection connection = dataSource.getConnection();
            DatasourceConfig datasourceConfig = new DatasourceConfig();
            datasourceConfig.setName(connection.getCatalog());
            datasourceConfig.setDataSource(dataSource);
            autoCodeConfig.setDatasourceConfig(datasourceConfig);
            servletContext.setAttribute(ParamConstant.autoCodeConfig,autoCodeConfig);
            logger.info("初始化完毕");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
