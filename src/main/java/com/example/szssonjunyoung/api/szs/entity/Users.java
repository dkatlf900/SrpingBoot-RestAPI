package com.example.szssonjunyoung.api.szs.entity;


import com.example.szssonjunyoung.api.szs.dto.request.SignReq;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userId;

    @Column(unique = true)
    private String regNo;

    private String password;

    private String name;



    /**
     * 회원가입
     */
    public Users(SignReq signReq) {
        this.userId = signReq.getUserId();
        this.password = signReq.getPassword();
        this.name = signReq.getName();
        this.regNo = signReq.getRegNo();
    }
}
