package com.example.szssonjunyoung.api.szs.repository;

import com.example.szssonjunyoung.api.szs.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignRepository extends JpaRepository<Users, Long> {
}
