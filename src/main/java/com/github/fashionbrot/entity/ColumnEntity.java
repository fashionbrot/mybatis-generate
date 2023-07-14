package com.github.fashionbrot.entity;

import lombok.Data;


@Data
public class ColumnEntity {
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 字段顺序
     */
    private String columnId;
    /**
     * 列名
     */
    private String columnName;
    /**
     * 列名
     */
    private String columnNameXmlUse;
    /**
     * 列名类型
     */
    private String dataType;
    /**
     * 字段存储长度
     */
    private Integer dataLength;
    /**
     * 字段限制存储长度
     */
    private Integer dataPrecision;
    /**
     * 字段精度
     */
    private Integer dataScale;
    /**
     * 是否可以为Null值
     */
    private String nullable;
    /**
     * 字段key属性
     */
    private String columnKey;
    /**
     * 列名备注
     */
    private String comments;
    /**
     * 属性名称
     */
    private String attrName;
    /**
     * 变量名称
     */
    private String variableAttrName;
    /**
     * 属性类型
     */
    private String attrType;
    /**
     *
     */
    private String extra;

}
