package com.sample.backend.sample.dao;

import org.apache.ibatis.annotations.Mapper;

import com.sample.backend.sample.model.SampleVO;

@Mapper
public interface SampleDao {

    SampleVO get(int id);

    int selectMaxId();

    int put(int id, SampleVO map);

    int remove(int id);
    
}
