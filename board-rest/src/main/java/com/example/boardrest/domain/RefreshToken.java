package com.example.boardrest.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
public class RefreshToken {

    @Id
    private String rtIndex;

    private String userId;

    private String tokenVal;

    private Date expires;
}
