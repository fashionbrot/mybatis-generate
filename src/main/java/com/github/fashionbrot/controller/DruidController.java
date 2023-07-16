package com.github.fashionbrot.controller;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.fashionbrot.request.DatabaseRequest;
import com.github.fashionbrot.response.Response;
import com.github.fashionbrot.service.DruidService;
import com.github.fashionbrot.util.FileUtil;
import com.github.fashionbrot.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DruidController {


    final DruidService druidService;

    @ResponseBody
    @RequestMapping("/reload")
    public Response reload(DatabaseRequest request) {
//        request = DatabaseRequest.builder()
//                .url("jdbc:mysql://127.0.0.1:3306/test?Unicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&tinyInt1isBit=false")
//                .name("uuid")
//                .username("root")
//                .password("123456")
//                .driverClassName("com.mysql.jdbc.Driver")
//                .build();
        druidService.reload(request);
        return Response.success();
    }



    @ResponseBody
    @RequestMapping("/load")
    public Response load() {

        List<DatabaseRequest> databaseList = druidService.getDatabaseList();
        return Response.success(databaseList);
    }


    @ResponseBody
    @RequestMapping("/remove")
    public Response remove(DatabaseRequest req) {
        String marsQuickCacheName = druidService.getFileName();
        String path = druidService.getPath();

        List<DatabaseRequest> databaseList = druidService.getDatabaseList();

        List<DatabaseRequest> old = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(databaseList)){
            for (int i = 0; i <databaseList.size() ; i++) {
                DatabaseRequest databaseReq = databaseList.get(i);
                if (databaseReq!=null && !databaseReq.getName().equals(req.getName())){
                    old.add(databaseReq);
                }
            }
        }

        String filePath = path + marsQuickCacheName;
        FileUtil.deleteFile(new File(filePath));
        FileUtil.writeFile(new File(filePath), JsonUtil.toString(old));
        return Response.success();
    }




}
