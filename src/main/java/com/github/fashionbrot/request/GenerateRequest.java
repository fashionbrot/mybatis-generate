package com.github.fashionbrot.request;

import com.github.fashionbrot.enums.DatabaseEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerateRequest {


    private String databaseName;
    private String tableName;

    private String selectTableNames;
    private String excludePrefix;
    private String author;

    /**
     * 输出类型 zip  local
     */
    private String outType;
    /**
     * 编译类型
     */
    private String compileType;
    /**
     * 输出总路径
     */
    private String out;
    /**
     * 包路径
     */
    private String packageOut;

    /**
     * java source set 如：src/main/java
     */
    private String sourceSetJava;
    /**
     * resources source set 如：src/main/resources
     */
    private String sourceSetResources;

    private Boolean controllerEnable;
    private String controllerOut;
    private String controllerSuffix;

    private Boolean serviceEnable;
    private String serviceOut;
    private String serviceSuffix;

    private Boolean serviceImplEnable;
    private String serviceImplOut;
    private String serviceImplSuffix;

    private Boolean mapperEnable;
    private String mapperOut;
    private String mapperSuffix;

    private Boolean mapperXmlEnable;
    private String mapperXmlOut;
    private String mapperXmlSuffix;

    private Boolean entityEnable;
    private String entityOut;
    private String entitySuffix;

    private Boolean requestEnable;
    private String requestOut;
    private String requestSuffix;

    private Boolean responseEnable;
    private String responseOut;
    private String responseSuffix;

    private String responseClassName;

    /**
     * 接口权限名称
     */
    private Boolean permissionEnable;
    /**
     * 权限包路径
     */
    private String permissionOut;
    /**
     * 权限ClassName
     */
    private String permissionClassName;


    /**
     * 是否生成 表之外的固定文件
     */
    private Boolean basicEnable;



    /**
     * 插入表 填充字段 多个逗号分割
     */
    private String fieldInsertFillNames;
    /**
     * 更新表 填充字段 多个逗号分割
     */
    private String fieldUpdateFillNames;
    /**
     * 如果是 DateTime 类型增加
     */
    private String dateTimeFormatValue;

    /**
     * 如果是 Date 类型
     */
    private String dateFormatValue;
    /**
     * 如果是 time 类型
     */
    private String timeFormatValue;
    /**
     * 逻辑删除默认值
     */
    private String tableLogicValue;
    /**
     * 逻辑删除”删除值“默认值
     */
    private String tableLogicDeleteValue;
    /**
     * 乐观锁标记字段
     */
    private String versionFieldName;
    /**
     * 删除标记字段
     */
    private String deleteFieldName;
    /**
     * 实体类 是否序列化
     */
    private Boolean serialVersionUIDEnable;
    /**
     * 是否开启 swagger2
     */
    private Boolean swagger2Enable;
    /**
     * 是否开启 swagger3
     */
    private Boolean swagger3Enable;
    /**
     * 是否启用 PageHelper
     */
    private Boolean pageHelperEnable;
    /**
     * 是否启用 mybatis-plus Page
     */
    private Boolean mybatisPlusPageEnable;
    /**
     * 是否启用 lombok
     */
    private Boolean lombokEnable;

    /**
     * 是否启用 mybatis-plus
     */
    private Boolean mybatisPlusEnable;

    /**
     * 是否生成自定义 PageList 接口
     */
    private Boolean customPageListInterfaceEnable;

    /**
     * 是否生成自定义 list 接口
     */
    private Boolean customListInterfaceEnable;
    /**
     * 是否生成自定义 Insert 接口
     */
    private Boolean customInsertInterfaceEnable;

    /**
     * 是否生成自定义 UpdateById 接口
     */
    private Boolean customUpdateByIdInterfaceEnable;

    /**
     * 是否生成自定义 deleteById 接口
     */
    private Boolean customDeleteByIdInterfaceEnable;
    /**
     * 是否生成自定义 deleteByIds 接口
     */
    private Boolean customDeleteByIdsInterfaceEnable;

    /**
     * 是否生成自定义 selectById 接口
     */
    private Boolean customSelectByIdInterfaceEnable;


    /**
     * 是否生成 mapper inserts
     */
    private Boolean mapperXmlInsertsEnable;
    /**
     * 是否生成 mapper insert
     */
    private Boolean mapperXmlInsertEnable;
    /**
     * 是否生成 mapper update
     */
    private Boolean mapperXmlUpdateByIdEnable;
    /**
     * 是否生成 mapper delete
     */
    private Boolean mapperXmlDeleteByIdEnable;
    /**
     * 是否生成 mapper delete
     */
    private Boolean mapperXmlSelectByIdEnable;

    /**
     *  mapper xml 中是否使用别名
     *  未使用别名<resultMap type="com.github.fashionbrot.UserEntity" id="userMap">
     *  使用别名后<resultMap type="userEntity" id="userMap">
     */
    private Boolean mapperXmlAliasEnable;


    private Boolean hasBigDecimal;

    private DatabaseEnum databaseEnum;

    private String databaseType;
}
