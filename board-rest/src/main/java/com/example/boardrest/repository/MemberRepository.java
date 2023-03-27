package com.example.boardrest.repository;

import com.example.boardrest.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, String> {

    @Query(value = "SELECT m.userId" +
            ", m.userPw" +
            ", m.auths " +
            "FROM Member m " +
            "WHERE m.userId = ?1")
    Member userInfo(String userId);

    Member findByUserId(String userId);
}
