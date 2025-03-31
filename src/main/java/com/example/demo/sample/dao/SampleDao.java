package com.example.demo.sample.dao;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.sample.model.SampleVO;

@Mapper
public interface SampleDao {

    SampleVO get(int id);

    int selectMaxId();

    int put(int id, SampleVO map);

    int remove(int id);
    
}
