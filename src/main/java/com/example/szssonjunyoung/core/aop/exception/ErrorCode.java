package com.example.szssonjunyoung.core.aop.exception;


import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
@ToString
public enum ErrorCode {

    /**
     * ********************************************************************************************************
     * Exception Start
     */

    /**
     * System Exception
     */
      HttpRequestMethodNotSupportedException(HttpStatus.NOT_FOUND, "404")
    , RuntimeException(HttpStatus.BAD_REQUEST, "400")
    , IllegalArgumentException(HttpStatus.BAD_REQUEST, "400")
    , AccessDeniedException(HttpStatus.UNAUTHORIZED, "401")
    , NoHandlerFoundException(HttpStatus.NOT_FOUND, "404")
    , NestedServletException(HttpStatus.NOT_FOUND, "404")
    , ResourceNotFoundException(HttpStatus.NOT_FOUND, "404")
    , ParseException(HttpStatus.BAD_REQUEST, "400")
    , ArrayIndexOutOfBoundsException(HttpStatus.BAD_REQUEST, "400")
    , NoSuchMessageException(HttpStatus.BAD_REQUEST, "400")
    , MailException(HttpStatus.BAD_REQUEST, "400")
    , NullPointerException(HttpStatus.BAD_REQUEST, "400")
    //, MethodArgumentNotValidException(HttpStatus.BAD_REQUEST, 400)
    , MethodArgumentTypeMismatchException(HttpStatus.NOT_FOUND, "404")
    , RequestRejectedException(HttpStatus.NOT_FOUND, "404")
    , UnsupportedOperationException(HttpStatus.BAD_REQUEST, "400")

    /**
     * Sql Exception
     */
    , BadSqlGrammarException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , InvalidResultSetAccessException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , DuplicateKeyException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , DataIntegrityViolationException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , DataAccessResourceFailureException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , CannotAcquireLockException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , DeadlockLoserDataAccessException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , CannotSerializeTransactionException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , SQLSyntaxErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "500") //Not error catch!?
    , DataAccessException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , SQLNonTransientException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , SQLException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    /**
     * 외 모든 Exception
     */
    , JsonProcessingException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , IOException(HttpStatus.INTERNAL_SERVER_ERROR, "500")
    , Exception(HttpStatus.INTERNAL_SERVER_ERROR, "500")


    , HttpMessageNotReadableException(HttpStatus.BAD_REQUEST, "400")


    /**
     * Exception END
     * ********************************************************************************************************
     */







    /**
     * ********************************************************************************************************
     * Custom Exception Start
     * - 하기 경로의 errorCode와 매칭하여 사용
     * - resources/message/Resource Bundle 'messages'/messages_*.properties
     */
    , ERROR_401(HttpStatus.UNAUTHORIZED,"401")


    /**
     * 삼쩜삼 에러코드 정의,범위는 일단 1000 ~ 1999
     */
    , ERROR_SZSE1000(HttpStatus.BAD_REQUEST,"SZSE1000")
    , ERROR_SZSE1001(HttpStatus.BAD_REQUEST,"SZSE1001")
    , ERROR_SZSE1002(HttpStatus.BAD_REQUEST,"SZSE1002")
    , ERROR_SZSE1003(HttpStatus.BAD_REQUEST,"SZSE1003")
    , ERROR_SZSE1004(HttpStatus.BAD_REQUEST,"SZSE1004")
    , ERROR_SZSE1005(HttpStatus.BAD_REQUEST,"SZSE1005")
    , ERROR_SZSE1006(HttpStatus.BAD_REQUEST,"SZSE1006")
    , ERROR_SZSE1007(HttpStatus.BAD_REQUEST,"SZSE1007")
    , ERROR_SZSE1008(HttpStatus.BAD_REQUEST,"SZSE1008")
    ;


    /**
     * Custom Exception End
     * ********************************************************************************************************
     */


    private final HttpStatus status;
    private String code;

    ErrorCode(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }

    ErrorCode(HttpStatus status) {
        this.status = status;
    }

    public static ErrorCode findException(String exceptionName) {
        return Arrays.stream(ErrorCode.values())
                .filter(s -> s.name().equals(exceptionName))
                .findAny()
                .orElse(ErrorCode.Exception);
    }



}
