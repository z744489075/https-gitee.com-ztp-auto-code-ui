package com.zengtengpeng.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zengtengpeng.autoCode.StartCode;
import com.zengtengpeng.autoCode.config.AutoCodeConfig;
import com.zengtengpeng.autoCode.config.GlobalConfig;
import com.zengtengpeng.common.bean.DataRes;
import com.zengtengpeng.common.bean.ResponseCode;
import com.zengtengpeng.common.drive.AutoCodeDrive;
import com.zengtengpeng.jdbc.bean.Bean;
import com.zengtengpeng.relation.bean.RelationTable;
import com.zengtengpeng.relation.config.RelationConfig;
import com.zengtengpeng.relation.manyToMany.BuildManyToMany;
import com.zengtengpeng.relation.oneToMany.BuildOneToMany;
import com.zengtengpeng.relation.oneToOne.BuildOneToOne;
import com.zengtengpeng.relation.utils.RelationUtils;
import com.zengtengpeng.ui.constant.ParamConstant;
import com.zengtengpeng.ui.utils.RequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 关系controller
 */
@RestController
@RequestMapping("/auto-code-ui/data/relation")
public class RelationController {


    /**
     * 生成一对一
     *
     * @return
     */
    @RequestMapping("buildOneToOne")
    public DataRes buildOneToOne(HttpServletRequest request, HttpServletResponse response, RelationConfig relationConfig,
                                 String primaryKeys, String foreignKeys, String pallColumnss, String fallColumnss) {
        AutoCodeConfig autoCodeConfig = (AutoCodeConfig) request.getServletContext().getAttribute(ParamConstant.autoCodeConfig);
        if(autoCodeConfig.getGlobalConfig().getWatchMobel()){
            return DataRes.error(ResponseCode.WATCHMOBEL);
        }
        buildSimple(autoCodeConfig,relationConfig.getPrimary(),primaryKeys,pallColumnss);
        buildSimple(autoCodeConfig,relationConfig.getForeign(),foreignKeys,fallColumnss);
        autoCodeConfig.getGlobalConfig().setRelationConfig(relationConfig);
        BuildOneToOne buildOneToOne = AutoCodeDrive.getBuildOneToOne();
        buildOneToOne.build(autoCodeConfig);

        return DataRes.success(1);
    }
    /**
     * 生成一对多
     *
     * @return
     */
    @RequestMapping("buildOneToMany")
    public DataRes buildOneToMany(HttpServletRequest request, HttpServletResponse response, RelationConfig relationConfig,
                                 String primaryKeys, String foreignKeys, String pallColumnss, String fallColumnss) {
        AutoCodeConfig autoCodeConfig = (AutoCodeConfig) request.getServletContext().getAttribute(ParamConstant.autoCodeConfig);
        if(autoCodeConfig.getGlobalConfig().getWatchMobel()){
            return DataRes.error(ResponseCode.WATCHMOBEL);
        }
        buildSimple(autoCodeConfig,relationConfig.getPrimary(),primaryKeys,pallColumnss);
        buildSimple(autoCodeConfig,relationConfig.getForeign(),foreignKeys,fallColumnss);
        autoCodeConfig.getGlobalConfig().setRelationConfig(relationConfig);
        BuildOneToMany buildOneToMany = AutoCodeDrive.getBuildOneToMany();
        buildOneToMany.build(autoCodeConfig);

        return DataRes.success(1);
    }
    /**
     * 生成多对多
     *
     * @return
     */
    @RequestMapping("buildManyToMany")
    public DataRes buildManyToMany(HttpServletRequest request, HttpServletResponse response, RelationConfig relationConfig,
                                 String primaryKeys, String foreignKeys, String pallColumnss, String fallColumnss) {
        AutoCodeConfig autoCodeConfig = (AutoCodeConfig) request.getServletContext().getAttribute(ParamConstant.autoCodeConfig);
        if(autoCodeConfig.getGlobalConfig().getWatchMobel()){
            return DataRes.error(ResponseCode.WATCHMOBEL);
        }
        buildSimple(autoCodeConfig,relationConfig.getPrimary(),primaryKeys,pallColumnss);
        buildSimple(autoCodeConfig,relationConfig.getForeign(),foreignKeys,fallColumnss);
        autoCodeConfig.getGlobalConfig().setRelationConfig(relationConfig);
        BuildManyToMany buildManyToMany = AutoCodeDrive.getBuildManyToMany();
        buildManyToMany.build(autoCodeConfig);

        return DataRes.success(1);
    }

    /**
     * 构建单表
     * @param autoCodeConfig
     * @param relationTable
     * @param keys
     * @param allColumnss
     */
    private void buildSimple(AutoCodeConfig autoCodeConfig, RelationTable relationTable, String keys, String allColumnss) {
        try {
            //如果需要生成
            if(relationTable.getGenerate()) {
                ObjectMapper objectMapper = new ObjectMapper();
                Bean bean = new Bean();
                bean.setPrimaryKey(RequestUtils.packBeanColumn(objectMapper.readValue(keys, List.class)));
                bean.setAllColumns(RequestUtils.packBeanColumn(objectMapper.readValue(allColumnss, List.class)));
                String parentPack = autoCodeConfig.getGlobalConfig().getParentPack();
                bean.setParentPack(parentPack);
                relationTable.setExistParentPackage(parentPack);
                bean.setTableName(relationTable.getBeanName());
                bean.setDataName(relationTable.getDataName());
                bean.setTableRemarks(relationTable.getRemark());
                autoCodeConfig.setBean(bean);
                StartCode startCode = AutoCodeDrive.getStartCode();
                startCode.build(autoCodeConfig);
            }
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }
}
