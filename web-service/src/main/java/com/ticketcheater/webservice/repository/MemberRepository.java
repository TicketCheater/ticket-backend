package com.ticketcheater.webservice.repository;

import com.ticketcheater.webservice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT m FROM member m WHERE m.name = :name")
    Optional<Member> findByName(@Param("name") String name);

    @Query("SELECT m FROM member m WHERE m.name = :name AND m.deletedAt IS NULL")
    Optional<Member> findByNameAndDeletedAtIsNull(@Param("name") String name);
}
