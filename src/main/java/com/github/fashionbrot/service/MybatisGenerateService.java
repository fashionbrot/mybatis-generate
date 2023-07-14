package com.github.fashionbrot.service;


import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.fashionbrot.config.GenerateOut;
import com.github.fashionbrot.config.GenerateTemplate;
import com.github.fashionbrot.config.VmConfig;
import com.github.fashionbrot.entity.ColumnEntity;
import com.github.fashionbrot.entity.TableEntity;
import com.github.fashionbrot.enums.DatabaseEnum;
import com.github.fashionbrot.exception.MybatisGenerateException;
import com.github.fashionbrot.request.GenerateRequest;
import com.github.fashionbrot.util.FileUtil;
import com.github.fashionbrot.util.GenericTokenUtil;
import com.github.fashionbrot.util.MethodUtil;
import com.github.fashionbrot.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MybatisGenerateService {

    final DruidService druidService;
    final DatasourceTypeServer baseMapper;
    final DatabaseService databaseService;
    final DataTypeService dataTypeService;

    final VmConfig vmConfig;

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
                generateOutList.forEach(m->{
                    FileUtil.createFile(m.getFileFullPath(),m.getTemplateValue().toString());
                });
            }

        }
    }


    private List<GenerateOut> getGenerateOutList(VelocityContext velocityContext,String tableName){

        List<GenerateOut> generateOutList= new ArrayList<>();
        VelocityContext context = initTableColumn(velocityContext, tableName);
        Map<String, Object> contextMap = transform(context);
        List<GenerateTemplate> vms = vmConfig.getVms();
        if (ObjectUtil.isNotEmpty(vms)){
            for (int i = 0; i < vms.size(); i++) {
                GenerateTemplate template = vms.get(i);

                String enableVariable = GenericTokenUtil.parse(template.getEnable(), contextMap);
                if (!ObjectUtil.parseBoolean(enableVariable)){
                    continue;
                }

                template.setTemplatePath( GenericTokenUtil.parse(template.getTemplatePath(), contextMap));
                template.setOutFilePath( GenericTokenUtil.parse(template.getOutFilePath(), contextMap));
                template.setOutFileName(GenericTokenUtil.parse(template.getOutFileName(), contextMap));
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
        String excludePrefix = ObjectUtil.formatString(context.get("excludePrefix"));

        String className = tableToJava(tableName, excludePrefix);
        tableEntity.setClassName(className);
        tableEntity.setVariableClassName(StringUtils.uncapitalize(className));


        List<ColumnEntity> columns = baseMapper.queryColumns(tableName);
        setDataType(context,tableEntity, columns);
        tableEntity.setColumns(columns);
        //description

        Boolean serialVersionUIDEnable = (Boolean) context.get("serialVersionUIDEnable");
        if (ObjectUtil.isBoolean(serialVersionUIDEnable)){
            context.put("serialVersionUID",IdWorker.getId());
        }
        setMapperColumn(context, tableEntity);
        context.put("tableField",tableEntity);

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

        for(ColumnEntity columnEntity: columns){
            //列名转换成Java属性名
            String attrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setAttrName(attrName);
            columnEntity.setVariableAttrName(ObjectUtil.uncapitalize(attrName));

            columnEntity.setColumnNameXmlUse(columnEntity.getColumnName());

            columnEntity.setColumnName(baseMapper.formatColumn(columnEntity.getColumnName()));

            String attrType = dataTypeService.getProperty(columnEntity.getDataType(), "unknowType");
            columnEntity.setAttrType(attrType);
            if (attrType.equalsIgnoreCase("BigDecimal" )) {
                context.internalPut("hasBigDecimal",true);
            }
            if ( baseMapper.isKeyIdentity(columnEntity)){
                tableEntity.setPrimaryKeyColumnEntity(columnEntity);
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
        return columnToJava(tableName);
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





}
