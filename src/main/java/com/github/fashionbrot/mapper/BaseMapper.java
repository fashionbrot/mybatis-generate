package com.github.fashionbrot.mapper;


import com.github.fashionbrot.entity.ColumnEntity;
import com.github.fashionbrot.entity.TableEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BaseMapper {

    /**
     * 获取数据库表
     * @param sql 查询条件
     * @return
     */
     List<TableEntity> tableList(String sql);

    /**
     * 获取当前表
     * @param sql
     * @return
     */
     TableEntity queryTable(String sql);
    /**
     * 获取表的列集合
     * @param sql 查询条件
     * @return
     */
    List<ColumnEntity> queryColumns(String sql);


}
