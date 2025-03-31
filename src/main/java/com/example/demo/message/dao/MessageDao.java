package com.example.demo.message.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.message.model.MessageDTO;

@Mapper
public interface MessageDao {

    int upsertList(List<MessageDTO> dataList);

    List<MessageDTO> select(Map<String, Object> param);
}
