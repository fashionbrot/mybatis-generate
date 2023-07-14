package com.github.fashionbrot.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.JSON;
import com.github.fashionbrot.consts.GlobalConst;
import com.github.fashionbrot.entity.ColumnEntity;
import com.github.fashionbrot.entity.TableEntity;
import com.github.fashionbrot.exception.MybatisGenerateException;
import com.github.fashionbrot.mapper.BaseMapper;
import com.github.fashionbrot.request.DatabaseRequest;
import com.github.fashionbrot.request.GenerateRequest;
import com.github.fashionbrot.response.PageResponse;
import com.github.fashionbrot.util.FileUtil;
import com.github.fashionbrot.util.JsonUtil;
import com.github.fashionbrot.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DruidService {

    final Environment environment;
    final DruidDataSource druidDataSource;
    final BaseMapper baseMapper;



    public String getPath() {

        String path = environment.getProperty(GlobalConst.CACHE_PATH);
        if (ObjectUtil.isEmpty(path)) {
            path = FileUtil.USER_HOME;
        }
        path = path + File.separator + GlobalConst.NAME + File.separator;
        return path;
    }


    public String getFileName() {
        return environment.getProperty(GlobalConst.CACHE_FILE_NAME);
    }


    public List<DatabaseRequest> getDatabaseList() {
        String path = getPath() + getFileName();

        List<File> files = FileUtil.searchFiles(new File(path), getFileName());
        if (!CollectionUtils.isEmpty(files)) {
            String fileContent = FileUtil.getFileContent(files.get(0));
            if (ObjectUtil.isEmpty(fileContent)) {
                return null;
            }
            return JsonUtil.parseArray(DatabaseRequest.class, fileContent);
        }
        return null;
    }


    public void reload(DatabaseRequest req) {
        if (ObjectUtil.isEmpty(req.getName())) {
            MybatisGenerateException.throwMsg("请输入此配置名称");
        }
        if (ObjectUtil.isEmpty(req.getUrl())) {
            MybatisGenerateException.throwMsg("请输入数据库配置");
        }
        try {
            druidDataSource.restart();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }


        String marsQuickCacheName = getFileName();
        String path = getPath();
        String filePath = path + marsQuickCacheName;

        List<File> files = FileUtil.searchFiles(new File(filePath), marsQuickCacheName);

        List<DatabaseRequest> old = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(files)) {
            old.add(req);
            String fileContent = FileUtil.getFileContent(files.get(0));
            List<DatabaseRequest> databases = JsonUtil.parseArray(DatabaseRequest.class, fileContent);
            if (ObjectUtil.isNotEmpty(databases)) {
                for (int i = 0; i < databases.size(); i++) {
                    DatabaseRequest databaseReq = databases.get(i);
                    if (databaseReq != null && !databaseReq.getName().equals(req.getName())) {
                        old.add(databaseReq);
                    }
                }
            }
        } else {
            old.add(req);
        }

        FileUtil.deleteFile(new File(filePath));
        FileUtil.writeFile(new File(filePath), JSON.toJSONString(old));

        druidDataSource.setUsername(req.getUsername());
        druidDataSource.setPassword(req.getPassword());
        druidDataSource.setUrl(req.getUrl());
        druidDataSource.setDriverClassName(req.getDriverClassName());

    }


    public Object queryList(GenerateRequest request) {
        reloadDatabase(request.getDatabaseName());

        List<TableEntity> tableEntities = baseMapper.tableList(request.getTableName());

        return tableEntities;
    }


    private boolean connectDatabase =  false;
    private String dbName = "";

    public void reloadDatabase(String databaseName){
        if (!connectDatabase || (StringUtils.isNotBlank(dbName) && !dbName.equals(databaseName)) ){
            List<DatabaseRequest> databaseList = getDatabaseList();
            if (StringUtils.isEmpty(databaseName)){
                databaseName = databaseList.get(0).getName();
            }
            dbName = databaseName;
            String finalDatabaseName = databaseName;
            Optional<DatabaseRequest> first = databaseList.stream().filter(m -> m.getName().equals(finalDatabaseName)).findFirst();
            DatabaseRequest databaseReq = null;
            if (first.isPresent()){
                databaseReq = first.get();
            }
            if (databaseReq!=null) {
                connectDatabase = true;
                reload(databaseReq);
            }
        }

    }

    public Object queryTableColumns(GenerateRequest request) {
        List<ColumnEntity> columnEntities = baseMapper.queryColumns(request.getTableName());

        return columnEntities;
    }
}
