package com.zengtengpeng.ui.utils;

import com.zengtengpeng.jdbc.bean.BeanColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestUtils {

    public static List<BeanColumn> packBeanColumn(List<Map> list){
        List<BeanColumn> beanColumns=new ArrayList<>();
        list.forEach(t->{
            BeanColumn beanColumn=new BeanColumn();
            beanColumn.setJdbcName(t.get("jdbcName").toString());
            beanColumn.setBeanName(t.get("beanName").toString());
            beanColumn.setJdbcType(t.get("jdbcType").toString());
            beanColumn.setBeanType(t.get("beanType").toString());
            beanColumn.setNullable(Boolean.valueOf(t.get("nullable").toString()));
            beanColumn.setLength(t.get("length").toString());
            beanColumn.setIdentity(Boolean.valueOf(t.get("identity").toString()));
            beanColumn.setDefaultValue(t.get("defaultValue")+"");
            beanColumn.setJson(Boolean.valueOf(t.get("json").toString()));
            beanColumn.setRemarks(t.get("remarks").toString());
            beanColumn.setKey(Boolean.valueOf(t.get("key").toString()));
            beanColumns.add(beanColumn);
        });
        return beanColumns;
    }
}
