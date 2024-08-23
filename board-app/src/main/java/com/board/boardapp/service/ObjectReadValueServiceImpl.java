package com.board.boardapp.service;

import com.board.boardapp.domain.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ObjectReadValueServiceImpl implements ObjectReadValueService{

    private static final ObjectMapper om = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     *
     * @param dto
     * @param responseValue
     * @return
     * @param <T>
     *
     * T 의 타입이 단순 DTO.class 타입인 경우.
     */
    @Override
    public <T> T fromJson(T dto, String responseValue) {
        try {
            return (T) om.readValue(responseValue, dto.getClass());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("ObjectMapping fail");
        }
    }

    /**
     *
     * @param dto
     * @param responseValue
     * @return
     * @param <T>
     *
     * T의 타입이 DTO<T>인 경우.
     */
    @Override
    public <T> T fromJsonWithReference(ParameterizedTypeReference<T> typeReference, String responseValue) {
        try {

            JavaType javaType = om.getTypeFactory().constructType(typeReference.getType());

            return om.readValue(responseValue, javaType);

//            return om.readValue(responseValue, om.constructType(typeReference.getType()));
        }catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to cast Type to class", e);
        }
    }

    /**
     *
     * @param dto
     * @param responseValue
     * @return
     *
     * 반환 타입이 List<DTO>인 경우
     */
    @Override
    public <T> List<T> fromJsonToList(List<T> dto, String responseValue) {
        try {
            return om.readValue(responseValue, new TypeReference<List<T>>() {});
        }catch (JsonProcessingException e) {
            throw new IllegalArgumentException("ObjectMapping fail", e);
        }
    }

    /**
     *
     * @param dto
     * @param responseValue
     * @param cri
     * @return
     * @param <T>
     *
     * Pagination이 들어가는 각 게시판 리스트와 댓글 리스트인 경우.
     */
    @Override
    public <T> T fromJsonWithPagination(ParameterizedTypeReference<T> dto, String responseValue, Criteria cri) {
        T mappingDTO = fromJsonWithReference(dto, responseValue);

        if(mappingDTO instanceof PaginationListDTO<?>)
            ((PaginationListDTO<?>) mappingDTO).setPageDTO(cri);


        return mappingDTO;
    }
}
