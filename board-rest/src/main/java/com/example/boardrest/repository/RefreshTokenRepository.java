package com.example.boardrest.repository;

import com.example.boardrest.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;

@Deprecated
/**
 * Redis 적용으로 인해 RefreshToken 관리를 DB -> Redis로 넘기게 되어
 * Repository 사용 안함.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE refreshToken " +
            "SET tokenVal = ?1" +
            ", expires = ?2" +
            ", rtIndex = ?3 " +
            "WHERE rtIndex = ?4"
    , nativeQuery = true)
    void patchToken(String refreshToken, Date refreshExpires, String rIndex, String originIndex);


    @Query(value = "SELECT userId " +
            "FROM refreshToken " +
            "WHERE rtIndex = ?1 " +
            "AND tokenVal = ?2"
    , nativeQuery = true)
    String existsByRtIndexAndUserId(String rtIndex, String refreshTokenVal);
}
