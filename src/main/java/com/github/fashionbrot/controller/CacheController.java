package com.github.fashionbrot.controller;


import com.github.fashionbrot.request.GenerateRequest;
import com.github.fashionbrot.response.Response;
import com.github.fashionbrot.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@RequestMapping("/cache")
@Controller
@RequiredArgsConstructor
public class CacheController {

    final CacheService cacheService;

    @ResponseBody
    @RequestMapping("/setCache")
    public Response setCache(GenerateRequest request) {
        cacheService.saveConfig(request);
        return Response.success();
    }

    @ResponseBody
    @RequestMapping("/getCache")
    public Response getCache(GenerateRequest request) {
        return Response.success(cacheService.getConfig(request));
    }
}
