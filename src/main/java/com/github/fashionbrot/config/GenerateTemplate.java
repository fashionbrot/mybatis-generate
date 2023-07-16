package com.github.fashionbrot.config;

import lombok.Data;

/**
 * @author fashionbrot
 */
@Data
public class GenerateTemplate {

    /**
     * 是否开启
     */
    private String enable;
    /**
     * vm 模板路径
     */
    private String templatePath;
    /**
     * 输出文件路径
     */
    private String outFilePath;
    /**
     * 输出文件名
     */
    private String outFileName;
    /**
     * 输出文件后缀如:
     * .java
     * .xml
     */
    private String outFileSuffix;

    /**
     * true 代表 只生成一次的文件
     * false 代表按照数据库表生成
     */
    private Boolean single;
}
