package com.github.fashionbrot.util;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;


@Slf4j
public class JsonUtil {

    private static final String CHARSET="UTF-8";

    public static  <T> T parseObject(String json,Class<T> clazz){
        if (ObjectUtil.isEmpty(json)){
            return null;
        }
        T t = null;
        try {
            t= JSON.parseObject(json.getBytes(StandardCharsets.UTF_8), clazz);
        }catch (Exception e){
            log.error("parseObject error json:{} clazz:{}",json,clazz,e);
        }
        return t;
    }

    public static <T> List<T> parseArray(Class<T> clazz,String content)  {
        return JSON.parseArray(content, clazz);
    }


    public static  <T> T parseObjectNon(String json,Class<T> clazz)  {
        if (ObjectUtil.isEmpty(json)){
            return null;
        }
        return JSON.parseObject(json.getBytes(StandardCharsets.UTF_8), clazz);
    }

}
