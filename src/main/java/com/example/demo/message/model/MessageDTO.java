package com.example.demo.message.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MessageDTO {
    private String key;
    private String lang;
    private boolean use;
    private String value;
}
