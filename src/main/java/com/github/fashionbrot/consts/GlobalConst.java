package com.github.fashionbrot.consts;

import com.github.fashionbrot.response.Response;

public class GlobalConst {

    public final static String HTTP_SCHEME ="http://";

    public final static String HTTPS_SCHEME = "https://";

    public static final String ENCODE_UTF8 ="UTF-8";

    public static final String NAME = "mybatis-generate";


    public static final String CACHE_PATH = "mybatis.generate.cache.path";
    public static final String CACHE_FILE_NAME = "mybatis.generate.cache.file-name";

    public static final Response vo = Response.success(null);
}
