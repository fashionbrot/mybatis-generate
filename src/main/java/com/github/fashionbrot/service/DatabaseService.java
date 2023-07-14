package com.github.fashionbrot.service;


import com.alibaba.druid.pool.DruidDataSource;
import com.github.fashionbrot.enums.DatabaseEnum;
import com.github.fashionbrot.exception.MybatisGenerateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseService {


    final DruidDataSource druidDataSource;

    public DatabaseEnum getDatabaseEnum(){
        String url = druidDataSource.getUrl();

        DatabaseEnum databaseEnum = DatabaseEnum.getMapperPrefix(url);
        if (databaseEnum==null){
            MybatisGenerateException.throwMsg("不支持当前数据库：" + druidDataSource.getDriverClassName());
        }

        return databaseEnum;
    }

}
