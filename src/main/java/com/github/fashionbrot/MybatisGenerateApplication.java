package com.github.fashionbrot;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@SpringBootApplication
public class MybatisGenerateApplication {

    private static Environment environment;

    @Resource
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(MybatisGenerateApplication.class,args);
        String port = environment.getProperty("server.port");
        log.info("启动完成:{}  访问端口:{}", DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"),environment.getProperty("server.port"));
        System.out.println("http://localhost:"+port+"");
    }


    @PostConstruct
    public void init() {
        try {
            String port = environment.getProperty("server.port");
            String url = "http://localhost:" + port;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime rt = Runtime.getRuntime();
                // For Windows
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
