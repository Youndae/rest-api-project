package com.example.boardrest.domain.factory;

import com.example.boardrest.domain.dto.responseDTO.ResponseDetailAndModifyDTO;
import com.example.boardrest.domain.dto.responseDTO.ResponsePageableListDTO;
import com.example.boardrest.service.PrincipalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class ResponseFactory {

    private final PrincipalService principalService;

    public <T> ResponseEntity<ResponsePageableListDTO<T>> createListResponse(Page<T> dto, Principal principal) {
        String nickname = principalService.getNicknameToPrincipal(principal);

        return new ResponseEntity<>(
                new ResponsePageableListDTO<>(dto, nickname)
                , HttpStatus.OK
        );
    }

    public <T> ResponseEntity<ResponseDetailAndModifyDTO<T>> createDetailResponse(T dto, Principal principal) {
        String nickname = principalService.getNicknameToPrincipal(principal);

        return new ResponseEntity<>(
                new ResponseDetailAndModifyDTO<>(dto, nickname)
                , HttpStatus.OK
        );
    }

}
