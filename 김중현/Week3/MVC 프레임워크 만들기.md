# Section 4. MVC 프레임워크 만들기
## 프론트 컨트롤러 패턴 
<img width="600" alt="스크린샷 2022-08-09 오후 3 28 39" src="https://user-images.githubusercontent.com/80838501/183583984-89fd63bc-c5f6-43f2-945c-f558cd483f2f.png">


### FrontController 패턴 특징
- 프론트 컨트롤러 서블릿 하나로 클라이언트의 요청을 받는다.
- 프론트 컨트롤러가 요청에 맞는 컨트롤러를 찾아 호출해준다.
  - 전에는 클라이언트의 요청에 맞는 컨트롤러를 직접 호출했다.
- 즉, 입구를 하나로 만들어 공통 부분 처리가 가능해진 것이다.
- 이 때, 프론트 컨트롤러를 제외한 나머지 컨트롤러는 굳이 서블릿을 사용하지 않아도 된다.
<br>

### 스프링 웹 MVC와 프론트 컨트롤러
- 스프링 웹 MVC의 핵심도 바로 이 **FrontController**다.
  - 스프링 웹 MVC의 `DispatcherServlet`이 FrontController 패턴으로 구현되어 있다.
<br>
<br>

```md
이번 section에서는 프론트 컨트롤러를 단계적으로 도입해보자!
```
<br>
<br>
<br>
<br>

## 프론트 컨트롤러 도입 - v1
> v1에서는 기존 코드를 최대한 유지하면서 프론트 컨트롤러를 도입해보자!

- 클라이언트가 HTTP 요청을 하면 FrontController 서블릿이 요청을 받는다.
- 요청이 들어오면 매핑 정보를 뒤져 어떤 컨트롤러를 호출할지 찾은 다음 해당 컨트롤러를 호출한다.
- 호출된 컨트롤러는 자신의 로직을 실행하고 forward 로직으로 JSP를 호출해 HTML 응답을 내보낸다. 
<br>

#### ControllerV1
> 컨트롤러 인터페이스
```java
package hello.servlet.web.frontcontroller.v1;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ControllerV1 {

    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
```
- 서블릿과 비슷한 모양으로 controller를 인터페이스로 구현해두고, 각 controller들은 이 인터페이스를 구현하도록 설계한다.
- 프론트 컨트롤러는 이 인터페이스를 호출해 구현과 관계 없이 로직의 일관성을 유지할 수 있다.
