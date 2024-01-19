package com.example.szssonjunyoung.api.szs.repository;

import com.example.szssonjunyoung.api.szs.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SignRepository extends JpaRepository<UsersEntity, Long> {

    @Query(value = "SELECT * FROM users a WHERE a.name = :name OR a.reg_no = :regNo", nativeQuery = true)
    Optional<UsersEntity> findByNameAndRegNo(@Param("name") String name, @Param("regNo") String regNo);
}
