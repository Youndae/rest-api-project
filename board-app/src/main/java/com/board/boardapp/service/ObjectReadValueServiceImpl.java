package com.board.boardapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ObjectReadValueServiceImpl implements ObjectReadValueService{

    @Override
    public <T> T setReadValue(T dto, String responseValue) {
        try{
            ObjectMapper om = new ObjectMapper();
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            dto = (T) om.readValue(responseValue, dto.getClass());
        }catch(JsonProcessingException e) {
            new Exception(e.getMessage());
        }

        return dto;
    }
}
