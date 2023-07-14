package com.github.fashionbrot.service;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.fashionbrot.config.DatasourceConfig;
import com.github.fashionbrot.entity.ColumnEntity;
import com.github.fashionbrot.entity.TableEntity;
import com.github.fashionbrot.enums.DatabaseEnum;
import com.github.fashionbrot.exception.MybatisGenerateException;
import com.github.fashionbrot.mapper.BaseMapper;
import com.github.fashionbrot.request.GenerateRequest;
import com.github.fashionbrot.util.MethodUtil;
import com.github.fashionbrot.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MybatisGenerateService {

    final DruidService druidService;
    final BaseMapper baseMapper;
    final DatabaseService databaseService;
    final DataTypeService dataTypeService;


    public void generatorCode(GenerateRequest request) {



        String[] tableNames=request.getSelectTableNames().split(",");
        if (ObjectUtil.isEmpty(tableNames)){
            return;
        }

        druidService.reloadDatabase(request.getDatabaseName());
        DatabaseEnum databaseEnum = databaseService.getDatabaseEnum();

        request.setDatabaseType(databaseEnum.getDb());

        VelocityContext velocityContext = initTemplate(request);

        for(String tableName : tableNames){
            velocityContext.internalPut("tableName",tableName);

            Map<String, StringWriter> fileMap = generator(req,tableName);
            if (CollectionUtils.isNotEmpty(fileMap)){
                for(Map.Entry<String,StringWriter> map: fileMap.entrySet()){

                    createFile(map.getKey(),map.getValue().toString());
                }
            }
        }

        if ("on".equals(request.getFixed())){
            Map<String, StringWriter> fixedMap = generateFixed(req);
            if (CollectionUtils.isNotEmpty(fixedMap)){
                for(Map.Entry<String,StringWriter> map: fixedMap.entrySet()){
                    createFile(map.getKey(),map.getValue().toString());
                }
            }
        }


    }


    private void getTemplateTxt(VelocityContext velocityContext,String tableName){

        TableEntity tableEntity = baseMapper.queryTable(tableName);
        if (tableEntity==null){
            MybatisGenerateException.throwMsg(tableName+"表不存在，请刷新重试");
        }


    }

    public void initTableColumn(VelocityContext context,String tableName){

        TableEntity tableEntity = baseMapper.queryTable(tableName);
        if (tableEntity==null){
            MybatisGenerateException.throwMsg(tableName+"表不存在，请刷新重试");
        }
        String excludePrefix = (String)context.get("excludePrefix");

        String className = tableToJava(tableName, excludePrefix);
        tableEntity.setClassName(className);
        tableEntity.setVariableClassName(StringUtils.uncapitalize(className));


        List<ColumnEntity> columns = baseMapper.queryColumns(tableName);
        setDataType(tableEntity, columns,req);
        tableEntity.setColumns(columns);
    }


    private void setDataType(TableEntity tableEntity, List<ColumnEntity> columns,CodeReq req) {


        String keyword = environment.getProperty("mars.quick.keyword");
        String[] keywords = keyword.split(",");

        for(ColumnEntity columnEntity: columns){
            //列名转换成Java属性名
            String attrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setAttrName(attrName);
            columnEntity.setVariableAttrName(StringUtils.uncapitalize(attrName));

            columnEntity.setColumnNameXmlUse(columnEntity.getColumnName());
            if (Arrays.stream(keywords).filter(m-> m.equals(columnEntity.getColumnName())).count()>0){
                columnEntity.setColumnName("`"+columnEntity.getColumnName()+"`");
            }

            //列的数据类型，转换成Java类型
            if("NUMBER".equals(columnEntity.getDataType()) && columnEntity.getDataScale() > 0){
                columnEntity.setAttrType("Double");
            }else if("NUMBER".equals(columnEntity.getDataType()) && columnEntity.getDataPrecision()>14){
                columnEntity.setAttrType("Long");
            }else{
                String attrType = environment.getProperty(columnEntity.getDataType(), "unknowType");
                columnEntity.setAttrType(attrType);
                if (attrType.equalsIgnoreCase("BigDecimal" )) {
                    req.setHasBigDecimal(true);
                }
            }
            //是否主键
            if("PRI".equalsIgnoreCase(columnEntity.getColumnKey()) && tableEntity.getPrimaryKeyColumnEntity() == null){
                tableEntity.setPrimaryKeyColumnEntity(columnEntity);
            }
        }
    }

    /**
     * 表名转换成Java类名
     */
    public static String tableToJava(String tableName, String tablePrefix) {
        if(StringUtils.isNotBlank(tablePrefix)){
            tableName = tableName.replaceFirst(tablePrefix, "");
        }
        return lineToHump(tableName);
    }


    private static Pattern linePattern = Pattern.compile("_(\\w)");
    /** 下划线转驼峰 */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    private VelocityContext initTemplate(GenerateRequest request){

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);

        VelocityContext context = new VelocityContext();
        Field[] fields = request.getClass().getDeclaredFields();
        if (ObjectUtil.isNotEmpty(fields)){
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                Class<?> fieldType = field.getType();
                if (fieldType==String.class || fieldType == CharSequence.class){
                    context.internalPut(field.getName(), MethodUtil.getFieldValue(request,field,fieldType));
                }else if (fieldType== Boolean.class || fieldType==boolean.class){
                    context.internalPut(field.getName(), MethodUtil.getFieldValue(request,field,fieldType));
                }
            }
        }
        return context;
    }




    private VelocityContext addTableTemplate(VelocityContext context,TableEntity tableEntity){
        Map<String, Object> map = new HashMap<>();
        map.put("oldTableName", tableEntity.getTableName());
        // 处理注释
        if(StringUtils.isNotBlank(tableEntity.getComments())){
            map.put("comments", tableEntity.getComments());
            map.put("commentsEntity", tableEntity.getComments());
            map.put("commentsService", tableEntity.getComments());
            map.put("commentsController", tableEntity.getComments());
            map.put("commentsApi", tableEntity.getComments());
            map.put("commentsDao", tableEntity.getComments());
        }
        map.put("pk", tableEntity.getPrimaryKeyColumnEntity());
        map.put("pkAttrType",tableEntity.getPrimaryKeyColumnEntity()!=null?tableEntity.getPrimaryKeyColumnEntity().getAttrType():"String");
        map.put("autoIncrement",tableEntity.getPrimaryKeyColumnEntity()!=null?"auto_increment".equalsIgnoreCase(tableEntity.getPrimaryKeyColumnEntity().getExtra()):false);
        map.put("className", tableEntity.getClassName().replace(captureName(req.getExcludePrefix()),""));
        map.put("variableClassName", tableEntity.getVariableClassName());
        map.put("columns", tableEntity.getColumns());

        map.put("serialVersionUID", IdWorker.getId());

        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        for (ColumnEntity c : tableEntity.getColumns()) {
            if (StringUtils.isNotBlank(sb.toString())) {
                sb.append(",").append(c.getColumnName());
                sb2.append(",a.").append(c.getColumnName());
            } else {
                sb.append(c.getColumnName());
                sb2.append("a." + c.getColumnName());
            }
        }
        map.put("allColumnNames",sb.toString());
        map.put("allColumnNames2",sb2.toString());

        map.put("hasBigDecimal", req.getHasBigDecimal());

        // API接口排序
        if(tableEntity.getCreateTime()!= null){
            map.put("apiSort", StringUtils.substring(String.valueOf(tableEntity.getCreateTime().getTime() + System.currentTimeMillis()), 1, 9));
        }


        if(StringUtils.isNotBlank(tableEntity.getTableName())){
            String tableName = StringUtils.lowerCase(tableEntity.getTableName());
            map.put("pathName", tableName.replace("_","/"));
            map.put("permissionPrefix", tableEntity.getVariableClassName());
            map.put("classServiceImpl",tableEntity.getVariableClassName());
            map.put("requestMappingPath",tableEntity.getVariableClassName());
            // 前端权限标识
            map.put("apiPermission", tableName.replace("_",":"));
//            map.put("vueFileName", className.toLowerCase());
        }
        if (context!=null){
            String[] keys = context.getKeys();
            if (keys!=null && keys.length>0){
                for(String key: keys){
                    Object value = context.get(key);
                    map.put(key,value);
                }
            }
        }

        VelocityContext newContent = new VelocityContext(map);

        return newContent;
    }


}
