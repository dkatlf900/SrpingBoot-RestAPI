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
     * 1000 ~ 1999, Error code 는 회원 관련 메세지만 정의한다.
     */
    , ERROR_AE1003(HttpStatus.BAD_REQUEST,"AE1003")
    , ERROR_AE1004(HttpStatus.BAD_REQUEST,"AE1004")
    , ERROR_AE1005(HttpStatus.BAD_REQUEST,"AE1005")
    , ERROR_AE1006(HttpStatus.BAD_REQUEST,"AE1006")
    , ERROR_AE1007(HttpStatus.BAD_REQUEST,"AE1007")
    , ERROR_AE1008(HttpStatus.BAD_REQUEST,"AE1008")
    , ERROR_AE1009(HttpStatus.BAD_REQUEST,"AE1009")
    , ERROR_AE1013(HttpStatus.BAD_REQUEST,"AE1013")
    , ERROR_AE1014(HttpStatus.BAD_REQUEST,"AE1014")
    , ERROR_AE1015(HttpStatus.BAD_REQUEST,"AE1015")
    , ERROR_AE1016(HttpStatus.BAD_REQUEST,"AE1016")
    , ERROR_AE1017(HttpStatus.BAD_REQUEST,"AE1017")
    , ERROR_AE1018(HttpStatus.BAD_REQUEST,"AE1018")
    , ERROR_AE1019(HttpStatus.BAD_REQUEST,"AE1019")
    , ERROR_AE1020(HttpStatus.BAD_REQUEST,"AE1020")
    , ERROR_AE1021(HttpStatus.BAD_REQUEST,"AE1021")
    , ERROR_AE1022(HttpStatus.BAD_REQUEST,"AE1022")
    , ERROR_AE1023(HttpStatus.BAD_REQUEST,"AE1023")
    , ERROR_AE1024(HttpStatus.BAD_REQUEST,"AE1024")
    , ERROR_AE1025(HttpStatus.BAD_REQUEST,"AE1025")
    , ERROR_AE1026(HttpStatus.BAD_REQUEST,"AE1026")
    , ERROR_AE1027(HttpStatus.BAD_REQUEST,"AE1027")
    , ERROR_AE1028(HttpStatus.BAD_REQUEST,"AE1028")
    , ERROR_AE1029(HttpStatus.BAD_REQUEST,"AE1029")
    , ERROR_AE1030(HttpStatus.BAD_REQUEST,"AE1030")
    , ERROR_AE1031(HttpStatus.BAD_REQUEST,"AE1031")
    , ERROR_AE1032(HttpStatus.BAD_REQUEST,"AE1032")


    /**
     * 1100 ~ 1199, Error code 는 회원가입 관련해서 정의하겠다.
     */
    , ERROR_AE1100(HttpStatus.BAD_REQUEST,"AE1100")
    , ERROR_AE1101(HttpStatus.BAD_REQUEST,"AE1101")
    , ERROR_AE1102(HttpStatus.BAD_REQUEST,"AE1102")
    , ERROR_AE1103(HttpStatus.BAD_REQUEST,"AE1103")
    , ERROR_AE1104(HttpStatus.BAD_REQUEST,"AE1104")
    , ERROR_AE1105(HttpStatus.BAD_REQUEST,"AE1105")


    /**
     * 5000 ~ 5999, Error code 는 OAuth2 관련 메세지만 정의한다.
     */
    , ERROR_AE5000(HttpStatus.BAD_REQUEST,"AE5000")
    , ERROR_AE5001(HttpStatus.BAD_REQUEST,"AE5001")
    , ERROR_AE5002(HttpStatus.BAD_REQUEST,"AE5002")
    , ERROR_AE5003(HttpStatus.BAD_REQUEST,"AE5003")
    , ERROR_AE5004(HttpStatus.BAD_REQUEST,"AE5004")
    , ERROR_AE5005(HttpStatus.BAD_REQUEST,"AE5005")
    , ERROR_AE5006(HttpStatus.BAD_REQUEST,"AE5006")
    , ERROR_AE5007(HttpStatus.BAD_REQUEST,"AE5007")
    , ERROR_AE5008(HttpStatus.BAD_REQUEST,"AE5008")
    , ERROR_AE5009(HttpStatus.BAD_REQUEST,"AE5009")
    , ERROR_AE5010(HttpStatus.BAD_REQUEST,"AE5010")
    , ERROR_AE5011(HttpStatus.BAD_REQUEST,"AE5011")
    , ERROR_AE5012(HttpStatus.BAD_REQUEST,"AE5012")
    , ERROR_AE5013(HttpStatus.BAD_REQUEST,"AE5013")
    , ERROR_AE5014(HttpStatus.BAD_REQUEST,"AE5014")
    , ERROR_AE5015(HttpStatus.BAD_REQUEST,"AE5015")
    , ERROR_AE5016(HttpStatus.BAD_REQUEST,"AE5016")
    , ERROR_AE5017(HttpStatus.BAD_REQUEST,"AE5017")
    , ERROR_AE5018(HttpStatus.BAD_REQUEST,"AE5018")
    , ERROR_AE5019(HttpStatus.BAD_REQUEST,"AE5019")



    , ERROR_AE5100(HttpStatus.UNAUTHORIZED,"AE5100")
    , ERROR_AE5101(HttpStatus.UNAUTHORIZED,"AE5101")
    , ERROR_AE5102(HttpStatus.UNAUTHORIZED,"AE5102")
    , ERROR_AE5103(HttpStatus.UNAUTHORIZED,"AE5103")


    /**
     * 6000~6200 관리기능
     */
    , ERROR_AE6000(HttpStatus.BAD_REQUEST,"AE6000")
    , ERROR_AE6001(HttpStatus.BAD_REQUEST,"AE6001")
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
