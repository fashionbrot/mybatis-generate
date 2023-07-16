package com.github.fashionbrot.service;

import com.github.fashionbrot.entity.ColumnEntity;
import com.github.fashionbrot.entity.TableEntity;
import com.github.fashionbrot.request.GenerateRequest;

import java.util.List;

/**
 * @author fashionbrot
 */
public interface DatasourceTypeServer {

    /**
     * 获取数据库表
     * @return
     */
    List<TableEntity> tableList(GenerateRequest request);
    /**
     * 获取表对象
     * @param tableName 查询条件
     * @return
     */
    TableEntity queryTable(String tableName);
    /**
     * 获取表的列集合
     * @param tableName 查询条件
     * @return
     */
    List<ColumnEntity> queryColumns(String tableName);

    /**
     * 是否是主键
     * @return
     */
    default boolean isKeyIdentity(ColumnEntity columnEntity){
        return false;
    }

    /**
     * 关键字格式化
     * @param columnName
     * @return
     */
    default String formatColumn(String columnName){
        return columnName;
    }
}
