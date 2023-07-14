package com.github.fashionbrot.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.fashionbrot.enums.DatabaseEnum;
import com.github.fashionbrot.exception.MybatisGenerateException;
import com.github.fashionbrot.mapper.BaseMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Configuration
@Component
public class DatasourceConfig implements BeanFactoryAware {

    BeanFactory beanFactory;

    @Resource
    private DruidDataSource db;

    @Bean
    @Primary
    public BaseMapper getGeneratorDao(){
        String url =  db.getUrl();

        DatabaseEnum mapperPrefix = DatabaseEnum.getDatabase(url);
        if (mapperPrefix==null){
            MybatisGenerateException.throwMsg("不支持当前数据库：" + db.getDriverClassName());
        }

        Object bean = beanFactory.getBean(mapperPrefix.getDb() + "Mapper");
        if (bean==null){
            MybatisGenerateException.throwMsg(mapperPrefix.getDb()+"Mapper  未找到");
        }
        return (BaseMapper) bean;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
