package com.example.demo.message.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.message.model.MessageDTO;
import com.example.demo.message.service.MessageService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("message")
public class MessageController {

    @Autowired
    private MessageService service;

    @GetMapping("/get")
    public ResponseEntity<List<MessageDTO>> getMessageList(@RequestParam Map<String, Object> param) throws Exception {
        log.debug("param: {}", param);
        List<MessageDTO> dataList = service.getMessageList(param);

        return CollectionUtils.isNotEmpty(dataList) ? ResponseEntity.ok(dataList) 
                                                    : ResponseEntity.notFound().build();
    }

    @PutMapping("/put")
    public ResponseEntity<Map<String, Object>> upsertList(@RequestBody Map<String, Object> param) throws Exception {
        log.debug("param: {}", param);
        int result = service.upsertList(param);

        Map<String, Object> map = new HashMap<>();
        map.put("message", "success");
        map.put("data", result);
        return ResponseEntity.ok(map);
    }

    @GetMapping("/getMessageFromJsonFile")
    public ResponseEntity<List<Map<String, Object>>> getMessageFromJsonFile(@RequestBody Map<String, Object> param) throws Exception {
        log.debug("param: {}", param);
        List<Map<String, Object>> dataList = service.getMessageFromJsonFile(param);

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dataList);
    }

    @PostMapping("/downloadExcelFromDB")
    public ResponseEntity<Object> downloadExcelFromDB(@RequestBody Map<String, Object> param) throws Exception {
        log.debug("param: {}", param);
        Map<String, Object> res = service.downloadExcelFromDB(param);
        Object bytes    = MapUtils.getObject(res, "bytes"   );
        String fileName = MapUtils.getString(res, "fileName");

        // set header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + fileName);
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @PostMapping("/downloadExcelFromJsonFile")
    public ResponseEntity<Object> exportExcelFromJsonFile(@RequestBody Map<String, Object> param) throws Exception {
        log.debug("param: {}", param);
        Map<String, Object> res = service.downloadExcelFromJsonFile(param);
        Object bytes    = MapUtils.getObject(res, "bytes"   );
        String fileName = MapUtils.getString(res, "fileName");
        
        // set header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @PostMapping("/uploadExcelToDB")
    public ResponseEntity<List<Map<String, Object>>> uploadExcelToDB(MultipartFile[] uploadFiles) throws Exception {
        List<Map<String, Object>> list = service.uploadExcelToDB(uploadFiles);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/uploadJsonToDB")
    public ResponseEntity<List<Map<String, Object>>> uploadJson(@RequestBody Map<String, Object> param) throws Exception {
        log.debug("param: {}", param);
        List<Map<String, Object>> dataList = service.getMessageFromJsonFile(param);

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dataList);
    }
}
