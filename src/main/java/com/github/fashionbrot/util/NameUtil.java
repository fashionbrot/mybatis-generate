package com.github.fashionbrot.util;


public class NameUtil {

    public static String captureName(String str) {
        if (ObjectUtil.isNotEmpty(str)) {
            // 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
            char[] cs = str.toCharArray();
            cs[0] -= 32;
            return String.valueOf(cs);
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(captureName("abc"));
    }

}
