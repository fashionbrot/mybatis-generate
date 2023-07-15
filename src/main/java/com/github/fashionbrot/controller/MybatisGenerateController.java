package com.github.fashionbrot.controller;


import com.github.fashionbrot.entity.ColumnEntity;
import com.github.fashionbrot.entity.TableEntity;
import com.github.fashionbrot.request.GenerateRequest;
import com.github.fashionbrot.response.Response;
import com.github.fashionbrot.service.DruidService;
import com.github.fashionbrot.service.MybatisGenerateService;
import com.github.fashionbrot.util.IoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MybatisGenerateController {


    final MybatisGenerateService mybatisGenerateService;
    final DruidService druidService;
    final HttpServletResponse response;

    @GetMapping("/")
    public String index(){
        return "index";
    }


    /**
     * 列表
     */
    @ResponseBody
    @RequestMapping("/list")
    public Response<List<TableEntity>> list(GenerateRequest request){
        return Response.success(druidService.queryList(request));
    }


    /**
     * 字段列表
     */
    @ResponseBody
    @RequestMapping("/columnList")
    public Response<List<ColumnEntity>> columnList(GenerateRequest request){
        return Response.success(druidService.queryTableColumns(request));
    }




    /**
     * 生成代码 下载文件
     */
    @ResponseBody
    @RequestMapping("/generateZip")
    public void generateZip(GenerateRequest request) {
        byte[] data = null;//quickService.generatorZip( req);
        try {
            response.reset();
            response.setHeader("Content-Disposition", ("attachment; filename=\"quick.zip\""));
            response.addHeader("Content-Length", "" + data.length);
            response.setContentType("application/octet-stream; charset=UTF-8");
            IoUtil.write(data, response.getOutputStream());
        }catch (Exception e){
            log.error("generateZip error",e);
        }
    }

    /**
     * 生成代码 到本地
     */
    @ResponseBody
    @RequestMapping("/generate")
    public Response generate(GenerateRequest request)  {

        request = GenerateRequest.builder()
                .selectTableNames("banner")
                .author("张三")
                .out("E:\\dev\\idea\\projects\\Sample")
                .packageOut("com.jinxing.electric")
                .sourceSetJava("\\src\\main\\java")
                .entityEnable(true)
                .entityOut(".entity")
                .entitySuffix("Entity")
                .serialVersionUIDEnable(true)
                .swagger2Enable(false)
                .swagger3Enable(false)
                .mybatisPlusEnable(true)
                .lombokEnable(true)
                .dateFormatValue("yyyy-MM-dd HH:mm:ss")
                .fieldInsertFillNames("create_date")
                .fieldUpdateFillNames("update_date")
                .build();

        mybatisGenerateService.generatorCode(request);
        return Response.success();
    }


}
