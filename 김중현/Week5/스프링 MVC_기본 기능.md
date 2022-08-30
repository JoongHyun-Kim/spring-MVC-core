# Section 6. 스프링 MVC - 기본 기능
## 로깅
- 운영 시스템에서는 System.out.println()같은 시스템 콘솔을 사용해서 필요한 정보를 출력하지 않고 별도의 로깅 라이브러리를 사용해서 로그를 출력한다.
<br>
<br>

#### 로깅 라이브러리
- 스프링 부트 라이브러리를 사용하면 스프링 부트 로깅 라이브러리(spring-boot-starter-logging)가 포함된다.
- 스프링 부트 로깅 라이브러리는 SLF4J와 Logback 로깅 라이브러리를 기본으로 사용한다.
<br>
<br>


#### LogTestController
```java
package hello.springmvc.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Slf4j
@RestController
public class LogTestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @RequestMapping("/log-test")
    public String logTest() {
        String name = "Spring";

        log.trace("trace log={}", name);
        log.debug("debug log={}", name);
        log.info(" info log={}", name);
        log.warn(" warn log={}", name);
        log.error("error log={}", name);

        //로그를 사용하지 않아도 a+b 계산 로직이 먼저 실행됨, 이런 방식으로 사용하면 X
        log.debug("String concat log=" + name);
        return "ok";
    } 
}
```
- trace 쪽으로 갈수록 level이 높아지는 것
<br>

#### 로그 선언
```java
private Logger log = LoggerFactory.getLogger(getClass());
private static final Logger log = LoggerFactory.getLogger(Xxx.class); //클래스 정보 주입
```
<br>

#### 로그 호출
```java
log.info("hello")
```
<br>

#### 매핑 정보
- @RestController
      - @Controller는 반환값이 String 이면 뷰 이름으로 인식되므로 뷰를 찾고 뷰가 랜더링된다. 그런데 @RestController는 반환값으로 <br>
        뷰를 찾는 것이 아니라 HTTP 메시지 바디에 바로 입력하기 때문에 실행 결과로 ok 메세지를 받을 수 있다. 
<br>

#### 테스트
- 로그가 출력되는 포멧을 확인한다.
    - 시간, 로그 레벨, 프로세스 ID, 쓰레드 명, 클래스명, 로그 메시지
- 로그 레벨 설정을 변경할 수 있다.
    - TRACE > DEBUG > INFO > WARN > ERROR 
    - 개발 서버는 debug, 운영 서버는 info 출력
<br>
<br>

#### 로그 사용 시 주의할 점
```
log.debug("data="+data)
```
로그 출력 레벨을 info로 설정해도 해당 코드에 있는 "data="+data가 실행이 되어버려서 더하기 연산이 발생한다. 
<br>

```
log.debug("data={}", data)
```
로그 출력 레벨을 info로 설정하면 아무일도 발생하지 않는다. 따라서 위와 같은 의미없는 연산이 발생하지 않는다.
<br>
<br>

#### 로그 사용시 장점
1. 쓰레드 정보, 클래스 이름 등의 부가 정보를 함께 볼 수 있고, 출력 모양을 조정할 수 있다.
2. 로그 레벨에 따라 개발 서버에서는 모든 로그를 출력하고, 운영서버에서는 출력하지 않는 등 로그를 상황에 맞게 조절할 수 있다.
3. 시스템 아웃 콘솔에만 출력하는 것이 아니라 파일이나 네트워크 등 로그를 별도의 위치에 남길 수 있다. <br>
   특히 파일로 남길 때는 일별, 특정 용량에 따라 로그를 분할하는 것도 가능하다.
4. 성능도 일반 System.out보다 좋다.(내부 버퍼링, 멀티 쓰레드 등) <br>
→ 실무에서는 꼭 로그를 사용해야 한다.
<br>
<br>
<br>
<br>

## 요청 매핑
> 요청이 왔을 때 어떤 컨트롤러가 호출이 될지 매핑하기
#### MappingController
```java
package hello.springmvc.basic.requestmapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MappingController {
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 기본 요청
     * 둘다 허용 /hello-basic, /hello-basic/
     * HTTP 메서드 모두 허용 GET, HEAD, POST, PUT, PATCH, DELETE
     */
    @RequestMapping("/hello-basic")
    public String helloBasic() {
        log.info("helloBasic");
        return "ok";
    }
}
```
- @RestController
    - @Controller는 반환값이 String이면 뷰 이름으로 인식되기 때문에 뷰를 찾고 뷰가 랜더링 된다.
    - @RestController는 반환값으로 뷰를 찾는 것이 아니라 HTTP 메시지 바디에 바로 입력하기 때문에 실행 결과로 ok 메세지를 받을 수 있다. 
- @RequestMapping("/hello-basic")
    - /hello-basic URL 호출이 오면 이 메소드가 실행되도록 매핑한다.
    - 대부분의 속성을 배열로 제공하기 때문에 다중 설정이 가능하다.(Ex. {"/hello-basic", "/hello-go"})
<br>
<br>
<br>

### 다양한 매핑
#### MappingController
```java
package hello.springmvc.basic.requestmapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class MappingController {
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 기본 요청
     * 둘다 허용 /hello-basic, /hello-basic/
     * HTTP 메서드 모두 허용 GET, HEAD, POST, PUT, PATCH, DELETE
     */
    @RequestMapping("/hello-basic")
    public String helloBasic() {
        log.info("helloBasic");
        return "ok";
    }

    /**
     * HTTP 메서드 매핑
     * method 특정 HTTP 메서드 요청만 허용
     * GET, HEAD, POST, PUT, PATCH, DELETE
     */
    @RequestMapping(value = "/mapping-get-v1", method = RequestMethod.GET)
    public String mappingGetV1() {
        log.info("mappingGetV1");
        return "ok";
    }

    /**
     * HTTP 메서드 매핑 축약
     * 편리한 축약 애노테이션
     * @GetMapping
     * @PostMapping
     * @PutMapping
     * @DeleteMapping
     * @PatchMapping
     */
    @GetMapping(value = "/mapping-get-v2")
    public String mappingGetV2() {
        log.info("mapping-get-v2");
        return "ok";
    }

    /**
     * PathVariable(경로변수) 사용
     * 변수명이 같으면 생략 가능
     * @PathVariable("userId") String userId -> @PathVariable userId
     * /mapping/userA
     */
    @GetMapping("/mapping/{userId}")
    public String mappingPath(@PathVariable("userId") String data) {
        log.info("mappingPath userId={}", data);
        return "ok";
    }

    /**
     * PathVariable 사용 (다중)
     */
    @GetMapping("/mapping/users/{userId}/orders/{orderId}")
    public String mappingPath(@PathVariable String userId, @PathVariable Long orderId) {
        log.info("mappingPath userId={}, orderId={}", userId, orderId);
        return "ok";
    }

    /**
     * 특정 파라미터 조건 매칭
     * 파라미터로 추가 매핑
     * params="mode",
     * params="!mode"
     * params="mode=debug"
     * params="mode!=debug" (! = )
     * params = {"mode=debug","data=good"}
     */
    @GetMapping(value = "/mapping-param", params = "mode=debug")
    public String mappingParam() {
        log.info("mappingParam");
        return "ok";
    }

    /**
     * 특정 헤더로 추가 매핑
     * headers="mode",
     * headers="!mode"
     * headers="mode=debug"
     * headers="mode!=debug" (! = )
     */
    @GetMapping(value = "/mapping-header", headers = "mode=debug")
    public String mappingHeader() {
        log.info("mappingHeader");
        return "ok";
    }

    /**
     * Content-Type 헤더 기반 추가 매핑 Media Type
     * consumes="application/json"
     * consumes="!application/json"
     * consumes="application/*"
     * consumes="*\/*"
     * MediaType.APPLICATION_JSON_VALUE
     */
    @PostMapping(value = "/mapping-consume", consumes = "application/json")
    public String mappingConsumes() {
        log.info("mappingConsumes");
        return "ok";
    }

    /**
     * Accept 헤더 기반 Media Type
     * * produces = "text/html"
     * produces = "!text/html"
     * * produces = "text/*"
     * produces = "*\/*"
     */
    @PostMapping(value = "/mapping-produce", produces = "text/html")
    public String mappingProduces() {
        log.info("mappingProduces");
        return "ok";
    }
}
```
<br>
<br>
<br>
<br>

## 요청 매핑 - API 예시
> 회원 관리를 하는 HTTP API를 만들 때 URL 매핑을 어떻게 하는지 예시를 통해 살펴보자!

### 회원 관리 API 설계
```
회원 목록 조회: GET /users
회원 등록: POST /users
회원 조회: GET /users/{userId}
회원수정: PATCH /users/{userId} 
회원 삭제: DELETE /users/{userId}
```
<br>
<br>

#### MappingClassController
```java
package hello.springmvc.basic.requestmapping;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mapping/users")
public class MappingClassController {
    /**
     * GET /mapping/users
     */
    @GetMapping
    public String users() {
        return "get users";
    }

    /**
     * POST /mapping/users
     */
    @PostMapping
    public String addUser() {
        return "post user";
    }

    /**
     * GET /mapping/users/{userId}
     */
    @GetMapping("/{userId}")
    public String findUser(@PathVariable String userId) {
        return "get userId=" + userId;
    }

    /**
     * PATCH /mapping/users/{userId}
     */
    @PatchMapping("/{userId}")
    public String updateUser(@PathVariable String userId) {
        return "update userId=" + userId;
    }

    /**
     * DELETE /mapping/users/{userId}
     */
    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable String userId) {
        return "delete userId=" + userId;
    }
}
```
<br>
<br>
<br>
<br>

## HTTP 요청 - 기본, 헤더 조회
> HTTP 헤더 정보를 조회하는 방법에 대해 알아보자!
#### RequestHeaderController
```java
package hello.springmvc.basic.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
   
@Slf4j
@RestController
public class RequestHeaderController {
    
    @RequestMapping("/headers")
    public String headers(HttpServletRequest request, 
                          HttpServletResponse response, 
                          HttpMethod httpMethod,
                          Locale locale,
                          @RequestHeader MultiValueMap<String, String> headerMap,
                          @RequestHeader("host") String host,
                          @CookieValue(value = "myCookie", required = false) String cookie) {
         log.info("request={}", request);
         log.info("response={}", response);
         log.info("httpMethod={}", httpMethod);
         log.info("locale={}", locale);
         log.info("headerMap={}", headerMap);
         log.info("header host={}", host);
         log.info("myCookie={}", cookie);
         
         return "ok";
     }
}
```
- @RequestHeader MultiValueMap<String, String> headerMap: 모든 HTTP 헤더를 `MultiValueMap` 형식으로 조회한다.
- @RequestHeader("host") String host: 특정 HTTP 헤더를 조회한다.
- @CookieValue(value = "myCookie", required = false) String cookie: 특정 쿠키를 조회한다.
<br>
<br>

#### `MultiValueMap`
- MAP과 유사하지만 하나의 키에 여러 값을 받을 수 있다.
- HTTP header, HTTP 쿼리 파라미터와 같이 하나의 키에 여러 값을 받을 때 사용한다.
```
keyA=value1&keyA=value2
```
```java
MultiValueMap<String, String> map = new LinkedMultiValueMap();
map.add("keyA", "value1");
map.add("keyA", "value2");
    
//[value1,value2]
List<String> values = map.get("keyA");
```
