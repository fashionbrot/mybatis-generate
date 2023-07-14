package com.github.fashionbrot.response;



import com.github.fashionbrot.consts.GlobalConst;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> implements Serializable{
    private static final long serialVersionUID = -3655390020082644681L;

    public static final int SUCCESS = 0;
    public static final int FAILED = 1;


    private int code;
    private String msg;
    private T data;


    public static Response fail(String msg){
        return Response.builder().code(FAILED).msg(msg).build();
    }

    public static Response fail(String msg, int code){
        return Response.builder().code(code).msg(msg).build();
    }

    public static<T> Response success(T data){
        return Response.builder().code(SUCCESS).msg("成功").data(data).build();
    }

    public static Response success(){
        return GlobalConst.vo;
    }

}
