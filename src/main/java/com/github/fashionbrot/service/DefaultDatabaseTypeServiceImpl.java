package com.github.fashionbrot.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.fashionbrot.common.util.ObjectUtil;
import com.github.fashionbrot.entity.ColumnEntity;
import com.github.fashionbrot.entity.TableEntity;
import com.github.fashionbrot.enums.DatabaseEnum;
import com.github.fashionbrot.exception.MybatisGenerateException;
import com.github.fashionbrot.mapper.BaseMapper;
import com.github.fashionbrot.mapper.SqliteMapper;
import com.github.fashionbrot.query.Query;
import com.github.fashionbrot.query.QueryHelper;
import com.github.fashionbrot.request.GenerateRequest;
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
    final DruidService druidService;
    final SqliteMapper sqliteMapper;


    private Query query;


    @Override
    public List<TableEntity> tableList(GenerateRequest request) {
        druidService.reloadDatabase(request.getDatabaseName());

        if (getDatabaseEnum()==DatabaseEnum.SQLITE){
            return sqliteMapper.tableList(request.getTableName());
        }
        initQuery();
        return baseMapper.tableList(query.tablesListSql(request.getTableName()));
    }

    @Override
    public TableEntity queryTable(String tableName) {

        if (getDatabaseEnum()==DatabaseEnum.SQLITE){
            return sqliteMapper.queryTable(tableName);
        }
        initQuery();
        return baseMapper.queryTable(query.tablesSql(tableName));
    }

    @Override
    public List<ColumnEntity> queryColumns(String tableName) {


        if (getDatabaseEnum()==DatabaseEnum.SQLITE){

            List<ColumnEntity> columnEntities = sqliteMapper.queryColumns(tableName);
            return columnEntities;
        }
        initQuery();
        return baseMapper.queryColumns(query.tableFieldsSql(tableName));
    }

    @Override
    public boolean isKeyIdentity(ColumnEntity columnEntity) {


        if (getDatabaseEnum()==DatabaseEnum.SQLITE){
            return sqliteMapper.isKeyIdentity(columnEntity);
        }
        initQuery();
        return query.isKeyIdentity(columnEntity);
    }

    @Override
    public String formatColumn(String columnName) {

        if (getDatabaseEnum()==DatabaseEnum.SQLITE){
            return sqliteMapper.formatColumn(columnName);
        }
        initQuery();
        return query.formatColumn(columnName);
    }


    public void initQuery(){
        DatabaseEnum databaseEnum = getDatabaseEnum();
        this.query = QueryHelper.getQuery(databaseEnum.getDb());
        if (this.query==null){
            MybatisGenerateException.throwMsg( databaseEnum.getDb()+" 未实现Query接口");
        }
    }

    public DatabaseEnum getDatabaseEnum(){
        String url =  dataSource.getUrl();
        DatabaseEnum database = DatabaseEnum.getDatabase(url);
        if (database==null){
            MybatisGenerateException.throwMsg("不支持当前数据库：" + dataSource.getDriverClassName());
        }
        return database;
    }

}
