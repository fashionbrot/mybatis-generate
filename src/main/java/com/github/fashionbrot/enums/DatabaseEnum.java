package com.github.fashionbrot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DatabaseEnum {


    MYSQL("mysql", ":mysql:", "MySql数据库"),
    MARIADB("mariadb", ":mariadb:", "MariaDB数据库"),
    ORACLE("oracle", ":oracle:", "Oracle11g及以下数据库(高版本推荐使用ORACLE_NEW)"),
    DB2("db2", ":oracle:", "DB2数据库"),
    H2("h2", ":db2:", "H2数据库"),
    SQLITE("sqlite", ":sqlite:", "SQLite数据库"),
    POST_GRE_SQL("postgresql", ":postgresql:", "Postgre数据库"),
    SQL_SERVER("sqlserver", ":sqlserver:", "SQLServer数据库"),
    DM("dm", ":dm:", "达梦数据库"),
    CLICK_HOUSE("clickhouse", ":clickhouse:", "clickhouse 数据库");


    private final String db;
    private final String keywords;
    private final String desc;



    public static DatabaseEnum getDatabase(String url){
        for (DatabaseEnum type : DatabaseEnum.values()) {
            if (url.contains(type.getKeywords())){
                return type;
            }
        }
        return null;
    }

}
