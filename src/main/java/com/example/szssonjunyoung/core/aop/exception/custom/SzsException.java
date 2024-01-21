package com.example.szssonjunyoung.core.aop.exception.custom;


import com.example.szssonjunyoung.core.aop.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SzsException extends RuntimeException {

    private ErrorCode errorCode;

    private String addMessage;


    public SzsException(final ErrorCode errorCode) {
        this.errorCode = errorCode;
    }


}
