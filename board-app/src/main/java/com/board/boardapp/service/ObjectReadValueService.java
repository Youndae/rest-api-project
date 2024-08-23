package com.board.boardapp.service;


import com.board.boardapp.domain.dto.Criteria;
import com.board.boardapp.domain.dto.PaginationListDTO;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

public interface ObjectReadValueService {

    <T> T fromJson(T dto, String responseValue);

    <T> T fromJsonWithReference(ParameterizedTypeReference<T> typeReference, String responseValue);


    <T>List<T> fromJsonToList(List<T> dto, String responseValue);

    <T> T fromJsonWithPagination(ParameterizedTypeReference<T> dto, String responseValue, Criteria cri);

}
