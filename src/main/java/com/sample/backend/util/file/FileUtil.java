package com.sample.backend.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileUtil {
    public static final String FILE_SEPARATOR = File.separator;
    public static final String ENTER = "\r\n";
    public static final String TAB = "\t";
    public static final String DEFAULT_CHARSET = "UTF-8";

    @Value("${environment.fileUpload}")
    private String uploadPath;
    
    private String getFileText(File file) throws Exception {
        if (ObjectUtils.isEmpty(file)) {
            throw new Exception("file is required");
        }

        if (!file.canRead()) {
            throw new Exception("can not read file: " + file.getAbsolutePath());
        }

        FileInputStream   fis = null;
        InputStreamReader isr = null;
        BufferedReader    br  = null;
        try {
            fis = new FileInputStream(file.getAbsolutePath());
            isr = new InputStreamReader(fis, "UTF-8");
            br  = new BufferedReader(isr);
            return String.join("", br.lines().toList());
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException: {}", e.getMessage());
            throw e;
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage());
            throw e;
        } finally {
            if (br  != null) br.close();
            if (isr != null) isr.close();
            if (fis != null) fis.close();
        }
    }

    public JSONObject getJsonFileData(File file) throws Exception {
        return new JSONObject(getFileText(file));
    }

	public Map<String, Object> upload(MultipartFile file) throws Exception {
        try {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String contentType = file.getContentType();
            String orginalName = file.getOriginalFilename();

            log.debug("orginalName: {}", orginalName);
            log.debug("contentType: {}", contentType);

            String fileName = orginalName.substring(orginalName.lastIndexOf("\\") + 1);
            // log.info("fileName: " + fileName);

            // UUID
            String uuid = UUID.randomUUID().toString();

            // 저장할 파일 이름 중간에 "_"를 이용해서 구현
            String saveName = uploadPath + File.separator + uuid + "_" + fileName;
            Path savePath = Paths.get(saveName);

            file.transferTo(savePath); // 실제 이미지 저장

            Map<String, Object> res = new HashMap<>();
            // res.put("folderPath", folderPath);
            res.put("uploadPath", uploadPath);
            res.put("fileName", fileName);
            res.put("uuid"    , uuid    );

            return res;
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage());
            throw e;
        }
	}
}
