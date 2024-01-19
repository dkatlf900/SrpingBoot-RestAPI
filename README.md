## 스펙
* Spring Boot 3.2.1
* java 17
* JPA, H2
* Gradle
* yaml
* lombok
* Swagger

## Swagger url
* http://localhost:8080/swagger-ui/index.html

## H2 Embeded DB
* http://localhost:8080/h2-console
* url: jdbc:h2:mem:testdb
* login: sa
* pw: test

---

## 요구사항
##### 1.사용자가 삼쩜삼에 가입 또는 로그인 
##### 2.가입한 유저의 정보를 스크랩 하여 환급액이 있는지 조회.
##### 3.조회한 금액을 계산한 후 유저에게 실제 환급액을 알려줌


# 구현
#### 1. 회원가입 API
* pw: bcrypt 단방향으로 암호화  
* 주민번호: aes256 암복호화 한다.
---
* Table : usersEntity
* 컬럼 : 아이디 / 주민번호는 unique key 설정

# 1. 다음의 요건을 만족하는 회원 가입 API를 작성해 주세요
   회원 가입
   엔드포인트: /szs/signup
   Method : POST
   
# 2. 가입한 회원을 로그인 하는 API를 작성해주세요
엔드포인트: /szs/login
Method : POST

# 3. 가입한 회원 정보를 가져오는 API를 작성해주세요
내 정보 보기
엔드포인트: /szs/me
Method : GET

# 4. 가입한 유저의 정보를 스크랩 합니다.
다음 요건을 만족하는 유저 스크랩 API를 작성해 주세요.
엔드포인트: /szs/scrap
Method : POST

# 5. 유저의 스크랩 정보를 바탕으로 유저의 결정세액과 퇴직연금세액공제금
액을 계산합니다.
엔드포인트: /szs/refund
Method : GET

