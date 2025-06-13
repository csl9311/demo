package com.sample.backend.util.excel.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelConfig <T> {
    private String sheetName;
    private List<T> dataList;
    private Class<T> dataClass;
}
