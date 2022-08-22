# Section 5. 스프링 MVC - 구조 이해
## 스프링 MVC 전체 구조
### 스프링 MVC 구조
<img width="837" alt="스크린샷 2022-08-22 오전 11 03 35" src="https://user-images.githubusercontent.com/80838501/185824576-fbf591ad-c71e-4042-9143-8fc0451c7fb5.png">

 - 전체 구조는 여태 만들었던 MVC 프레임워크 구조와 동일하고, 이름 정도만 변경되었다.
 ```
 FrontController → DispatcherServlet
 handlerMappingMap → HandlerMapping
 MyHandlerAdapter → HandlerAdapter
 ModelView → ModelAndView
 viewResolver → ViewResolver(인터페이스)
 MyView → View(인터페이스)
 ```
 
 #### 동작 순서
 ```
1. 핸들러 조회: 핸들러 매핑을 통해 요청 URL에 매핑된 핸들러(컨트롤러)를 조회한다.
2. 핸들러 어댑터 조회: 핸들러를 실행할 수 있는 핸들러 어댑터를 조회한다.
3. 핸들러 어댑터 실행: 핸들러 어댑터를 실행한다.
4. 핸들러 실행: 핸들러 어댑터가 실제 핸들러를 실행한다.
5. ModelAndView 반환: 핸들러 어댑터는 핸들러가 반환하는 정보를 ModelAndView로 변환해서 반환한다.
6. viewResolver 호출: 뷰 리졸버를 찾고 실행한다.
      JSP의 경우, InternalResourceViewResolver가 자동 등록되고 사용된다.
7. View반환: 뷰 리졸버는 뷰의 논리 이름을 물리 이름으로 바꾸고, 렌더링 역할을 담당하는 뷰 객체를 반환한다.
      JSP의 경우, InternalResourceView(JstlView)를 반환하는데, 내부에 forward() 로직이 있다.
8. 뷰렌더링: 뷰를 통해서 뷰를 렌더링한다.
 ```
 <br>
 <br>
 
 ### DispatcherServlet 구조
 > org.springframework.web.servlet.DispatcherServlet
- 스프링 MVC도 프론트 컨트롤러 패턴으로 구현되어 있는데, 그 프론트 컨트롤러가 `DispatcherServlet`이다.
<br>

#### DispatcherServlet 등록
- DispatcherServlet도 부모 클래스로부터 HttpServlet을 상속 받아 사용하고 서블릿으로 동작한다.
<img width="886" alt="스크린샷 2022-08-22 오전 11 17 11" src="https://user-images.githubusercontent.com/80838501/185825831-a62083ad-a896-473d-9233-5226794fa82b.png">

- 스프링 부트는 내장 WAS를 띄우면서 DispatcherServlet을 서블릿으로 자동 등록해 모든 경로(`urlPatterns="/"`)에 대해 매핑한다.
  - 더 자세한 경로가 우선순위가 높기 때문에 기존에 등록한 서블릿도 함께 동작할 수 있다.
<br>
<br>

#### 요청 흐름
- 서블릿이 호출되면 HttpServlet이 제공하는 `service()`가 호출된다.
  - 스프링 MVC는 DispatcherServlet의 부모인 FrameworkServlet에서 service()를 오버라이드 해두었다.
- FrameworkServlet.service()를 시작으로 여러 메소드가 호출되면서 최종적으로 Dispatcher.doDispatch()가 호출된다.
<br>

#### DispatcherServlet.doDispatch()
```java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
    HttpServletRequest processedRequest = request;
    HandlerExecutionChain mappedHandler = null;
    ModelAndView mv = null;
    
    // 1. 핸들러 조회
    mappedHandler = getHandler(processedRequest); 
    if (mappedHandler == null) {
        noHandlerFound(processedRequest, response);
        return; 
    }

    //2.핸들러 어댑터 조회-핸들러를 처리할 수 있는 어댑터
    HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

    // 3. 핸들러 어댑터 실행 -> 4. 핸들러 어댑터를 통해 핸들러 실행 -> 5. ModelAndView 반환 
    mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
    processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
}

private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, HandlerExecutionChain mappedHandler, ModelAndView mv, Exception exception) throws Exception {
    // 뷰 렌더링 호출
    render(mv, request, response);
}

protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
    View view;
    String viewName = mv.getViewName(); //getViewName()을 이용해 논리 이름을 가지고 실제 경로 찾기
    
    //6. 뷰 리졸버를 통해서 뷰 찾기, 7.View 반환
    view = resolveViewName(viewName, mv.getModelInternal(), locale, request);

    // 8. 뷰 렌더링
    view.render(mv.getModelInternal(), request, response);
}
```
<br>
<br>

### 주요 인터페이스 목록
> 스프링 MVC의 큰 강점은 `DispatcherServlet` 코드의 변경 없이, 인터페이스를 구현해 원하는 기능을 변경하거나 확장할 수 있다는 점이다. 
```
핸들러 매핑: org.springframework.web.servlet.HandlerMapping 
핸들러 어댑터: org.springframework.web.servlet.HandlerAdapter 
뷰 리졸버: org.springframework.web.servlet.ViewResolver
뷰: org.springframework.web.servlet.View
```
