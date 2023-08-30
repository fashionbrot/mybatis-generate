package com.github.fashionbrot.util;

import com.github.fashionbrot.common.util.ObjectUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameUtil {

    /**
     * 将字符串的首字母转换为大写。
     *
     * @param str 要转换的字符串
     * @return 转换后的字符串，如果输入字符串为空则返回空字符串
     */
    public static String capitalizeFirstLetter(String str) {
        if (ObjectUtil.isEmpty(str)) {
           return "";
        }
        // 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
        char[] cs = str.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }


    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 将下划线命名转换为驼峰命名。
     *
     * @param str 要转换的下划线命名字符串
     * @return 转换后的驼峰命名字符串
     */
    public static String columnToJava(String str) {
        if (ObjectUtil.isEmpty(str)){
            return "";
        }
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
