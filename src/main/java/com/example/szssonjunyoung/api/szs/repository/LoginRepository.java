package com.example.szssonjunyoung.api.szs.repository;

import com.example.szssonjunyoung.api.szs.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Users, Long> {

    @Query(value = "SELECT * FROM users a WHERE a.user_id = :userId", nativeQuery = true)
    Optional<Users> findByUserId(@Param("userId") String userId);
}
