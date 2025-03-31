package com.example.demo.sample.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

import com.example.demo.util.excel.annotation.ExcelHeader;

import lombok.Data;

@Data
public class SampleExcelVO {

    @ExcelHeader(headerText = "Number")
    private Number number;

    @ExcelHeader(headerText = "Character")
    private Character char1;

    @ExcelHeader(headerText = "char")
    private char char2;

    @ExcelHeader(headerText = "Integer")
    private Integer int1;

    @ExcelHeader(headerText = "int")
    private int int2;

    @ExcelHeader(headerText = "Float")
    private Float float1;

    @ExcelHeader(headerText = "float")
    private float float2;

    @ExcelHeader(headerText = "Double")
    private Double double1;

    @ExcelHeader(headerText = "double")
    private double double2;

    @ExcelHeader(headerText = "Boolean")
    private Boolean boolean1;

    @ExcelHeader(headerText = "boolean")
    private boolean boolean2;

    @ExcelHeader(headerText = "Byte")
    private Byte byte1;

    @ExcelHeader(headerText = "byte")
    private byte byte2;

    @ExcelHeader(headerText = "String")
    private String string;

    @ExcelHeader(headerText = "Date")
    private Date date;

    @ExcelHeader(headerText = "Calendar")
    private Calendar calendar;

    @ExcelHeader(headerText = "LocalDate")
    private LocalDate localDate;

    @ExcelHeader(headerText = "LocalDateTime")
    private LocalDateTime localDateTime;
    
    @ExcelHeader(headerText = "BigDecimal")
    private BigDecimal bigDecimal;

    @ExcelHeader(headerText = "essential", essential = true)
    private String essential;

    @ExcelHeader(headerText = "editable", editable = true)
    private String editable;

    @ExcelHeader(headerText = "dateFormat", dateFormat = "yyyy-MM-dd")
    private LocalDateTime dateFormat;

    @ExcelHeader(headerText = "general", align = HorizontalAlignment.GENERAL)
    private String general;

    @ExcelHeader(headerText = "center", align = HorizontalAlignment.CENTER)
    private String center;

    @ExcelHeader(headerText = "right", align = HorizontalAlignment.RIGHT)
    private String right;

    @ExcelHeader(headerText = "maskingName")
    private String maskingName;

    @ExcelHeader(headerText = "maskingSsn")
    private String maskingSsn;
    
    @ExcelHeader(headerText = "maskingPhone")
    private String maskingPhone;

    @ExcelHeader(headerText = "maskingEmail")
    private String maskingEmail;

    @ExcelHeader(headerText = "maskingAccount")
    private String maskingAccount;

    public SampleExcelVO() {}

    public SampleExcelVO(int i) {
        String iStr = String.valueOf(i);

        this.number = i;
        this.bigDecimal = new BigDecimal(i).divide(new BigDecimal(1000));

        this.int1 = i * 10;
        this.int2 = i * 100;

        
        this.float1 = Float.parseFloat(iStr) / 10;
        this.float2 = Float.parseFloat(iStr) / 100;

        this.double1 = Double.parseDouble(iStr) / 1000;
        this.double2 = Double.parseDouble(iStr) / 10000;

        this.char1 = (char) i;
        this.char2 = (char) i;

        this.boolean1 = (i % 2 == 0);
        this.boolean2 = (i % 2 == 0);

        this.byte1 = (byte) i;
        this.byte2 = (byte) i;

        this.string = (String.format("%04d", i));

        this.date = new Date();
        this.calendar = Calendar.getInstance();
        this.localDate = LocalDate.now();
        this.localDateTime = LocalDateTime.now();

        this.dateFormat = LocalDateTime.now();

        this.general = "general";
        this.center  = "center";
        this.right   = "right";

        this.maskingName = "이름테스트";
        this.maskingPhone = "010-1234-5678";
        this.maskingEmail = "emailText@domainTest.com";
        this.maskingSsn = "123456-1234567";
        this.maskingAccount = "123456-12-123456";

        this.essential = "Y";
        this.editable = "Y";
    }
}
