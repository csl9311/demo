package com.sample.backend.message.model;

import com.sample.backend.util.excel.annotation.ExcelHeader;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MessageVO {
    @ExcelHeader(headerText = "key", width = 20)
    private String key;

    @ExcelHeader(headerText = "ko", width = 30)
    private String value_ko;

    @ExcelHeader(headerText = "en", width = 30)
    private String value_en;

    @ExcelHeader(headerText = "ja", width = 30)
    private String value_ja;

    @ExcelHeader(headerText = "count")
    private int useCount;
}
