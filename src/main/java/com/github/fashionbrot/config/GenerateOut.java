package com.github.fashionbrot.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.StringWriter;

/**
 * @author fashionbrot
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerateOut {

    private String fileFullPath;

    private StringWriter templateValue;

}
