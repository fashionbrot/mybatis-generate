package com.github.fashionbrot.util;

import java.lang.reflect.Field;

public class MethodUtil {


    public static Object getFieldValue(Object object, Field field,Class type){
        if (type==String.class || type== CharSequence.class){
            try {
                return field.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }else if (type == Boolean.class || type==boolean.class){
            try {
                return field.getBoolean(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
