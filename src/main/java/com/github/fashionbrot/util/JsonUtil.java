package com.github.fashionbrot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fashionbrot.common.util.ObjectUtil;
import com.github.fashionbrot.config.GenerateTemplate;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@Slf4j
public class JsonUtil {

    private static final String CHARSET="UTF-8";

    static ObjectMapper objectMapper = new ObjectMapper();

    public static  <T> T parseObject(String json,Class<T> clazz){
        if (ObjectUtil.isEmpty(json)){
            return null;
        }
        T t = null;
        try {
            t = objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> List<T> parseArray(Class<T> clazz,String content)  {
        List<T> ts = null;
        try {
            ts = objectMapper.readValue(content, new TypeReference<List<T>>() { });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ts;
    }

    public static <T> List<T> parseArray(String content,TypeReference<List<T>> typeReference)  {
        List<T> ts = null;
        try {
            ts =  objectMapper.readValue(content, typeReference);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ts;
    }

    public static <T> List<T> parseInputStream(Class<T> clazz , InputStream stream){
        List<T> ts = null;
        try {
            ts = objectMapper.readValue(stream, new TypeReference<List<T>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ts;
    }

    public static <T> List<T> parseInputStream(InputStream stream,TypeReference<List<T>> typeReference){
        List<T> ts = null;
        try {
            ts = objectMapper.readValue(stream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ts;
    }

    public static String toString(Object obj){
        String s = null;
        try {
            s = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return s;
    }


    public static void main(String[] args) {
        System.out.println(toString(new GenerateTemplate()));

        String json="{\"enable\":null,\"templatePath\":null,\"outFilePath\":null,\"outFileName\":null,\"outFileSuffix\":null}";
        GenerateTemplate generateTemplate = parseObject(json, GenerateTemplate.class);
        System.out.println(toString(generateTemplate));
        String jsonArray="[{\"enable\":null,\"templatePath\":null,\"outFilePath\":null,\"outFileName\":null,\"outFileSuffix\":null}]";
        List<GenerateTemplate> generateTemplates = parseArray(GenerateTemplate.class, jsonArray);
        System.out.println(toString(generateTemplates));
    }


}
