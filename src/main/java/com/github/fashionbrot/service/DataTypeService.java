package com.github.fashionbrot.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.fashionbrot.enums.DatabaseEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataTypeService {


    final DruidDataSource db;

    private static Map<String, Properties> dataTypeMap = new ConcurrentHashMap<>();
    private static final String NONE = "default";

    public Properties getDataType() {
        DatabaseEnum databaseEnum = DatabaseEnum.getDatabase(db.getUrl());


        if (databaseEnum == null) {
            Properties properties = dataTypeMap.get(NONE);
            if (properties == null) {
                properties = loadProperties(NONE);
                dataTypeMap.put(NONE, properties);
            }
            return dataTypeMap.get(NONE);
        } else {
            String db = databaseEnum.getDb();
            if (dataTypeMap.containsKey(db)) {
                return dataTypeMap.get(db);
            }

            Properties properties = loadProperties(db);
            dataTypeMap.put(db, properties);
            return dataTypeMap.get(db);
        }
    }

    public Properties loadProperties(String databaseType) {
        try {
            String fileName = "/dataType.properties";
            if (!NONE.equals(databaseType)) {
                fileName = "/dataType-" + databaseType + ".properties";
            }
            InputStream in = this.getClass().getResourceAsStream(fileName);
            Properties props = new Properties();
            InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
            props.load(inputStreamReader);
            return props;
        } catch (Exception e) {
            log.error("loadProperties error", e);
        }

        return null;
    }


    public String getProperty(String dataType, String unknowType) {
        Properties properties = getDataType();
        return properties.getProperty(dataType,unknowType);
    }
}
