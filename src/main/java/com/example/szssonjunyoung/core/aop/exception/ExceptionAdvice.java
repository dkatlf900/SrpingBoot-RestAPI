package com.example.szssonjunyoung.core.aop.exception;


import com.example.szssonjunyoung.base.dto.GeneralResponse;
import com.example.szssonjunyoung.base.service.MessageSourceService;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @Autowired
    MessageSourceService me;



    /**
     * valid 관련 Advice
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler(final MethodArgumentNotValidException e, WebRequest request) {
        String validMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return this.validExceptionHandler(e, request, validMessage);
    }


    /**
     * valid 관련 Advice
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationExceptionandler(final ConstraintViolationException e, WebRequest request) {
        String validMessage = e.getMessage();
        if(validMessage.indexOf(":") > -1) {
            validMessage = validMessage.substring(validMessage.indexOf(":") + 1);
            validMessage = validMessage.trim();
        }
        return this.validExceptionHandler(e, request, validMessage);
    }





    /**
     * 서비스시 발생하는 에러 Advice
     */@ExceptionHandler(Exception.class)

    public ResponseEntity exceptionHandler(final Exception e, WebRequest request) {
        ErrorCode exceptionType = ErrorCode.findException(e.getClass().getSimpleName());

        String path = request.getDescription(false);
        String exceptionKey = path + LocalDateTime.now();
//        String exceptionKey = EncryptionUtil.md5(path + LocalDateTime.now());

        HashMap<String, Object> hashMap = errorHashMap(e, request, exceptionKey);

        log.info("[Error] Exception   Code: {} , Msg: {}", exceptionType.getCode(), String.valueOf(exceptionType.getCode()));

        StringBuilder sb = new StringBuilder();
        for(StackTraceElement el : e.getStackTrace()){
            sb.append(request.getDescription(false)).append("-[").append(exceptionKey).append("] ").append(el.toString()).append("\n");
        }
        log.error(sb.toString());
        GeneralResponse response = new GeneralResponse<>(hashMap);
        response.setCode(String.valueOf(exceptionType.getCode()));
        response.setMsg(me.getLocaleMsg(String.valueOf(exceptionType.getCode()), Locale.KOREAN));

        return ResponseEntity.status(exceptionType.getStatus()).body(response);
    }



    /**
     * valid 공통 info
     */
    private ResponseEntity validExceptionHandler(final Exception e, WebRequest request, String validMessage) {
        String[] validMessages = validMessage.split(":");
        String errorCode;
        String subMessage = "";
        String localeMessage;
        String message = "";

        HashMap<String, Object> hashMap = errorHashMap(e, request);
        ErrorCode exceptionType;
        if(validMessages.length > 1) {
            exceptionType = ErrorCode.findException(validMessages[0].trim());
            errorCode = String.valueOf(exceptionType.getCode());
            subMessage = validMessages[1].trim();
        } else {
            exceptionType = ErrorCode.findException(validMessage.trim());
            errorCode = String.valueOf(exceptionType.getCode());
        }

        if(exceptionType == ErrorCode.Exception) {
            message = me.getLocaleMsg(errorCode, Locale.KOREAN);
            hashMap.put("message","Server Error, 개발자에게 문의하세요.");

            GeneralResponse response = new GeneralResponse<>(hashMap);
            response.setCode(errorCode);
            response.setMsg(message);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } else {
            message = "Bad Request";
        }

        localeMessage = me.getLocaleMsg(errorCode, request.getLocale());
        if(localeMessage.indexOf("{") > -1) {
            localeMessage = localeMessage.replace("{}", subMessage);
        } else {
            subMessage = subMessage != "" ? "(" + subMessage + ")" : "";
            localeMessage += subMessage;
        }
        hashMap.put("message",localeMessage);
        log.info("[Error] validException   Code: {} , Msg: {}", errorCode, message);

        GeneralResponse response = new GeneralResponse<>(hashMap);
        response.setCode(errorCode);
        response.setMsg(message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }



    /**
     * 공통 info
     */
    public HashMap<String, Object> errorHashMap(Exception e, WebRequest request) {
        HashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
        hashMap.put("path", request.getDescription(false));
        hashMap.put("error", e.getClass().getSimpleName());
        hashMap.put("time", LocalDateTime.now());
        hashMap.put("message",e.getMessage());

        return hashMap;
    }

    /**
     * exceptionKey 요걸로 좀더 알아야할때
     */
    public HashMap<String, Object> errorHashMap(Exception e, WebRequest request, String exceptionKey) {
        HashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
        String path = request.getDescription(false);

        hashMap.put("path", path);
        hashMap.put("error", e.getClass().getSimpleName());
        hashMap.put("time", LocalDateTime.now());
        hashMap.put("message",e.getMessage());
        if(exceptionKey != null) {
            hashMap.put("exceptionKey", exceptionKey);
        }
        return hashMap;
    }

}
