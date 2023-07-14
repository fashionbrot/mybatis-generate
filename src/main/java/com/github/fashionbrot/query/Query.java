package com.github.fashionbrot.query;

import com.github.fashionbrot.entity.ColumnEntity;
import com.github.fashionbrot.entity.TableEntity;

import java.util.List;

/**
 * @author fashionbrot
 */
public interface Query {

    /**
     * 表信息查询 SQL
     */
    String tablesListSql(String tableName);

    /**
     * 表信息查询 SQL
     */
    String tablesSql(String tableName);

    /**
     * 表字段信息查询 SQL
     */
    String tableFieldsSql(String tableName);


    /**
     * 是否是主键
     * @return
     */
    boolean isKeyIdentity(ColumnEntity columnEntity);

    /**
     * 关键字格式化
     * @param columnName
     * @return
     */
    String formatColumn(String columnName);
}
