package com.github.fashionbrot.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class MybatisGenerateException extends RuntimeException  {

    private int code=-1;

    private String msg;


    public MybatisGenerateException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public static void throwMsg(String msg){
        throw new MybatisGenerateException(msg);
    }
}