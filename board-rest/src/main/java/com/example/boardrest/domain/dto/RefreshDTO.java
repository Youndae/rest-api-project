package com.example.boardrest.domain.dto;

import lombok.*;

import java.util.Date;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RefreshDTO {

    private String refreshIndex;

    private String tokenVal;

    private Date tokenExpires;

    private String originIndex;


}
