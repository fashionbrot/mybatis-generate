package com.github.fashionbrot.service;


import com.github.fashionbrot.common.consts.CharsetConst;
import com.github.fashionbrot.common.util.FileUtil;
import com.github.fashionbrot.common.util.ObjectUtil;
import com.github.fashionbrot.consts.GlobalConst;
import com.github.fashionbrot.request.GenerateRequest;
import com.github.fashionbrot.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheService {

    final Environment environment;

    public void saveConfig(GenerateRequest request) {
        String databaseName = request.getDatabaseName();
        File file = new File(getPath() + databaseName + ".json");
        FileUtil.deleteFile(file);
        String jsonString = JsonUtil.toString(request);
        FileUtil.writeFile(file,jsonString, CharsetConst.UTF8_CHARSET);
    }


    public String getPath() {

        String path = environment.getProperty(GlobalConst.CACHE_PATH);
        if (ObjectUtil.isEmpty(path)) {
            path = FileUtil.getUserHome();
        }
        path = path + File.separator + GlobalConst.NAME + File.separator;
        return path;
    }

    public Object getConfig(GenerateRequest request) {
        String key = request.getDatabaseName()+".json";
        List<File> files = FileUtil.searchFiles(new File(getPath()), key);
        if (ObjectUtil.isNotEmpty(files)){
            String fileContent = FileUtil.getFileContent(new File(getPath() + key), CharsetConst.UTF8_CHARSET);
            return JsonUtil.parseObject(fileContent,GenerateRequest.class);
        }
        return null;
    }
}
