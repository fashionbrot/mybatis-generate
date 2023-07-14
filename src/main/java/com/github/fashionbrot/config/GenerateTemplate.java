package com.github.fashionbrot.config;

import lombok.Data;

/**
 * @author fashionbrot
 */
@Data
public class GenerateTemplate {

    private String enable;
    private String templatePath;
    private String outFilePath;
    private String outFileName;
    private String outFileSuffix;

}
