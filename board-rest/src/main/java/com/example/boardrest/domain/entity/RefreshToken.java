package com.example.boardrest.domain.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Deprecated
/**
 * Redis 적용으로 인해 RefreshToken 관리를 DB -> Redis로 넘기게 되어
 * Entity 사용 안함.
 */
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
