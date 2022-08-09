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
