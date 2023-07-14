package com.github.fashionbrot.util;

import com.github.fashionbrot.query.Query;

import java.lang.reflect.Field;

public class MethodUtil {


    public static Object getFieldValue(Object object, Field field,Class type){
        if (type==String.class || type== CharSequence.class){
            try {
                //打开私有访问
                field.setAccessible(true);
                return field.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }else if (type == Boolean.class || type==boolean.class){
            try {
                //打开私有访问
                field.setAccessible(true);
                return field.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static Query newInstance(Class constraint){
        try {
            return (Query) constraint.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
