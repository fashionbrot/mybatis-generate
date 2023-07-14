package com.github.fashionbrot.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.fashionbrot.entity.ColumnEntity;
import com.github.fashionbrot.entity.TableEntity;
import com.github.fashionbrot.enums.DatabaseEnum;
import com.github.fashionbrot.exception.MybatisGenerateException;
import com.github.fashionbrot.mapper.BaseMapper;
import com.github.fashionbrot.query.Query;
import com.github.fashionbrot.query.QueryHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fashionbrot
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultDatabaseTypeServiceImpl implements DatasourceTypeServer {

    final DruidDataSource dataSource;
    final BaseMapper baseMapper;


    private Query query;


    @Override
    public List<TableEntity> tableList(String tableName) {

        initQuery();
        return baseMapper.tableList(query.tablesListSql(tableName));
    }

    @Override
    public TableEntity queryTable(String tableName) {

        initQuery();
        return baseMapper.queryTable(query.tablesSql(tableName));
    }

    @Override
    public List<ColumnEntity> queryColumns(String tableName) {

        initQuery();
        return baseMapper.queryColumns(query.tableFieldsSql(tableName));
    }

    @Override
    public boolean isKeyIdentity(ColumnEntity columnEntity) {

        initQuery();
        return query.isKeyIdentity(columnEntity);
    }

    @Override
    public String formatColumn(String columnName) {

        initQuery();
        return query.formatColumn(columnName);
    }


    public void initQuery(){
        String url =  dataSource.getUrl();
        DatabaseEnum databaseEnum = DatabaseEnum.getDatabase(url);
        if (databaseEnum==null){
            MybatisGenerateException.throwMsg("不支持当前数据库：" + dataSource.getDriverClassName());
        }
        this.query = QueryHelper.getQuery(databaseEnum.getDb());
        if (this.query==null){
            MybatisGenerateException.throwMsg( databaseEnum.getDb()+" 未实现Query接口");
        }
    }
}
