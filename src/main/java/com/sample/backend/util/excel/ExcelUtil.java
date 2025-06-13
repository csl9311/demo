package com.sample.backend.util.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.sample.backend.util.excel.annotation.ExcelHeader;
import com.sample.backend.util.excel.model.ExcelConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExcelUtil {
    private Workbook workbook;

    private CellStyle HEADER;
    private CellStyle ESSENTIAL;
    private CellStyle DEFAULT;
    private CellStyle CENTER;
    private CellStyle RIGHT;
    private CellStyle EDITABLE;
    private CellStyle EDITABLE_CENTER;
    private CellStyle EDITABLE_RIGHT;

    public ExcelUtil() {
        workbook = new XSSFWorkbook();
        initWorkbook();
    }

    public ExcelUtil(int rowAccessWindowSize) {
        workbook = new SXSSFWorkbook(rowAccessWindowSize); // row 단위 flush
        initWorkbook();
    }

    // workbook 초기화
    public ExcelUtil initWorkbook() {
        // font
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);

        Font bodyFont = workbook.createFont();
        bodyFont.setBold(false);

        // set style
        this.DEFAULT = workbook.createCellStyle();
        this.DEFAULT.setFont(bodyFont);
        this.DEFAULT.setVerticalAlignment(VerticalAlignment.CENTER);
        this.DEFAULT.setBorderTop(BorderStyle.THIN);
        this.DEFAULT.setBorderLeft(BorderStyle.THIN);
        this.DEFAULT.setBorderRight(BorderStyle.THIN);
        this.DEFAULT.setBorderBottom(BorderStyle.THIN);

        this.HEADER = workbook.createCellStyle();
        this.HEADER.setFont(headerFont);
        this.HEADER.setVerticalAlignment(VerticalAlignment.CENTER);
        this.HEADER.setBorderTop(BorderStyle.THIN);
        this.HEADER.setBorderLeft(BorderStyle.THIN);
        this.HEADER.setBorderRight(BorderStyle.THIN);
        this.HEADER.setBorderBottom(BorderStyle.THIN);
        this.HEADER.setAlignment(HorizontalAlignment.CENTER);
        this.HEADER.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.index);
        this.HEADER.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        this.ESSENTIAL = workbook.createCellStyle();
        this.ESSENTIAL.cloneStyleFrom(HEADER);
        this.ESSENTIAL.setFillForegroundColor(IndexedColors.RED.index);

        this.EDITABLE = workbook.createCellStyle();
        this.EDITABLE.cloneStyleFrom(DEFAULT);
        this.EDITABLE.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);

        this.EDITABLE_CENTER = workbook.createCellStyle();
        this.EDITABLE_CENTER.cloneStyleFrom(EDITABLE);
        this.EDITABLE_CENTER.setAlignment(HorizontalAlignment.CENTER);

        this.EDITABLE_RIGHT = workbook.createCellStyle();
        this.EDITABLE_RIGHT.cloneStyleFrom(EDITABLE);
        this.EDITABLE_RIGHT.setAlignment(HorizontalAlignment.RIGHT);

        this.CENTER = workbook.createCellStyle();
        this.CENTER.cloneStyleFrom(DEFAULT);
        this.CENTER.setAlignment(HorizontalAlignment.CENTER);

        this.RIGHT = workbook.createCellStyle();
        this.RIGHT.cloneStyleFrom(DEFAULT);
        this.RIGHT.setAlignment(HorizontalAlignment.RIGHT);
        return this;
    }

    // create sheet
    // render head
    // render body
    // create temp file
    // set response

    public <T> byte[] exportExcelByAnnotation(List<ExcelConfig<T>> excelSheetConfigs) throws Exception {
        for (ExcelConfig<T> excelSheetConfig : excelSheetConfigs) {
            String sheetName = excelSheetConfig.getSheetName();
            List<T> dataList = excelSheetConfig.getDataList();
            Class<T> dataClass = excelSheetConfig.getDataClass();

            // create sheet
            Sheet sheet = workbook.createSheet(sheetName);

            // render header
            List<Field> fields = Arrays.asList(dataClass.getDeclaredFields());
            fields = fields.stream()
                    .filter(x -> {
                        return x.getAnnotation(ExcelHeader.class) != null;
                    })
                    .sorted((o1, o2) -> {
                        ExcelHeader o1Annotation = o1.getAnnotation(ExcelHeader.class);
                        ExcelHeader o2Annotation = o2.getAnnotation(ExcelHeader.class);
                        return Integer.compare(o1Annotation.sort(), o2Annotation.sort());
                    })
                    .collect(Collectors.toList());
            // log.debug("fields count: {}", fields.size());

            Row header = sheet.createRow(0);

            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                field.setAccessible(true);

                ExcelHeader colConfig = field.getAnnotation(ExcelHeader.class);

                // HorizontalAlignment align = colConfig.align(); // 헤더는 align 사용 X
                // int sort = colConfig.sort(); // 앞에서 sorting 할 때 이미 사용
                // boolean editable = colConfig.editable(); // body에서만 사용

                Cell cell = header.createCell(i);
                cell.setCellValue(colConfig.headerText());
                cell.setCellStyle(colConfig.essential() ? this.ESSENTIAL : this.HEADER);
                sheet.setColumnWidth(i, 256 * colConfig.width());
            }

            // render body
            int rowNum = sheet.getLastRowNum() + 1; // 마지막 행 + 1
            sheet.createFreezePane(0, rowNum); // 틀고정

            for (T data : dataList) {
                Row row = sheet.createRow(rowNum++);
                int cellNum = 0;

                for (Field field : fields) {
                    Cell cell = row.createCell(cellNum++);
                    field.setAccessible(true);

                    ExcelHeader colConfig = field.getAnnotation(ExcelHeader.class);
                    // this.createBodyCell(row, cellNum++, value, colConfig, maskConfig);

                    // set style
                    CellStyle cellStyle = null;
                    boolean editable = colConfig.editable();
                    switch (colConfig.align()) {
                        case CENTER: cellStyle = editable ? this.EDITABLE_CENTER : this.CENTER ; break;
                        case RIGHT : cellStyle = editable ? this.EDITABLE_RIGHT  : this.RIGHT  ; break;
                        default    : cellStyle = editable ? this.EDITABLE        : this.DEFAULT; break;
                    }
                    cell.setCellStyle(cellStyle);

                    try {
                        Object value = field.get(data);

                        if (value == null) {
                            log.debug("value is empty: field: {}, data: {}", field.getName());
                            continue;
                        }

                        String valueStr = String.valueOf(value).trim();

                        if (value instanceof Number) {
                            cell.setCellValue(Double.parseDouble(valueStr));

                        } else if (value instanceof String) {
                            cell.setCellValue(valueStr);

                        } else if (value instanceof Character) {
                            cell.setCellValue(valueStr);

                        } else if (value instanceof Boolean) {
                            cell.setCellValue(Boolean.parseBoolean(valueStr));

                        } else if (value instanceof Date) {
                            String dateFormat = StringUtils.isNotEmpty(colConfig.dateFormat()) ? colConfig.dateFormat() : "yyyy-MM-dd HH:mm:dd";
                            Date date = (Date) value;
                            LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                            cell.setCellValue(localDateTime.format(DateTimeFormatter.ofPattern(dateFormat)));

                        } else if (value instanceof GregorianCalendar) {
                            String dateFormat = StringUtils.isNotEmpty(colConfig.dateFormat()) ? colConfig.dateFormat() : "yyyy-MM-dd HH:mm:dd";
                            GregorianCalendar gc = (GregorianCalendar) value;
                            LocalDateTime localDateTime = gc.toZonedDateTime().toLocalDateTime();
                            cell.setCellValue(localDateTime.format(DateTimeFormatter.ofPattern(dateFormat)));

                        } else if (value instanceof LocalDate) {
                            String dateFormat = StringUtils.isNotEmpty(colConfig.dateFormat()) ? colConfig.dateFormat() : "yyyy-MM-dd";
                            LocalDate localDate = (LocalDate) value;
                            cell.setCellValue(localDate.format(DateTimeFormatter.ofPattern(dateFormat)));

                        } else if (value instanceof LocalDateTime) {
                            String dateFormat = StringUtils.isNotEmpty(colConfig.dateFormat()) ? colConfig.dateFormat() : "yyyy-MM-dd HH:mm:dd";
                            LocalDateTime localDateTime = (LocalDateTime) value;
                            cell.setCellValue(localDateTime.format(DateTimeFormatter.ofPattern(dateFormat)));
                        } else {
                            log.error("undefined type - type: {}, value: {}", value.getClass().getSimpleName(), valueStr);
                        }

                    } catch (IllegalArgumentException e) {
                        log.error("IllegalArgumentException: {}", e.getMessage());
                    } catch (IllegalAccessException e) {
                        log.error("IllegalAccessException: {}", e.getMessage());
                    }
                } // for fields
            } // for dataList
        } // for excelSheetConfigs
        
        try {
            // 엑셀 파일 바이트 변환
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            byte[] excelBytes = outputStream.toByteArray();

            outputStream.close();
            workbook.close();

            return excelBytes;
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage());
            throw e;
        }
    }
}
