package com.example.demo.message.service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.message.mapper.MessageMapper;
import com.example.demo.message.model.MessageDTO;
import com.example.demo.message.model.MessageVO;
import com.example.demo.util.excel.ExcelUtil;
import com.example.demo.util.excel.model.ExcelConfig;
import com.example.demo.util.file.FileUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageService {

    @Autowired
    private MessageMapper mapper;

    @Value("${environment.message.path}")
    private String messagePath;

    @Value("${environment.message.name_rule}")
    private String messageFileNameRule;

    @Autowired
    private FileUtil fileUtil;

    // DB 목록 조회
	public List<MessageDTO> getMessageList(Map<String, Object> param) {
        return mapper.select(param);
	}

    // json 파일 읽기
    public List<Map<String, Object>> getMessageFromJsonFile(Map<String, Object> param) throws Exception {
        File messageDir = new File(messagePath);
        if (!messageDir.exists()) {
            log.error("messagePath not exists: {}", messagePath);
            return null;
        }

        if (!messageDir.isDirectory()) {
            log.error("messagePath is not directory: {}", messagePath);
            return null;
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        Set<String> existSet = new HashSet<>();
        for (File file : messageDir.listFiles()) {
            // log.debug("fileName: {}", file.getName());
            String fileName = file.getName();
            String language = "ko";

            if (fileName.contains("_")) {
                language = fileName.replace(".json", "").split("_")[1];
            }

            JSONObject json = fileUtil.getJsonFileData(file);
            for (String key : json.keySet()) {
                Object value = json.get(key);

                boolean exists = !existSet.add(key); // 존재여부 확인

                // 미존재 시 add 후 continue
                if (!exists) {
                    Map<String, Object> data = new LinkedHashMap<>();
                    data.put("key", key);
                    data.put("value_" + language, value);
                    dataList.add(data);
                    continue;
                }

                // 존재 시 해당 map 찾아 value_{lang}에 값 세팅
                for (int j = 0; j < dataList.size(); j++) {
                    Map<String, Object> data = dataList.get(j);

                    String dataKey = MapUtils.getString(data, "key");

                    if (StringUtils.equals(key, dataKey)) {
                        data.put("value_" + language, value);
                        dataList.set(j, data);
                        break;
                    }
                }
            }
        }

        return dataList;
    }

    /** DB > 목록 저장
        languageList: ["ko", "en", "ja", ...]
        dataList: [
            {
                key: String
                value_ko: String
                value_en: String
                value_jp: String
                ...
            }
        ]
     */
    public int upsertList(Map<String, Object> param) throws Exception {
        File messageDir = new File(messagePath);
        if (!messageDir.exists()) {
            throw new Exception("messagePath is not exists: " + messagePath);
        }

        if (!messageDir.isDirectory()) {
            throw new Exception("messagePath is not directory: " + messagePath);
        }

        Set<String> langSet = new HashSet<>();
        for (File file : messageDir.listFiles()) {
            String fileName = file.getName();
            String language = "ko";

            if (fileName.contains("_")) {
                language = fileName.replace(".json", "").split("_")[1];
            }

            boolean exist = !langSet.add(language);
            if (exist) {
                log.error("already exist lang: {}", language);
            }
        }

        if (!param.containsKey("dataList")) {
            throw new Exception("dataList is required");
        }

        JSONObject paramJson = new JSONObject(param);
        JSONArray dataList = new JSONArray(paramJson.getJSONArray("dataList"));

        // 데이터 가공
        // List<MessageDTO> dataList = dataUtil.convertJsonArrayToList(jsonArr, MessageDTO.class);
        List<MessageDTO> upsertList = new ArrayList<>();
        for (int i = 0; i < dataList.length(); i++) {
            JSONObject data = dataList.getJSONObject(i);
            
            for (String lang : langSet) {
                if (data.has("value_" + lang)) {
                    MessageDTO dto = new MessageDTO();
                    dto.setKey(data.getString("key"));
                    dto.setValue(data.getString("value_" + lang));
                    dto.setLang(lang);
                    dto.setUse(true);
                    upsertList.add(dto);
                }
            }
        }

        // sort list
        upsertList.sort((o1, o2) -> {
            String o1Key = o1.getKey();
            String o2Key = o2.getKey();
            return StringUtils.compare(o1Key, o2Key);
        });

        // DB upsert
        // int result = mapper.upsertList(upsertList);
        int subSize = 1000;
        int result = 0;
        for (int i = 0; i < upsertList.size(); i += subSize) {
            int toIndex = i + subSize <= upsertList.size() ? i + subSize : upsertList.size();

            List<MessageDTO> subList = upsertList.subList(i, toIndex);
            log.debug("fromIndex: {}, toIndex: {}, subList size: {}", String.format("%-5d", i), String.format("%-5d", toIndex), subList.size());
            result += mapper.upsertList(subList);
        }
        return result;
    }

    private MessageVO setData(MessageVO vo, String lang, String value) {
        switch (lang) {
            case "ko": vo.setValue_ko(value); break;
            case "en": vo.setValue_en(value); break;
            case "ja": vo.setValue_ja(value); break;
            default: break;
        }
        return vo;
    }

    public Map<String, Object> downloadExcelFromDB(Map<String, Object> param) throws Exception {
        // DB 조회
        List<MessageDTO> dtoList = getMessageList(param);
        Set<String> keySet = new HashSet<>();

        // DTO to VO
        List<MessageVO> voList = new ArrayList<>();
        for (MessageDTO dto : dtoList) {
            String key   = dto.getKey();
            String lang  = dto.getLang();
            String value = dto.getValue();

            boolean exist = !keySet.add(key);

            // 존재하지 않는 경우 add 후 continue
            if (!exist) {
                MessageVO vo = new MessageVO();
                vo.setKey(key);
                vo = setData(vo, lang, value);
                voList.add(vo);
                continue;
            }
            
            // 존재하는 경우
            for (int i = 0; i < voList.size(); i++) {
                MessageVO vo = voList.get(i);
                String voKey = vo.getKey();

                if (StringUtils.equals(key, voKey)) {
                    vo = setData(vo, lang, value);
                    voList.set(i, vo);
                    break;
                }
            }
        }

        // set sheetConfig
        ExcelConfig<MessageVO> sheetConfig = new ExcelConfig<>();
        sheetConfig.setSheetName("message from db");
        sheetConfig.setDataList(voList);
        sheetConfig.setDataClass(MessageVO.class);

        List<ExcelConfig<MessageVO>> excelSheetConfigs = Arrays.asList(sheetConfig);
        ExcelUtil excelUtil = new ExcelUtil(); // init excel
        byte[] bytes = excelUtil.exportExcelByAnnotation(excelSheetConfigs); // render body

        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss_"))
                + "엑셀다운로드_(DB_to_Excel).xlsx";

        Map<String, Object> res = new HashMap<>();
        res.put("fileName", fileName);
        res.put("bytes", bytes);
        return res;
    }

    public Map<String, Object> downloadExcelFromJsonFile(Map<String, Object> param) throws Exception {
        // set objectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Map to MessageVO
        List<MessageVO> voList = getMessageFromJsonFile(param)
                .stream()
                .map(x -> {
                    return objectMapper.convertValue(x, MessageVO.class);
                }).collect(Collectors.toList());

        // set sheetConfig
        ExcelConfig<MessageVO> sheetConfig = new ExcelConfig<>();
        sheetConfig.setSheetName("message from json");
        sheetConfig.setDataList(voList);
        sheetConfig.setDataClass(MessageVO.class);

        List<ExcelConfig<MessageVO>> excelSheetConfigs = Arrays.asList(sheetConfig);
        ExcelUtil excelUtil = new ExcelUtil(); // init excel
        byte[] bytes = excelUtil.exportExcelByAnnotation(excelSheetConfigs); // render body
        
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss_"))
                + "엑셀다운로드_(json_to_Excel).xlsx";

        Map<String, Object> res = new HashMap<>();
        res.put("fileName", fileName);
        res.put("bytes", bytes);
        return res;
    }

    public List<Map<String, Object>> uploadExcelToDB(@RequestParam MultipartFile[] files) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();

        if (files == null) {
            return list;
        }

        for (MultipartFile file: files) {
            Map<String, Object> uploadRes = fileUtil.upload(file);
            
            // Workbook workbook = new XSSFWorkbook(file.getInputStream());
            // Iterator<Sheet> iter = workbook.sheetIterator();

            // while (iter.hasNext()) {
            //     Sheet sheet = iter.next();
            // }

            list.add(uploadRes);
        }

        return list;
    }

}
