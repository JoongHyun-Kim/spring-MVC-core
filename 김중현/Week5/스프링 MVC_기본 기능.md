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
<br>
<br>
<br>
<br>

## HTTP 요청 파라미터 - 쿼리 파라미터, HTML Form
> HTTP 요청 메시지를 통해 클라이언트에서 서버로 데이터를 전달하는 방법
```
1. GET - 쿼리 파라미터
- Ex. /url?username=hello&age=20
- 메시지 바디 없이, URL의 쿼리 파라미터에 데이터를 포함해서 전달
- 검색, 필터, 페이징등에서 많이 사용하는 방식
2. POST - HTML Form
- content-type: application/x-www-form-urlencoded
- 메시지 바디에 쿼리 파리미터 형식으로 전달 username=hello&age=20 
- Ex. 회원 가입, 상품 주문, HTML Form 사용
3. HTTP message body에 데이터를 직접 담아서 요청 
- HTTP API에서 주로 사용
- 데이터 형식은 주로 JSON 사용
- POST, PUT, PATCH
```
<br>
<br>

### 쿼리 파라미터, HTML Form
- HttpServletRequest의 request.getParameter()를 사용하면 GET 쿼리 파라미터 전송 방식과 POST HTML Form 전송 방식 모두 조회할 수 있다. <br>
→ 이를 요청 파라미터 조회라고 한다.
<br>
<br>

- `request.getParameter()`를 사용하면 요청 파라미터를 조회할 수 있다.
<br>
<br>
<br>
<br>

## HTTP 요청 파라미터 - @RequestParam
> 스프링이 제공하는 @RequestParam을 사용하면 요청 파라미터를 편리하게 사용할 수 있다.

#### requestParamV2
```java
/**
* @RequestParam 사용
* 파라미터 이름으로 바인딩
* @ResponseBody 추가
* View 조회를 무시하고 HTTP message body에 직접 해당 내용 입력 
*/
@ResponseBody
@RequestMapping("/request-param-v2")
public String requestParamV2(
        @RequestParam("username") String memberName,
        @RequestParam("age") int memberAge) {
        
            log.info("username={}, age={}", memberName, memberAge);
            return "ok";
        }
```
- @RequestParam: 파라미터 이름으로 바인딩
- @ResponseBody: View 조회를 무시하고 HTTP message body에 직접 해당 내용을 입력한다.
- @RequestParam의 name(value) 속성이 파라미터 이름으로 사용된다.
    - @RequestParam("username") String memberName <br>
      → request.getParameter("username")
<br>
<br>

#### requestParamV3
```java
/**
* @RequestParam 사용
* HTTP 파라미터 이름이 변수 이름과 같으면 @RequestParam(name="xx") 생략 가능 
*/
@ResponseBody
@RequestMapping("/request-param-v3")
public String requestParamV3(@RequestParam String username, @RequestParam int age) {
    log.info("username={}, age={}", username, age);
    return "ok";
}
```
<br>
<br>

#### requestParamV4
```java
/**
* @RequestParam 사용
* String, int 등의 단순 타입이면 @RequestParam 도 생략 가능 */
@ResponseBody
@RequestMapping("/request-param-v4")
public String requestParamV4(String username, int age) {
    log.info("username={}, age={}", username, age);
    return "ok";
}
```
<br>
<br>

#### requestRequired(파라미터 필수 여부)
```java
/**
     * @RequestParam.required
* /request-param-required -> username이 없으므로 예외 
* 
* /request-param-required?username= -> 빈문자로 통과 
* 
* /request-param-required
* int age -> null을 int에 입력하는 것은 불가능. 따라서 Integer 변경해야 함(또는 다음에 나오는 defaultValue 사용) 
*/
@ResponseBody
@RequestMapping("/request-param-required")
public String requestParamRequired(@RequestParam(required = true) String username, @RequestParam(required = false) Integer age) {
    log.info("username={}, age={}", username, age);
    return "ok";
}
```
1. 파라미터 이름만 사용
```
/request-param?username=
```
    - 파라미터 이름만 있고 값이 없는 경우 빈문자로 통과
<br>

2. 기본형(primitive)에 null 입력
```
/request-param 요청 
@RequestParam(required = false) int age
```
- null을 int에 입력하는 것은 불가능(500 예외 발생)
- null을 받을 수 있는 Integer로 변경하거나 뒤에 나오는 defaultValue 사용
<br>
<br>

#### requestParamDefault(기본값 적용)
```java
/**
* @RequestParam
* defaultValue 사용 
*
* 참고)
* defaultValue는 빈 문자의 경우에도 적용 
* /request-param-default?username=
*/
@ResponseBody
@RequestMapping("/request-param-default")
public String requestParamDefault(
    @RequestParam(required = true, defaultValue = "guest") String username, 
    @RequestParam(required = false, defaultValue = "-1") int age) {
      log.info("username={}, age={}", username, age);
      return "ok";
    }
```
- 파라미터에 값이 없는 경우 `defaultValue`를 사용하면 기본값을 적용할 수 있다. 이 때, 이미 기본 값이 있기 때문에 required는 의미가 없다.
-  defaultValue 는 빈 문자의 경우에도 설정한 기본 값이 적용된다.
<br>
<br>

#### requestParamMap(파라미터를 Map으로 조회)
```java
/**
 * @RequestParam Map, MultiValueMap
 * Map(key=value)
 * MultiValueMap(key=[value1, value2, ...]) ex) (key=userIds, value=[id1, id2])
 */
 @ResponseBody
 @RequestMapping("/request-param-map")
 public String requestParamMap(@RequestParam Map<String, Object> paramMap) {
      log.info("username={}, age={}", paramMap.get("username"), paramMap.get("age"));
      return "ok";
 }
```
- 파라미터를 `Map`, `MultiValueMap`으로 조회할 수 있다.
<br>
<br>
<br>
<br>

## HTTP 요청 파라미터 - @ModelAttribute
- 실제 개발을 할 때, 요청 파라미터를 받아서 필요한 객체를 만들고 그 객체에 값을 넣어주어야 한다.
```java
@RequestParam String username;
@RequestParam int age;
  
HelloData data = new HelloData();
data.setUsername(username);
data.setAge(age);
```
- 스프링은 위 과정을 자동화해주는 `@ModelAttribute` 기능을 제공한다.
<br>
<br>

#### HelloData
> 요청 파라미터를 바인딩받을 객체
```java
package hello.springmvc.basic;
 
import lombok.Data;

@Data
public class HelloData {
    private String username;
    private int age;
}
```
<br>
<br>
<br>

#### modelAttributeV1
> @ModelAttribute 적용
```java
/**
* @ModelAttribute 사용
* 참고)
* model.addAttribute(helloData) 코드도 함께 자동 적용됨
*/
@ResponseBody
@RequestMapping("/model-attribute-v1")
public String modelAttributeV1(@ModelAttribute HelloData helloData) {
    log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());
    return "ok";
}
```
- 자동으로 HelloData 객체가 생성되고, 요청 파라미터의 값도 모두 들어가 있다.
<br>

- 스프링MVC는 `@ModelAttribute`가 있으면 다음을 실행한다. 
```
HelloData 객체를 생성한다.
요청 파라미터의 이름으로 HelloData 객체의 프로퍼티를 찾고 해당 프로퍼티의 setter를 호출해 파라미터의 값을 입력(바인딩)한다.
Ex) 파라미터 이름이 username이면 setUsername() 메소드를 찾아 호출하면서 값을 입력한다.
```
<br>
<br>

#### 프로퍼티
```java
class HelloData {
    getUsername();
    setUsername();
}
```
- 객체에 getUsername(), setUsername() 메소드가 있으면 해당 객체는 username이라는 프로퍼티를 가지고 있다.
- username 프로퍼티의 값을 변경하면 setUsername()이 호출되고, 조회하면 getUsername()이 호출된다.
<br>
<br>

#### modelAttributeV2
> @ModelAttribute 생략
```java
/**
* @ModelAttribute 생략 가능
* String, int 같은 단순 타입 = @RequestParam
* argument resolver 로 지정해둔 타입 외 = @ModelAttribute 
*/
@ResponseBody
@RequestMapping("/model-attribute-v2")
public String modelAttributeV2(HelloData helloData) {
    log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());
    return "ok";
}
```
- @ModelAttribute는 생략할 수 있다. 하지만 @RequestParam도 생략할 수 있어 혼란이 발생할 수 있다.
- 이 때, 스프링은 다음과 같은 규칙을 적용한다.
```
String, int, Integer 같은 단순 타입 = @RequestParam
나머지 = @ModelAttribute (argument resolver로 지정해둔 타입 외)
```
<br>
<br>
<br>
<br>

## HTTP 요청 메시지 - 단순 텍스트
- 요청 파라미터와 다르게 HTTP 메시지 바디를 통해 데이터가 직접 넘어오는 경우에는 `@RequestParam`, `@ModelAttribute`를 사용할 수 없다. 
<br>

- 가장 단순한 텍스트 메시지를 HTTP 메시지 바디에 담아서 전송하고, 읽어보는 예제를 살펴보자.
    - HTTP 메시지 바디의 데이터를 InputStream 을 사용해서 직접 읽을 수 있다.
#### RequestBodyStringController
```java
@Slf4j
@Controller
public class RequestBodyStringController {
    @PostMapping("/request-body-string-v1")
    public void requestBodyString(HttpServletRequest request, HttpServletResponse response) throws IOException {
          ServletInputStream inputStream = request.getInputStream();
          String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
          log.info("messageBody={}", messageBody);
          response.getWriter().write("ok");
    }
}
``` 
<br>
<br>

#### requestBodyStringV2(Input, Output 스트림, Reader)
```java
/**
* InputStream(Reader): HTTP 요청 메시지 바디의 내용을 직접 조회 
* OutputStream(Writer): HTTP 응답 메시지의 바디에 직접 결과 출력 
*/
@PostMapping("/request-body-string-v2")
public void requestBodyStringV2(InputStream inputStream, Writer responseWriter) throws IOException {
     String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
     log.info("messageBody={}", messageBody);
     responseWriter.write("ok");
}
```
- 스프링 MVC는 아래 파라미터를 지원한다.
    - InputStream(Reader): HTTP 요청 메시지 바디 내용을 직접 조회 
    - OutputStream(Writer): HTTP 응답 메시지의 바디에 직접 결과 출력
<br>
<br>

#### requestBodyStringV3(HttpEntity)
```java
/**
* HttpEntity: HTTP header, body 정보를 편리하게 조회
* - 메시지 바디 정보를 직접 조회(@RequestParam X, @ModelAttribute X)
* - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용 
*
* 응답에서도 HttpEntity 사용 가능
* - 메시지 바디 정보 직접 반환(view 조회X)
* - HttpMessageConverter 사용 
* -> StringHttpMessageConverter 적용
*/
@PostMapping("/request-body-string-v3")
public HttpEntity<String> requestBodyStringV3(HttpEntity<String> httpEntity) {
    String messageBody = httpEntity.getBody();
    log.info("messageBody={}", messageBody);
      
    return new HttpEntity<>("ok");
}
```
- 스프링 MVC는 아래 파라미터를 지원한다.
    - HttpEntity: HTTP header, body 정보를 편리하게 조회
        - 메시지 바디 정보를 직접 조회
        - 요청 파라미터를 조회하는 기능과 관계 없다. `@RequestParam` X, `@ModelAttribute` X 
    - HttpEntity는 응답에도 사용 가능하다.
        - 메시지 바디 정보 직접 반환 
        - 헤더 정보 포함 가능
        - view 조회X
<br>

#### 참고
> HttpEntity 를 상속받은 다음 객체들도 같은 기능을 제공한다. 
- RequestEntity
    - HttpMethod, url 정보가 추가됨
    - 요청에서 사용 
- ResponseEntity
    - HTTP 상태 코드 설정 가능
    - 응답에서 사용
<br>
<br>

#### requestBodyStringV4(@RequestBody)
```java
/**
* @RequestBody
* - 메시지 바디 정보를 직접 조회(@RequestParam X, @ModelAttribute X)
* - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용 
* 
* @ResponseBody
* - 메시지 바디 정보 직접 반환(view 조회X)
* - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용 
*/
@ResponseBody
@PostMapping("/request-body-string-v4")
public String requestBodyStringV4(@RequestBody String messageBody) {
    log.info("messageBody={}", messageBody);
    return "ok";
}
```
### @RequestBody
- @RequestBody를 사용하면 HTTP 메시지 바디 정보를 편리하게 조회할 수 있다. 
- 헤더 정보가 필요하면 HttpEntity나 @RequestHeader를 사용하면 된다.
- 이런 메시지 바디를 직접 조회하는 기능은 요청 파라미터를 조회하는 @RequestParam, @ModelAttribute와는 전혀 관계가 없다.
<br>
<br>

### 요청 파라미터 vs HTTP 메시지 바디
- 요청 파라미터를 조회하는 기능: @RequestParam, @ModelAttribute 
- HTTP 메시지 바디를 직접 조회하는 기능: @RequestBody
<br>
<br>
<br>
<br>
    
## HTTP 요청 메시지 - JSON
> HTTP API에서 주로 사용하는 JSON 데이터 형식을 조회하는 것에 대해 알아보자!
#### RequestBodyJsonController
```java
package hello.springmvc.basic.request;
  
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.springmvc.basic.HelloData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
  
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
/**
* {"username":"hello", "age":20}
* content-type: application/json
*/
@Slf4j
@Controller
public class RequestBodyJsonController {
    private ObjectMapper objectMapper = new ObjectMapper();
      
    @PostMapping("/request-body-json-v1")
    public void requestBodyJsonV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
          ServletInputStream inputStream = request.getInputStream();
          String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
          
          log.info("messageBody={}", messageBody);
          HelloData data = objectMapper.readValue(messageBody, HelloData.class);
          log.info("username={}, age={}", data.getUsername(), data.getAge());
          
          response.getWriter().write("ok");
      }
}
```
- HttpServletRequest를 사용해 직접 HTTP 메시지 바디에서 데이터를 읽어와 문자로 변환한다.
- 문자로 된 JSON 데이터를 Jackson 라이브러리인 `objectMapper`를 사용해 자바 객체로 변환한다.
<br>
<br>

#### RequestBodyJsonV2
> @RequestBody 문자 변환
```java
/**
* @RequestBody
* HttpMessageConverter 사용 -> StringHttpMessageConverter 적용 *
* @ResponseBody
* - 모든 메서드에 @ResponseBody 적용
* - 메시지 바디 정보 직접 반환(view 조회X)
* - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
*/
@ResponseBody
@PostMapping("/request-body-json-v2")
public String requestBodyJsonV2(@RequestBody String messageBody) throws IOException {
    HelloData data = objectMapper.readValue(messageBody, HelloData.class);
    log.info("username={}, age={}", data.getUsername(), data.getAge());
    return "ok";
}
```
- 전에 배웠던 @RequestBody를 사용해 HTTP 메시지에서 데이터를 꺼내고 messageBody에 저장한다.
- 문자로 된 JSON 데이터인 messageBody를 objectMapper를 통해 자바 객체로 변환한다.
<br>

> 문자로 변환하고 다시 json으로 변환하는 과정이 불필요하게 느껴진다. <br>
>  @ModelAttribute처럼 한번에 객체로 변환할 수는 없을까?
#### requestBodyJsonV3
> @RequestBody 객체 변환
```java
/**
* @RequestBody 생략 불가능(@ModelAttribute가 적용되어 버린다.)
* HttpMessageConverter 사용 
* -> MappingJackson2HttpMessageConverter (content-type: application/json)
*
*/
@ResponseBody
@PostMapping("/request-body-json-v3")
public String requestBodyJsonV3(@RequestBody HelloData data) {
    log.info("username={}, age={}", data.getUsername(), data.getAge());
    return "ok";
}
```
- HelloData에 @RequestBody를 생략하면 @ModelAttribute가 적용되어버리기 때문에, HTTP 메시지 바디가 아니라 요청 파라미터를 처리하게 된다.
<br>

### @RequestBody 객체 파라미터
- RequestBody에 직접 만든 객체를 지정할 수 있다.
- HttpEntity, @RequestBody를 사용하면 `HTTP 메시지 컨버터`가 HTTP 메시지 바디의 내용을 원하는 문자나 객체 등으로 변환해준다.
- HTTP 메시지 컨버터는 문자 뿐만 아니라 JSON도 객체로 변환해주고, V2에서 했던 작업을 대신 처리해준다.
<br>
<br>

#### RequestBodyJsonV4
> HttpEntity
```java
@ResponseBody
@PostMapping("/request-body-json-v4")
public String requestBodyJsonV4(HttpEntity<HelloData> httpEntity) {
    HelloData data = httpEntity.getBody();
    log.info("username={}, age={}", data.getUsername(), data.getAge());
    return "ok";
}
```
<br>
<br>

#### requestBodyJsonV5
```java
/**
* @RequestBody 생략 불가능(@ModelAttribute가 적용되어 버린다.)
* HttpMessageConverter 사용 
* -> MappingJackson2HttpMessageConverter (content- type: application/json)
*
* @ResponseBody 적용
* - 메시지 바디 정보 직접 반환(view 조회X)
* - HttpMessageConverter 사용 
* -> MappingJackson2HttpMessageConverter 적용 (Accept: application/json)
*/
@ResponseBody
@PostMapping("/request-body-json-v5")
public HelloData requestBodyJsonV5(@RequestBody HelloData data) {
    log.info("username={}, age={}", data.getUsername(), data.getAge());
    return data;
}
```
### @ResponseBody
- 응답의 경우에도 @ResponseBody를 사용하면 해당 객체를 HTTP 메시지 바디에 직접 넣어줄 수 있다.
- 마찬가지로 이 경우에도 HttpEntity를 사용해도 된다.
<br>

#### 정리
```
@RequestBody 요청: JSON 요청 → HTTP 메시지 컨버터 → 객체
@ResponseBody 응답: 객체 → HTTP 메시지 컨버터 → JSON 응답
```
<br>
<br>
<br>
<br>

## HTTP 응답 - 정적 리소스, 뷰 템플릿
스프링에서 응답 데이터를 만드는 방법은 크게 3가지이다.
```
1) 정적 리소스
- Ex. 웹 브라우저에 정적인 HTML, css, js를 제공할 때는, 정적 리소스를 사용한다.
2) 뷰 템플릿 사용
- Ex. 웹 브라우저에 동적인 HTML을 제공할 때는 뷰 템플릿을 사용한다.
3) HTTP 메시지 사용
- HTTP API를 제공하는 경우에는 HTML이 아니라 데이터를 전달해야 하므로, HTTP 메시지 바디에 JSON같은 형식으로 데이터를 담아 보낸다.
```
<br>

### 정적 리소스
스프링 부트는 클래스 패스의 다음 디렉토리에 있는 정적 리소스를 제공한다. <br>
`/static`, `/public`, `/resources`, `/META-INF/resources`
- src/main/resources는 리소스를 보관하는 곳인 동시에 클래스패스의 시작 경로이다. <br>
  그러므로 위의 디렉토리에 리소스를 넣어두면 스프링 부트가 정적 리소스로 서비스를 제공한다.
<br>
<br>

### 뷰 템플릿
뷰 템플릿을 거쳐서 HTML이 생성되고, 뷰가 응답을 만들어서 전달한다.
일반적으로 HTML을 동적으로 생성하는 용도로 사용하지만, 뷰 템플릿이 만들 수 있는 것이면 어떤 것이라도 가능하다.
- 스프링 부트가 제공하는 기본 뷰 템플릿 경로는 `src/main/resources/templates`이다.

#### 뷰 템플릿 생성 예제
```java
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <p th:text="${data}">empty</p>
</body>
</html>
```
<br>
<br>

#### ResponseViewController
> 뷰 템플릿을 호출하는 컨트롤러
```java
package hello.springmvc.basic.response;
  
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
@Controller
public class ResponseViewController {

    @RequestMapping("/response-view-v1")
    public ModelAndView responseViewV1() {
        ModelAndView mav = new ModelAndView("response/hello").addObject("data", "hello!");
        return mav; 
    }
      
    @RequestMapping("/response-view-v2")
    public String responseViewV2(Model model) {
        model.addAttribute("data", "hello!!");
        return "response/hello";
    }
    
    @RequestMapping("/response/hello")
    public void responseViewV3(Model model) {
        model.addAttribute("data", "hello!!");
    }
}
```
<br>
<br>

#### String을 반환하는 경우
> View or HTTP 메시지

@ResponseBody가 없으면 response/hello로 뷰 리졸버가 실행되어서 뷰를 찾고, 렌더링 한다. <br>
@ResponseBody가 있으면 뷰 리졸버를 실행하지 않고 HTTP 메시지 바디에 직접 response/hello 문자가 입력된다.
<br>
<br>

#### Void를 반환하는 경우
@Controller를 사용하고 HttpServletResponse, OutputStream(Writer) 같은 HTTP 메시지 바디를 처리하는 파라미터가 없으면 <br>
요청 URL을 참고해서 논리 뷰 이름으로 사용한다.
<br>
<br>
<br>

### HTTP 메시지
`@ResponseBody`, `HttpEntity`를 사용하면 HTTP 메시지 바디에 직접 응답 데이터를 출력할 수 있다.
<br>
<br>
<br>
<br>

## HTTP 응답 - HTTP API, 메시지 바디에 직접 입력
> HTTP API를 제공하는 경우에는 HTML이 아니라 데이터를 전달해야 하므로 HTTP 메시지 바디에 JSON같은 형식으로 데이터를 담아 보낸다. <br>
> 정적 리소스나 뷰 템플릿 없이 직접 HTTP 응답 메시지를 전달하는 경우에 대해 정리해보자!

#### ResponseBodyController
```java
package hello.springmvc.basic.response;
    
import hello.springmvc.basic.HelloData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
public class ResponseBodyController {
    
    @GetMapping("/response-body-string-v1")
    public void responseBodyV1(HttpServletResponse response) throws IOException {
        response.getWriter().write("ok");
    }

    /**
    * HttpEntity, ResponseEntity(Http Status 추가)
    * @return
    */
    @GetMapping("/response-body-string-v2")
    public ResponseEntity<String> responseBodyV2() {
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
    
    @ResponseBody
    @GetMapping("/response-body-string-v3")
    public String responseBodyV3() {
        return "ok";
    }
    
    @GetMapping("/response-body-json-v1")
    public ResponseEntity<HelloData> responseBodyJsonV1() {
        HelloData helloData = new HelloData();
        helloData.setUsername("userA");
        helloData.setAge(20);
        
        return new ResponseEntity<>(helloData, HttpStatus.OK);
    }
    
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/response-body-json-v2")
    public HelloData responseBodyJsonV2() {
        HelloData helloData = new HelloData();
        helloData.setUsername("userA");
        helloData.setAge(20);
          
        return helloData;
    } 
}
```
#### responseBodyV1
```java
response.getWriter().write("ok")
```
- 서블릿을 다룰 때처럼 HttpServletResponse 객체를 통해 HTTP 메시지 바디에 직접 ok 응답 메시지를 전달한다.
<br>
<br>

#### responseBodyV2
- ResponseEntity는 HttpEntity를 상속 받는데 HttpEntity는 HTTP 메시지의 헤더, 바디 정보를 가지고 있다.
    - ResponseEntity는 여기에 더해 HTTP 응답 코드를 설정할 수 있다.
<br>
<br>

#### responseBodyV3
- ResponseBody를 사용하면 view를 사용하지 않고 HTTP 메시지 컨버터를 통해 HTTP 메시지를 직접 입력할 수 있다.<br>
  ResponseEntity도 동일한 방식으로 동작한다.
<br>
<br>

#### responseBodyJsonV1
- ResponseEntity를 반환한다. HTTP 메시지 컨버터를 통해 JSON 형식으로 변환되어 반환된다.
<br>
<br>

#### responseBodyJsonV2
- ResponseEntity는 HTTP 응답 코드를 설정할 수 있는데 반해 @ResponseBody를 사용하면 이런 것을 설정하기 까다롭다.
- @ResponseStatus(HttpStatus.OK) 애노테이션을 사용하면 응답 코드도 설정할 수 있다.
    - 애노테이션이기 때문에 응답 코드를 동적으로 변경할 수 없다.
    - 동적으로 변경하고 싶으면 ResponseEntity를 사용하면 된다.
<br>
<br>

#### @RestController
- 이름 그대로 Rest API(HTTP API)를 만들 때 사용하는 컨트롤러이다.
- @Controller 대신 @RestController 애노테이션을 사용하면 해당 컨트롤러에 모두 @ResponseBody가 적용되는 효과가 있다. <br>
  따라서 뷰 템플릿을 사용하는 것이 아니라 HTTP 메시지 바디에 직접 데이터를 입력한다. 
<br>
<br>
<br>
<br>

## HTTP 메시지 컨버터
> 뷰 템플릿으로 HTML을 생성해서 응답하는 것이 아니라, HTTP API처럼 JSON 데이터를 HTTP 메시지 바디에서 직접 읽거나 쓰는 경우 HTTP 메시지 컨버터를 사용하면 편리하다.

스프링 MVC는 다음의 경우에 HTTP 메시지 컨버터를 적용한다.
```
HTTP 요청: @RequestBody, HttpEntity(RequestEntity)
HTTP 응답: @ResponseBody, HttpEntity(ResponseEntity)
```
<br>
<br>

#### HTTP 메시지 컨버터 인터페이스
```java
package org.springframework.http.converter;
  
public interface HttpMessageConverter<T> {
  boolean canRead(Class<?> clazz, @Nullable MediaType mediaType);
  boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType);
  List<MediaType> getSupportedMediaTypes();
   
  T read(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException;
    
  void write(T t, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException;
}
```
<br>
<br>
<br>

### 스프링 부트 기본 메시지 컨버터
```
0 = ByteArrayHttpMessageConverter
1 = StringHttpMessageConverter
2 = MappingJackson2HttpMessageConverter
```
- 스프링 부트는 다양한 메시지 컨버터를 제공하는데, 대상 클래스 타입과 미디어 타입을 체크해서 사용여부를 결정한다. <br>
  만족하지 않는 경우에는 다음 메시지 컨버터로 우선순위가 넘어간다.
<br>
<br>

#### 주요 메시지 컨버터
```
1. ByteArrayHttpMessageConverter: byte[] 데이터를 처리한다.
    - 클래스 타입: byte[], 미디어타입: */* ,
    - 요청 예시) @RequestBody byte[] data
    - 응답 예시) @ResponseBody return byte[] 
        - 미디어타입 application/octet-stream
2. StringHttpMessageConverter: String 문자로 데이터를 처리한다. 
    - 클래스 타입: String, 미디어타입: */*
    - 요청 예시) @RequestBody String data
    - 응답 예시) @ResponseBody return "ok" 
        - 미디어타입 text/plain
3. MappingJackson2HttpMessageConverter: application/json
    - 클래스 타입: 객체 또는 HashMap, 미디어타입 application/json 관련
    - 요청 예시) @RequestBody HelloData data
    - 응답 예시) @ResponseBody return helloData
        - 미디어타입 application/json 관련
```
<br>
<br>
<br>

### 정리
#### HTTP 요청 데이터 읽기
- HTTP 요청이 오고 컨트롤러에서 @RequestBody, HttpEntity 파라미터를 사용한다. 
- 메시지 컨버터가 메시지를 읽을 수 있는지 확인하기 위해 canRead()를 호출한다.
    - 1. 대상 클래스 타입을 지원하는가
        - Ex) @RequestBody의 대상 클래스(byte[], String, HelloData)
    - 2. HTTP 요청의 Content-Type 미디어 타입을 지원하는가
        - Ex) text/plain, application/json, */*
- canRead() 조건을 만족하면, read()를 호출해서 객체를 생성하고 반환한다.
<br>
<br>

#### HTTP 응답 데이터 생성
- 컨트롤러에서 @ResponseBody, HttpEntity로 값이 반환된다.
- 메시지 컨버터가 메시지를 쓸 수 있는지 확인하기 위해 canWrite() 를 호출한다.
    - 1. 대상 클래스 타입을 지원하는가
        - Ex) return의 대상 클래스 (byte[],  String, HelloData)
    - 2. HTTP 요청의 Accept 미디어 타입을 지원하는가(더 정확히는 @RequestMapping의 produces) 
        - Ex) text/plain, application/json, */*
- canWrite() 조건을 만족하면 write()를 호출해 HTTP 응답 메시지 바디에 데이터를 생성한다.
<br>
<br>
<br>
<br>

## 요청 매핑 핸들러 어댑터 구조
> HTTP 메시지 컨버터가 스프링 MVC 구조의 어디쯤에서 사용되는 것인지 알아보자!

@RequestMapping을 처리하는 핸들러 어댑터인 RequestMappingHandlerAdapter를 살펴보자.
<br>

### RequestMappingHandlerAdapter 동작 방식
<img width="558" alt="스크린샷 2022-08-30 오후 5 29 09" src="https://user-images.githubusercontent.com/80838501/187388978-497fcd41-89d4-4b29-a7d1-12a58af7cc44.png">

#### ArgumentResolver
```java
public interface HandlerMethodArgumentResolver {
      
    boolean supportsParameter(MethodParameter parameter);
      
    @Nullable
    Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
              NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception;
}
```
애노테이션 기반의 컨트롤러는 매우 다양한 파라미터를 사용할 수 있다. 지금까지 살펴본 바로는 HttpServletRequest, Model뿐만 아니라 
@RequestParam, @ModelAttribute 같은 애노테이션, 그리고 @RequestBody, HttpEntity 같은 HTTP 메시지를 처리하는 
파라미터까지 사용할 수 있었다.
이와 같이 파라미터를 유연하게 처리할 수 있는 이유가 바로 `ArgumentResolver` 덕분이다.
애노테이션 기반 컨트롤러를 처리하는 **RequestMappingHandlerAdapter**는 `ArgumentResolver`를 호출해서 
컨트롤러(핸들러)가 필요로 하는 다양한 파라미터의 값(객체)을 생성한 뒤 컨트롤러를 호출하면서 값을 넘겨준다.
<br>
<br>

#### 동작 방식
ArgumentResolver의 supportsParameter()를 호출해 해당 파라미터를 지원하는지 체크하고, 만약 지원하면 resolveArgument()를 
호출해서 실제 객체를 생성한다. 그리고 이렇게 생성된 객체가 컨트롤러 호출 시 넘어간다.
<br>
<br>
<br>

#### HTTP 메시지 컨버터 위치
<img width="592" alt="스크린샷 2022-08-30 오후 5 43 51" src="https://user-images.githubusercontent.com/80838501/187391984-f4990b5f-3a54-4074-8bd4-b499ab8c5c11.png">

**요청**
```
@RequestBody를 처리하는 ArgumentResolver가 있고 HttpEntity를 처리하는 ArgumentResolver가 있다. 
이 ArgumentResolver들이 HTTP 메시지 컨버터를 사용해서 필요한 객체를 생성한다.
```
<br>
<br>

**응답**
```
@ResponseBody와 HttpEntity를 처리하는 ReturnValueHandler가 있다. 여기에서 HTTP 메시지 컨버터를 호출해서 응답 결과를 만든다.
```
<br>
<br>
<br>

#### 확장
- 스프링은 다음을 모두 인터페이스로 제공하기 때문에, 필요에 따라 언제든지 기능을 확장할 수 있다.
    - `HandlerMethodArgumentResolver`, `HandlerMethodReturnValueHandler`, `HttpMessageConverter`
- 실제 자주 사용되지는 않지만, 기능 확장이 필요하면 WebMvcConfigurer를 상속 받아서 스프링 빈으로 등록하면 된다. 
<br>

#### WebMvcConfigurer 확장
```java
@Bean
public WebMvcConfigurer webMvcConfigurer() {
    return new WebMvcConfigurer() {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            //...
        }

        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
            //...
        } 
    };
}
```
