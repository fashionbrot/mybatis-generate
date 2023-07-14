package com.github.fashionbrot.mapper;


import com.github.fashionbrot.entity.ColumnEntity;
import com.github.fashionbrot.entity.TableEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface BaseMapper {

    /**
     * 获取数据库表
     * @param tableName 查询条件
     * @return
     */
    List<TableEntity> tableList(String tableName);
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
    boolean isKeyIdentity(ColumnEntity columnEntity);

    List<String> getKeyWords();

}
