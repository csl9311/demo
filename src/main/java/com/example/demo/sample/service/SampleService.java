package com.example.demo.sample.service;

import java.time.Duration;
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

import com.example.demo.sample.dao.SampleDao;
import com.example.demo.sample.model.SampleExcelVO;
import com.example.demo.sample.model.SampleVO;
import com.example.demo.util.excel.ExcelUtil;
import com.example.demo.util.excel.model.ExcelConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SampleService {

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
        LocalDateTime ldt = LocalDateTime.now();

        List<SampleExcelVO> voList = new ArrayList<>();
        int dummySize = MapUtils.getIntValue(param, "dummySize", 0);
        for (int i = 0; i < dummySize; i++) {
            voList.add(new SampleExcelVO(i));
        }

        Duration duration = Duration.between(ldt, LocalDateTime.now());
        String nanoSec = String.format("%09d", duration.getNano()).substring(0, 3);
        log.info("set list done , duration : {}.{}", duration.getSeconds(), nanoSec);

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
    
}
