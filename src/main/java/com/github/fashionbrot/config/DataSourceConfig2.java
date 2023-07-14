//package com.github.fashionbrot.config;
//
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.github.fashionbrot.exception.MybatisGenerateException;
//import com.github.fashionbrot.mapper.*;
//import org.springframework.beans.factory.BeanFactoryAware;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Component;
//
//
//@SuppressWarnings("ALL")
//@Configuration
//@Component
//public class DataSourceConfig2  {
//
//
//    @Autowired
//    private DruidDataSource db;
//
//    @Bean
//    @Primary
//    public BaseMapper getGeneratorDao(){
//
//        if(db.getDriverClassName().contains("mysql")){
////            return mysqlMapper;
//        }else if(db.getDriverClassName().contains("oracle")){
////            return oracleMapper;
//        }else if(db.getDriverClassName().contains("sqlserver")){
////            return sqlServerMapper;
//        }else {
//            MybatisGenerateException.throwMsg("不支持当前数据库：" + db.getDriverClassName());
//        }
//
//        return null;
//    }
//
//
//}
