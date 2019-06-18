package com.zengtengpeng.ui.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zengtengpeng.autoCode.StartCode;
import com.zengtengpeng.autoCode.config.AutoCodeConfig;
import com.zengtengpeng.autoCode.config.TableConfig;
import com.zengtengpeng.autoCode.utils.MyStringUtils;
import com.zengtengpeng.common.bean.DataRes;
import com.zengtengpeng.common.bean.ResponseCode;
import com.zengtengpeng.common.drive.AutoCodeDrive;
import com.zengtengpeng.jdbc.bean.Bean;
import com.zengtengpeng.jdbc.bean.BeanColumn;
import com.zengtengpeng.ui.constant.ParamConstant;
import com.zengtengpeng.ui.utils.RequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 单表的控制器
 */
@RestController
@RequestMapping("/auto-code-ui/data/simple")
public class SimpleController {


    /**
     * 解析表结构
     * @return
     */
    @RequestMapping("saxTable")
    public DataRes saxTable(HttpServletRequest request, HttpServletResponse response, Bean bean){
        AutoCodeConfig autoCodeConfig = (AutoCodeConfig) request.getServletContext().getAttribute(ParamConstant.autoCodeConfig);
        bean.setTableName(MyStringUtils.upperCase_(bean.getDataName(),true));
        autoCodeConfig.setBean(bean);
        StartCode startCode = AutoCodeDrive.getStartCode();
        bean = startCode.saxTable(autoCodeConfig);
        return DataRes.success(bean);
    }
    /**
     * 生成单表代码
     * @return
     */
    @RequestMapping("build")
    public DataRes build(HttpServletRequest request, HttpServletResponse response, Bean bean,String primaryKeys,String allColumnss) throws IOException {
        AutoCodeConfig autoCodeConfig = (AutoCodeConfig) request.getServletContext().getAttribute(ParamConstant.autoCodeConfig);
        if(autoCodeConfig.getGlobalConfig().getWatchMobel()){
            return DataRes.error(ResponseCode.WATCHMOBEL);
        }
        ObjectMapper objectMapper=new ObjectMapper();
        bean.setPrimaryKey(RequestUtils.packBeanColumn(objectMapper.readValue(primaryKeys, List.class)));
        bean.setAllColumns(RequestUtils.packBeanColumn(objectMapper.readValue(allColumnss, List.class)));
        bean.setParentPack(autoCodeConfig.getGlobalConfig().getParentPack());
        autoCodeConfig.setBean(bean);
        StartCode startCode = AutoCodeDrive.getStartCode();
        startCode.build(autoCodeConfig);
        return DataRes.success(bean);
    }
    /**
     * 批量生成单表代码
     * @return
     */
    @RequestMapping("buildBatch")
    public DataRes buildBatch(String col,HttpServletRequest request) throws IOException {
        AutoCodeConfig autoCodeConfig = (AutoCodeConfig) request.getServletContext().getAttribute(ParamConstant.autoCodeConfig);
        if(autoCodeConfig.getGlobalConfig().getWatchMobel()){
            return DataRes.error(ResponseCode.WATCHMOBEL);
        }
        ObjectMapper objectMapper=new ObjectMapper();
        List<Map<String,String>> list = objectMapper.readValue(col, List.class);
        List<TableConfig> tn=new ArrayList<>();
        for (Map<String, String> stringStringMap : list) {
            TableConfig tableConfig=new TableConfig();
            tableConfig.setDataName(stringStringMap.get("dataName"));
            tableConfig.setAliasName(stringStringMap.get("aliasName"));
            tn.add(tableConfig);
        }
        autoCodeConfig.getGlobalConfig().setTableNames(tn);
        StartCode startCode = AutoCodeDrive.getStartCode();
        startCode.start(autoCodeConfig);
        return DataRes.success(1);
    }




}
