package com.github.fashionbrot.query;

import com.github.fashionbrot.enums.DatabaseEnum;
import com.github.fashionbrot.util.MethodUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fashionbrot
 */
public class QueryHelper {

    private static Map<String,Query> queryMap = new ConcurrentHashMap<>();

    static {
        putQuery(DatabaseEnum.MYSQL.getDb(),MysqlQuery.class);
        putQuery(DatabaseEnum.ORACLE.getDb(),OracleQuery.class);
        putQuery(DatabaseEnum.SQL_SERVER.getDb(),SqlserverQuery.class);
    }


    public static void putQuery(String dbName ,Class clazz){
        queryMap.put(dbName, MethodUtil.newInstance(clazz));
    }

    public static Query getQuery(String dbName){
        Query query = queryMap.get(dbName);
        return query;
    }
}
