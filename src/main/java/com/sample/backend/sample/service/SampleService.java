package com.sample.backend.sample.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sample.backend.sample.dao.SampleDao;
import com.sample.backend.sample.model.SampleExcelVO;
import com.sample.backend.sample.model.SampleVO;
import com.sample.backend.util.excel.ExcelUtil;
import com.sample.backend.util.excel.model.ExcelConfig;
import com.sample.backend.util.file.FileUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SampleService {

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private SampleDao dao;

    public SampleVO get(int id) {
        return dao.get(id);
    }

    public int selectMaxId() {
        return dao.selectMaxId();
    }

    public int put(int id, SampleVO map) {
        return dao.put(id, map);
    }

    public int remove(int id) {
        return dao.remove(id);
    }

    public Map<String, Object> downloadExcel(Map<String, Object> param) throws Exception {
        List<SampleExcelVO> voList = new ArrayList<>();
        int dummySize = MapUtils.getIntValue(param, "dummySize", 0);
        for (int i = 0; i < dummySize; i++) {
            voList.add(new SampleExcelVO(i));
        }

        ExcelConfig<SampleExcelVO> sheetConfig = new ExcelConfig<>();
        sheetConfig.setSheetName("message from json");
        sheetConfig.setDataList(voList);
        sheetConfig.setDataClass(SampleExcelVO.class);

        List<ExcelConfig<SampleExcelVO>> excelSheetConfigs = Arrays.asList(sheetConfig);
        ExcelUtil excelUtil = new ExcelUtil(100000); // init excel
        byte[] bytes = excelUtil.exportExcelByAnnotation(excelSheetConfigs); // render body
        
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss_")) + "sample.xlsx";

        Map<String, Object> res = new HashMap<>();
        res.put("fileName", fileName);
        res.put("bytes", bytes);
        return res;
    }

    public List<Map<String, Object>> uploadFile(MultipartFile[] files) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();

        if (files == null) {
            return list;
        }

        for (MultipartFile file: files) {
            Map<String, Object> uploadRes = fileUtil.upload(file);
            list.add(uploadRes);
        }

        return list;
    }
    
}
