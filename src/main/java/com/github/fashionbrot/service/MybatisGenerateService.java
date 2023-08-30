package com.github.fashionbrot.service;


import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.fashionbrot.common.util.FileUtil;
import com.github.fashionbrot.common.util.GenericTokenUtil;
import com.github.fashionbrot.common.util.ObjectUtil;
import com.github.fashionbrot.config.GenerateOut;
import com.github.fashionbrot.config.GenerateTemplate;
import com.github.fashionbrot.entity.ColumnEntity;
import com.github.fashionbrot.entity.TableEntity;
import com.github.fashionbrot.enums.DatabaseEnum;
import com.github.fashionbrot.exception.MybatisGenerateException;
import com.github.fashionbrot.request.GenerateRequest;
import com.github.fashionbrot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MybatisGenerateService {

    final DruidService druidService;
    final DatasourceTypeServer baseMapper;
    final DatabaseService databaseService;
    final DataTypeService dataTypeService;

    final TemplateService templateService;

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

            List<GenerateOut> generateOutList = getGenerateOutList(velocityContext, tableName);
            if (ObjectUtil.isNotEmpty(generateOutList)){
                generateOutList.forEach(template->{
                    FileUtil.writeFile(new File(template.getFileFullPath()),template.getTemplateValue().toString());
                });
            }
        }

        List<GenerateOut> fixedGenerateOutList = getFixedGenerateOutList(velocityContext);
        if (ObjectUtil.isNotEmpty(fixedGenerateOutList)){
            fixedGenerateOutList.forEach(template->{
                FileUtil.writeFile(new File(template.getFileFullPath()),template.getTemplateValue().toString());
            });
        }

    }


    private List<GenerateOut> getGenerateOutList(VelocityContext velocityContext,String tableName){

        List<GenerateOut> generateOutList= new ArrayList<>();
        VelocityContext context = initTableColumn(velocityContext, tableName);
        Map<String, Object> contextMap = transform(context);
        List<GenerateTemplate> templateList = templateService.getTemplate().stream().filter(m-> !ObjectUtil.isBoolean(m.getSingle())).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(templateList)){
            for (int i = 0; i < templateList.size(); i++) {
                GenerateTemplate t = templateList.get(i);
                GenerateTemplate template = new GenerateTemplate();
                BeanUtils.copyProperties(t,template);

                if (!ObjectUtil.parseBoolean(GenericTokenUtil.parse(template.getEnable(), contextMap))){
                    continue;
                }

                template.setTemplatePath( GenericTokenUtil.parse(template.getTemplatePath(), contextMap));
                template.setOutFilePath( replaceAll(GenericTokenUtil.parse(template.getOutFilePath(), contextMap)));
                template.setOutFileName(replaceAll(GenericTokenUtil.parse(template.getOutFileName(), contextMap)));
                template.setOutFileSuffix(GenericTokenUtil.parse(template.getOutFileSuffix(), contextMap));

                StringWriter templateValue = getTemplateValue(template.getTemplatePath(), context);
                String fileFullPath = getFileFullPath(template);

                generateOutList.add(GenerateOut.builder()
                                .templateValue(templateValue)
                                .fileFullPath(fileFullPath)
                        .build());
            }
        }

        return generateOutList;
    }



    private List<GenerateOut> getFixedGenerateOutList(VelocityContext context){
        List<GenerateOut> generateOutList= new ArrayList<>();
        Map<String, Object> contextMap = transform(context);
        String compileType = (String)contextMap.get("compileType");
        List<GenerateTemplate> templateList = templateService.getTemplate().stream().filter(m-> ObjectUtil.isBoolean(m.getSingle())).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(templateList)){
            for (int i = 0; i < templateList.size(); i++) {
                GenerateTemplate t = templateList.get(i);
                GenerateTemplate template = new GenerateTemplate();
                BeanUtils.copyProperties(t,template);

                if (!ObjectUtil.parseBoolean(GenericTokenUtil.parse(template.getEnable(), contextMap))){
                    continue;
                }
                if ("gradle".equals(compileType)){
                    if ("pom".equals(template.getOutFileName()) && ".xml".equals(template.getOutFileSuffix()) ){
                        continue;
                    }
                }
                if ("maven".equals(compileType)){
                    if ("build".equals(template.getOutFileName()) && ".gradle".equals(template.getOutFileSuffix())) {
                        continue;
                    }
                    if ("settings".equals(template.getOutFileName()) && ".gradle".equals(template.getOutFileSuffix())){
                        continue;
                    }
                }

                template.setTemplatePath( GenericTokenUtil.parse(template.getTemplatePath(), contextMap));
                template.setOutFilePath( replaceAll(GenericTokenUtil.parse(template.getOutFilePath(), contextMap)));
                template.setOutFileName(replaceAll(GenericTokenUtil.parse(template.getOutFileName(), contextMap)));
                template.setOutFileSuffix(GenericTokenUtil.parse(template.getOutFileSuffix(), contextMap));

                StringWriter templateValue = getTemplateValue(template.getTemplatePath(), context);
                String fileFullPath = getFileFullPath(template);

                generateOutList.add(GenerateOut.builder()
                        .templateValue(templateValue)
                        .fileFullPath(fileFullPath)
                        .build());
            }
        }
        return generateOutList;
    }


    public String getFileFullPath(GenerateTemplate template){
        return template.getOutFilePath()+ File.separator+ template.getOutFileName()+template.getOutFileSuffix();
    }

    private StringWriter getTemplateValue(String templatePath, VelocityContext velocityContext) {
        StringWriter sw = new StringWriter();
        Template tpl = Velocity.getTemplate(templatePath, "UTF-8");
        tpl.merge(velocityContext, sw);
        return sw;
    }

    public Map<String,Object> transform(VelocityContext context){
        String[] keys = context.getKeys();
        Map<String,Object> map=new HashMap<>();
        if (ObjectUtil.isNotEmpty(keys)){
            for(String key: keys){
                Object value = context.get(key);
                map.put(key,value);
            }
            return map;
        }
        return map;
    }

    public VelocityContext initTableColumn(VelocityContext context,String tableName){
        context.put("tableName",tableName);
        TableEntity tableEntity = baseMapper.queryTable(tableName);
        if (tableEntity==null){
            MybatisGenerateException.throwMsg(tableName+"表不存在，请刷新重试");
        }
        context.put("tableNameDescription",tableEntity.getComments());
        String excludePrefix = ObjectUtil.formatString(context.get("excludePrefix"));

        String className = tableToJava(tableName, excludePrefix);
        context.put("className",className);
        tableEntity.setClassName(className);
        tableEntity.setVariableClassName(StringUtils.uncapitalize(className));
        context.put("variableClassName",tableEntity.getVariableClassName());

        List<ColumnEntity> columns = baseMapper.queryColumns(tableName);
        //解析主键、数据库关键字、属性转换
        setDataType(context,tableEntity, columns);
        tableEntity.setColumns(columns);
        //Table 主键类型
        context.put("primaryKeyType",tableEntity.getPrimaryKeyType());
        //Table entity 是否需要序列化
        Boolean serialVersionUIDEnable = (Boolean) context.get("serialVersionUIDEnable");
        if (ObjectUtil.isBoolean(serialVersionUIDEnable)){
            context.put("serialVersionUID",IdWorker.getId());
        }
        setMapperColumn(context, tableEntity);
        context.put("tableFieldList",tableEntity.getColumns());
        context.put("requestMappingPath",tableEntity.getVariableClassName());

        return context;
    }

    private void setMapperColumn(VelocityContext context, TableEntity tableEntity) {
        StringBuilder allColumn = new StringBuilder();
        StringBuilder allColumnAlias = new StringBuilder();
        for (ColumnEntity c : tableEntity.getColumns()) {
            if (ObjectUtil.isNotEmpty(allColumn.toString())) {
                allColumn.append(",").append(c.getColumnName());
                allColumnAlias.append(",en.").append(c.getColumnName());
            } else {
                allColumn.append(c.getColumnName());
                allColumnAlias.append("en." + c.getColumnName());
            }
        }
        context.put("allColumn",allColumn.toString());
        context.put("allColumnAlias",allColumnAlias.toString());
    }


    private void setDataType(VelocityContext context,TableEntity tableEntity, List<ColumnEntity> columns) {
        String  deleteFieldName = ObjectUtil.formatString(context.get("deleteFieldName"));
        String  versionFieldName = ObjectUtil.formatString(context.get("versionFieldName"));

        String  fieldInsertFillNames = ObjectUtil.formatString(context.get("fieldInsertFillNames"));
        String  fieldUpdateFillNames = ObjectUtil.formatString(context.get("fieldUpdateFillNames"));


        for(ColumnEntity columnEntity: columns){

            setFieldInsertFill(columnEntity,fieldInsertFillNames);
            setFieldUpdateFill(columnEntity,fieldUpdateFillNames);
            //列名转换成Java属性名
            String attrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setAttrName(attrName);
//            columnEntity.setVariableAttrName(NameUtil.capitalizeFirstLetter(attrName));
            columnEntity.setVariableAttrName((attrName));

            columnEntity.setColumnNameXmlUse(columnEntity.getColumnName());

            if (attrName.equals(deleteFieldName)){
                columnEntity.setDeleteLogic(true);
            }
            if (attrName.equals(versionFieldName)){
                columnEntity.setVersionLogic(true);
            }
            columnEntity.setGetSetName(NameUtil.capitalizeFirstLetter(attrName));

            columnEntity.setColumnName(baseMapper.formatColumn(columnEntity.getColumnName()));

            String attrType = dataTypeService.getProperty(columnEntity.getDataType(), "unknowType");
            columnEntity.setAttrType(attrType);
            if (attrType.equalsIgnoreCase("BigDecimal" )) {
                context.internalPut("hasBigDecimal",true);
            }
            if (baseMapper.isKeyIdentity(columnEntity)){
                columnEntity.setPrimaryKey(true);
                tableEntity.setPrimaryKeyType(attrType);
            }


        }
    }

    public void setFieldInsertFill(ColumnEntity columnEntity,String fieldInsertFillNames){
        if (ObjectUtil.isNotEmpty(fieldInsertFillNames)){
            Optional<String> first = Arrays.stream(fieldInsertFillNames.split(",")).filter(m -> m.equalsIgnoreCase(columnEntity.getColumnName())).findFirst();
            if (first.isPresent()){
                columnEntity.setInsertFill(true);
            }
        }
    }
    public void setFieldUpdateFill(ColumnEntity columnEntity,String fieldUpdateFillNames){
        if (ObjectUtil.isNotEmpty(fieldUpdateFillNames)){
            Optional<String> first = Arrays.stream(fieldUpdateFillNames.split(",")).filter(m -> m.equalsIgnoreCase(columnEntity.getColumnName())).findFirst();
            if (first.isPresent()){
                columnEntity.setUpdateFill(true);
            }
        }
    }

    /**
     * 表名转换成Java类名
     */
    public static String tableToJava(String tableName, String tablePrefix) {
        if(ObjectUtil.isNotEmpty(tablePrefix)){
            tableName = tableName.replaceFirst(tablePrefix, "");
        }
        String s = columnToJava(tableName);
        return NameUtil.capitalizeFirstLetter(s);
    }


    private static Pattern linePattern = Pattern.compile("_(\\w)");
    /** 下划线转驼峰 */
    public static String columnToJava(String str) {
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


    private String replaceAll(String str){
        return str.replaceAll("\\.", Matcher.quoteReplacement(File.separator))
                .replaceAll("/",Matcher.quoteReplacement(File.separator))
                .replaceAll("\\\\",Matcher.quoteReplacement(File.separator));
    }



}
