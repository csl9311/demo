package com.sample.backend.sample.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sample.backend.sample.service.SampleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("sample")
public class SampleController {

    @Autowired
    private SampleService service;

    /** 
     * @PathVariable => 파라미터를 경로 변수로 받기
     * @RequestParam => form-data
     *                  x-www-form-urlencoded
     * @RequestBody  => 요청 본문을 JSON으로 받기
    */
    // @GetMapping("/get/{id}")
    // public ResponseEntity<SampleVO> getItem(@PathVariable("id") int id) {
    //     SampleVO item = service.get(id);
    //     return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    // }

    // /** @RequestBody - 요청 본문을 JSON으로 받기 */
    // @PostMapping("/items")
    // public ResponseEntity<String> createItem(@RequestBody Map<String, Object> request) {
    //     int id = service.selectMaxId();

    //     ObjectMapper mapper = new ObjectMapper();
    //     SampleVO vo = mapper.convertValue(mapper, SampleVO.class);
    //     service.put(id, vo);

    //     return ResponseEntity.ok("Item created with ID: " + id);
    // }

    // /** 특정 ID에 해당하는 데이터를 수정 */
    // @PutMapping("/items/{id}")
    // public ResponseEntity<String> updateItem(@PathVariable("id") int id, @RequestBody Map<String, Object> request) {
    //     boolean exist = service.get(id) == null;
    //     if (!exist) {
    //         return ResponseEntity.notFound().build();
    //     }

    //     ObjectMapper mapper = new ObjectMapper();
    //     SampleVO vo = mapper.convertValue(mapper, SampleVO.class);
    //     service.put(id, vo);

    //     return ResponseEntity.ok("Item updated with ID: " + id);
    // }

    // // DELETE 요청 - 특정 ID의 데이터를 삭제
    // @DeleteMapping("/items/{id}")
    // public ResponseEntity<String> deleteItem(@PathVariable("id") int id) {
    //     boolean exist = service.get(id) == null;
    //     if (!exist) {
    //         return ResponseEntity.notFound().build();
    //     }

    //     service.remove(id);
    //     return ResponseEntity.ok("Item deleted with ID: " + id);
    // }

    @PostMapping("/downloadExcel")
    public ResponseEntity<Object> excelDownloadSample(@RequestBody Map<String, Object> param) throws Exception {
        Map<String, Object> res = service.downloadExcel(param);
        Object bytes    = MapUtils.getObject(res, "bytes"   );
        String fileName = MapUtils.getString(res, "fileName");
        
        // set header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<List<Map<String, Object>>> uploadFile(MultipartFile[] files) throws Exception {
        return new ResponseEntity<>(service.uploadFile(files), HttpStatus.OK);
    }
}
