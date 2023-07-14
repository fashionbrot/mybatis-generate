package com.github.fashionbrot.request;

import com.github.fashionbrot.enums.DatabaseEnum;
import lombok.Data;


@Data
public class GenerateRequest {


    private String databaseName;
    private String tableName;

    /**
     * zip  local
     */
    private String outType;

    private String packageOut;

    private String out;

    private String controllerOut;
    private String controllerSuffix;

    private String serviceOut;
    private String serviceSuffix;

    private String serviceImplOut;
    private String serviceImplSuffix;

    private String mapperOut;
    private String mapperSuffix;

    private String mapperXmlOut;
    private String mapperXmlSuffix;

    private String entityOut;
    private String entitySuffix;


    private String requestOut;
    private String requestSuffix;

    private String responseOut;
    private String responseSuffix;


    private String responseClassName;

    private String permissionOut;
    private String permissionClassName;
    private Boolean permissionEnable;

    private String selectTableNames;
    private String excludePrefix;
    private String author;






    /**
     * 是否生成 表之外的固定文件
     */
    private String fixed;

    private String compileType;



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
    private Boolean generateCustomPageListInterfaceEnable;

    /**
     * 是否生成自定义 Insert 接口
     */
    private Boolean generateCustomInsertInterfaceEnable;

    /**
     * 是否生成自定义 Update 接口
     */
    private Boolean generateCustomUpdateInterfaceEnable;

    /**
     * 是否生成自定义 Update 接口
     */
    private Boolean generateCustomDeleteInterfaceEnable;


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
    private Boolean mapperXmlUpdateEnable;
    /**
     * 是否生成 mapper delete
     */
    private Boolean mapperXmlDeleteEnable;

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
