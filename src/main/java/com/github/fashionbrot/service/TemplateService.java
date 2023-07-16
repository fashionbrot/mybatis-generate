package com.github.fashionbrot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.fashionbrot.config.GenerateTemplate;
import com.github.fashionbrot.util.JsonUtil;
import com.github.fashionbrot.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

    private List<GenerateTemplate> generateTemplates=new ArrayList<>();


    public List<GenerateTemplate> getTemplate()  {
        if (ObjectUtil.isNotEmpty(generateTemplates)){
            return generateTemplates;
        }
        String path = "/vm.json";
        InputStream inputStream = this.getClass().getResourceAsStream(path);
        generateTemplates = JsonUtil.parseInputStream(inputStream, new TypeReference<List<GenerateTemplate>>() {});
        return generateTemplates;
    }




}
