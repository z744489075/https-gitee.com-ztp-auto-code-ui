package com.zengtengpeng.ui.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zengtengpeng.autoCode.StartCode;
import com.zengtengpeng.autoCode.config.AutoCodeConfig;
import com.zengtengpeng.autoCode.config.DatasourceConfig;
import com.zengtengpeng.autoCode.config.GlobalConfig;
import com.zengtengpeng.autoCode.utils.MyStringUtils;
import com.zengtengpeng.common.bean.DataRes;
import com.zengtengpeng.jdbc.utils.JDBCUtils;
import com.zengtengpeng.ui.constant.ParamConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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

    @Autowired(required = false)
    private AutoCodeConfig autoCodeConfig;

    @Value("${spring.datasource.name:}")
    private String name1;
    @Value("${datasource.name:}")
    private String name2;


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
    public DataRes saveConfig(HttpServletRequest request, HttpServletResponse response)  {
        AutoCodeConfig a = (AutoCodeConfig) request.getServletContext().getAttribute(ParamConstant.autoCodeConfig);
        GlobalConfig param = a.getGlobalConfig();
        request.getParameterMap().forEach((key, value) -> {
            for (Method method : param.getClass().getMethods()) {
                if(method.getName().equals("set"+ MyStringUtils.firstUpperCase(key))){
                    try {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if(parameterTypes.length==1){
                            String simpleName = parameterTypes[0].getSimpleName();
                            if("Integer".equals(simpleName)||"int".equals(simpleName)){
                                method.invoke(param, Integer.valueOf(value[0]));
                            }else if("Boolean".equals(simpleName)||"boolean".equals(simpleName)){
                                method.invoke(param, Boolean.valueOf(value[0]));
                            }else if ("String".equals(simpleName)){
                                method.invoke(param,value[0]);
                            }else if ("Byte".equals(simpleName)){
                                method.invoke(param,Byte.valueOf(value[0]));
                            }
                        }
                    } catch (Exception e) {
                       throw new RuntimeException(e);
                    }
                    break;
                }
            }
        });
        a.setGlobalConfig(param);
        return DataRes.success("成功");
    }

    /**
     * 获取数据库表名称
     * @param request
     * @param response
     * @param globalConfig
     * @return
     */
    @RequestMapping("getTablesName")
    public DataRes getTablesName(HttpServletRequest request, HttpServletResponse response, GlobalConfig globalConfig)  {
        try {
            Connection connection = dataSource.getConnection();
            String ca = null;
            if(!MyStringUtils.isEmpty(name1)){
                ca=name1;
            }
            if(!MyStringUtils.isEmpty(name2)){
                ca=name2;
            }
            List<String> tablesName = JDBCUtils.getTablesName(connection,ca);
            return DataRes.success(tablesName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 获取根据表名获取字段
     * @param request
     * @param response
     * @param tableName
     * @return
     */
    @RequestMapping("getTablesColumn")
    public DataRes getTablesColumn(HttpServletRequest request, HttpServletResponse response,String tableName)  {
        try {
            Connection connection = dataSource.getConnection();
            List<String> tablesColumn = JDBCUtils.getTablesColumn(connection, tableName);
            return DataRes.success(tablesColumn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @PostConstruct
    public void init(){
        Yaml yaml = new Yaml();
        if(autoCodeConfig==null){
            autoCodeConfig = yaml.loadAs(StartCode.class.getClassLoader().getResourceAsStream("auto-code.yaml"), AutoCodeConfig.class);
        }

        try {
            Connection connection = dataSource.getConnection();
            DatasourceConfig datasourceConfig = new DatasourceConfig();
            String catalog = connection.getCatalog();
            if(!MyStringUtils.isEmpty(name1)){
                catalog=name1;
            }
            if(!MyStringUtils.isEmpty(name2)){
                catalog=name2;
            }
            datasourceConfig.setName(catalog);
            datasourceConfig.setDataSource(dataSource);
            autoCodeConfig.setDatasourceConfig(datasourceConfig);
            servletContext.setAttribute(ParamConstant.autoCodeConfig,autoCodeConfig);
            logger.info("初始化完毕");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
