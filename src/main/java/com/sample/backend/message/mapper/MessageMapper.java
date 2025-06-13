package com.sample.backend.message.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.sample.backend.message.model.MessageDTO;

@Mapper
public interface MessageMapper {

    int upsertList(List<MessageDTO> dataList);

    List<MessageDTO> select(Map<String, Object> param);
}
