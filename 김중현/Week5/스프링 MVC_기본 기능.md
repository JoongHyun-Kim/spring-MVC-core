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
