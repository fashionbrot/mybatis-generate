package com.github.fashionbrot.query;

import com.github.fashionbrot.common.util.ObjectUtil;
import com.github.fashionbrot.entity.ColumnEntity;


import java.util.*;

public class PostgresqlQuery extends AbstractQuery{
    @Override
    public String tablesListSql(String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT A.tablename as tableName, obj_description(relfilenode, 'pg_class') AS comments FROM pg_tables A, pg_class B WHERE  A.tablename = B.relname  ");
        if (ObjectUtil.isNotEmpty(tableName)){
            sql.append(" AND A.schemaname like '%"+tableName+"%'");
        }
        return sql.toString();
    }

    @Override
    public String tablesSql(String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT A.tablename as tableName, obj_description(relfilenode, 'pg_class') AS comments FROM pg_tables A, pg_class B WHERE  A.tablename = B.relname  ");
        if (ObjectUtil.isNotEmpty(tableName)){
            sql.append(" AND A.schemaname = '"+tableName+"'");
        }
        return sql.toString();
    }

    @Override
    public String tableFieldsSql(String tableName) {
        String sql= "SELECT A.attname AS columnName,format_type (A.atttypid,A.atttypmod) AS dataType,col_description (A.attrelid,A.attnum) AS comments,\n" +
                "(CASE WHEN (SELECT COUNT (*) FROM pg_constraint AS PC WHERE A.attnum = PC.conkey[1] AND PC.contype = 'p') > 0 THEN 'PRI' ELSE '' END) AS columnKey \n" +
                "FROM pg_class AS C,pg_attribute AS A WHERE A.attrelid='"+tableName+"'::regclass AND A.attrelid= C.oid AND A.attnum> 0 AND NOT A.attisdropped ORDER  BY A.attnum";

        return sql;
    }

    @Override
    public boolean isKeyIdentity(ColumnEntity columnEntity) {
        return "id".equals(columnEntity.getColumnName());
    }

    @Override
    public String formatColumn(String columnName) {
        String upperCase = columnName.toUpperCase(Locale.ENGLISH);
        Optional<String> first = KEY_WORDS.stream().filter(m -> m.equals(upperCase)).findFirst();
        if (first.isPresent()){
            return String.format(formatStyle(),columnName);
        }
        return columnName;
    }


    public String formatStyle() {
        return "\"%s\"";
    }

    private static final List<String> KEY_WORDS = new ArrayList<>(Arrays.asList(
            "ALL",
            "ANALYSE",
            "ANALYZE",
            "AND",
            "ANY",
            "ARRAY",
            "AS",
            "ASC",
            "ASYMMETRIC",
            "AUTHORIZATION",
            "BINARY",
            "BOTH",
            "CASE",
            "CAST",
            "CHECK",
            "COLLATE",
            "COLLATION",
            "COLUMN",
            "CONCURRENTLY",
            "CONSTRAINT",
            "CREATE",
            "CROSS",
            "CURRENT_CATALOG",
            "CURRENT_DATE",
            "CURRENT_ROLE",
            "CURRENT_SCHEMA",
            "CURRENT_TIME",
            "CURRENT_TIMESTAMP",
            "CURRENT_USER",
            "DEFAULT",
            "DEFERRABLE",
            "DESC",
            "DISTINCT",
            "DO",
            "ELSE",
            "END",
            "EXCEPT",
            "FALSE",
            "FETCH",
            "FOR",
            "FOREIGN",
            "FREEZE",
            "FROM",
            "FULL",
            "GRANT",
            "GROUP",
            "HAVING",
            "ILIKE",
            "IN",
            "INITIALLY",
            "INNER",
            "INTERSECT",
            "INTO",
            "IS",
            "ISNULL",
            "JOIN",
            "LATERAL",
            "LEADING",
            "LEFT",
            "LIKE",
            "LIMIT",
            "LOCALTIME",
            "LOCALTIMESTAMP",
            "NATURAL",
            "NOT",
            "NOTNULL",
            "NULL",
            "OFFSET",
            "ON",
            "ONLY",
            "OR",
            "ORDER",
            "OUTER",
            "OVERLAPS",
            "PLACING",
            "PRIMARY",
            "REFERENCES",
            "RETURNING",
            "RIGHT",
            "SELECT",
            "SESSION_USER",
            "SIMILAR",
            "SOME",
            "SYMMETRIC",
            "TABLE",
            "TABLESAMPLE",
            "THEN",
            "TO",
            "TRAILING",
            "TRUE",
            "UNION",
            "UNIQUE",
            "USER",
            "USING",
            "VARIADIC",
            "VERBOSE",
            "WHEN",
            "WHERE",
            "WINDOW",
            "WITH"
    ));

    public static void main(String[] args) {
        String sql =  "SELECT A.attname AS name,format_type (A.atttypid,A.atttypmod) AS type,col_description (A.attrelid,A.attnum) AS comment,\n" +
                "(CASE WHEN (SELECT COUNT (*) FROM pg_constraint AS PC WHERE A.attnum = PC.conkey[1] AND PC.contype = 'p') > 0 THEN 'PRI' ELSE '' END) AS key \n" +
                "FROM pg_class AS C,pg_attribute AS A WHERE A.attrelid='\"%s\"'::regclass AND A.attrelid= C.oid AND A.attnum> 0 AND NOT A.attisdropped ORDER  BY A.attnum";

        System.out.println(sql);
    }
}
