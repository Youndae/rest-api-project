package com.example.boardrest.repository;

import com.example.boardrest.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, String> {

    Member findByUserId(String userId);
}
