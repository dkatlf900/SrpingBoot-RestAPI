package com.example.szssonjunyoung.api.szs.repository;

import com.example.szssonjunyoung.api.szs.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<UsersEntity, Long> {

    @Query(value = "SELECT * FROM users a WHERE a.user_id = :userId", nativeQuery = true)
    Optional<UsersEntity> findByUserId(@Param("userId") String userId);
}
