package com.example.szssonjunyoung.api.szs.repository;

import com.example.szssonjunyoung.api.szs.entity.RefundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefundRepository extends JpaRepository<RefundEntity, Long> {

    @Query(value = "SELECT * FROM REFUND WHERE scrap_info_id = (SELECT max(scrap_info_id) FROM users a " +
            "inner join scrap_info b " +
            "on a.id = b.id " +
            "WHERE a.id = :id)", nativeQuery = true)
    Optional<RefundEntity> findByUserIdRefund(@Param("id") String id);
}
