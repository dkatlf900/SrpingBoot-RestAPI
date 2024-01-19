package com.example.szssonjunyoung.api.szs.repository;

import com.example.szssonjunyoung.api.szs.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UsersEntity, Long> {

}
