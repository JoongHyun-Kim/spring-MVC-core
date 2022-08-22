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
<br>
<br>
<br>
<br>

## 핸들러 매핑과 핸들러 어댑터
- 지금은 사용하지 않지만 과거에 주로 사용했던, 스프링이 제공하는 간단한 컨트롤러를 예시로 핸들러 매핑과 어댑터에 대해 알아보자!
<br>

#### Controller 인터페이스
> 과거 버전 스프링 컨트롤러
```java
public interface Controller {
      ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
```
<br>

#### OldController
> 구현체
```java
package hello.servlet.web.springmvc.old;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component("/springmvc/old-controller")
public class OldController implements Controller {
     @Override
     public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
          System.out.println("OldController.handleRequest");
          return null;
     }
}
```
- `@Component`: 이 컨트롤러는 /springmvc/old-controller라는 이름으로 스프링 빈이 등록되었고, 빈 이름으로 URL을 매핑할 것이다.
<br>
<br>

스프링 MVC 구조를 다시 살펴 보자. 이 컨트롤러가 호출되려면 무엇이 필요할까?
1. HandlerMapping
   - HandlerMapping에서 이 컨트롤러를 찾을 수 있어야 한다.
   - Ex) 스프링 빈의 이름으로 핸들러를 찾을 수 있는 핸들러 매핑이 필요하다.
2. HandlerAdapter
   - 핸들러 매핑을 통해 찾은 핸들러를 실행할 수 있는 핸들러 어댑터가 필요하다.
   - Ex) Controller 인터페이스를 실행할 수 있는 핸들러 어댑터를 찾고 실행해야 한다.
<br>
<br>

### 스프링 부트가 자동 등록하는 핸들러 매핑과 핸들러 어댑터
#### HandlerMapping
```java
0 = RequestMappingHandlerMapping → 애노테이션 기반 컨트롤러인 @RequestMapping에서 사용
1 = BeanNameUrlHandlerMapping → 스프링 빈 이름으로 핸들러를 찾는다.
```
<br>
<br>

#### HandlerAdapter
```java
0 = RequestMappingHandlerAdapter → 애노테이션 기반 컨트롤러인 @RequestMapping에서 사용
1 = HttpRequestHandlerAdapter → HttpRequestHandler 처리
2 = SimpleControllerHandlerAdapter → Controller 인터페이스 처리 (애노테이션이 아닌, 과거에 사용한 형태)
```
- 핸들러 매핑과 핸들러 어댑터 모두 위 순서대로 찾고, 만약 없으면 다음 순서로 넘어간다.
<br>
<br>

#### OldController 예제
1. 핸들러 매핑으로 핸들러 조회
    - HandlerMapping 을 순서대로 실행해서 핸들러를 찾는다.
    - 이 경우, 빈 이름으로 핸들러를 찾아야 하기 때문에 이름 그대로 빈 이름으로 핸들러를 찾아주는 BeanNameUrlHandlerMapping가 실행에 성공하고 핸들러인 OldController를 반환한다.
2. 핸들러 어댑터 조회
    - HandlerAdapter의 supports()를 순서대로 호출한다. SimpleControllerHandlerAdapter가 Controller 인터페이스를 지원하므로 대상이 된다.
3. 핸들러 어댑터 실행
    - 디스패처 서블릿이 조회한 SimpleControllerHandlerAdapter를 실행하면서 핸들러 정보도 함께 넘겨준다.
    - SimpleControllerHandlerAdapter는 핸들러인 OldController 를 내부에서 실행하고, 그 결과를 반환한다.
<br>
<br>

### HttpRequestHandler 예제
#### HttpRequestHandler 인터페이스
```java
public interface HttpRequestHandler {
   void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
```
<br>
<br>

#### MyHttpRequestHandler
```java
package hello.servlet.web.springmvc.old;

import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;
     
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("/springmvc/request-handler")
public class MyHttpRequestHandler implements HttpRequestHandler {
      
      @Override
      public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          System.out.println("MyHttpRequestHandler.handleRequest");
      }
}
```
1. 핸들러 매핑으로 핸들러 조회
    - HandlerMapping 을 순서대로 실행해서 핸들러를 찾는다.
    - 이 경우 빈 이름으로 핸들러를 찾아야 하기 때문에 이름 그대로 빈 이름으로 핸들러를 찾아주는 BeanNameUrlHandlerMapping가 실행에 성공하고 핸들러인 MyHttpRequestHandler를 반환한다.
2. 핸들러 어댑터 조회
    - HandlerAdapter의 supports()를 순서대로 호출한다.
    - HttpRequestHandlerAdapter가 HttpRequestHandler 인터페이스를 지원하므로 대상이 된다.
3. 핸들러 어댑터 실행
    - 디스패처 서블릿이 조회한 HttpRequestHandlerAdapter를 실행하면서 핸들러 정보도 함께 넘겨준다.
    - HttpRequestHandlerAdapter는 핸들러인 MyHttpRequestHandler를 내부에서 실행하고, 그 결과를 반환한다.
<br>
<br>
<br>
<br>

## 뷰 리졸버
### 뷰 리졸버 - InternalResourceViewResolver
스프링 부트는 `InternalResourceViewResolver`라는 뷰 리졸버를 자동으로 등록하는데, application.properties에 등록한 spring.mvc.view.prefix, spring.mvc.view.suffix 설정 정보를 사용해서 등록한다.
<br>
<br>

#### 스프링 부트가 자동 등록하는 뷰 리졸버
> 중요한 부분 일부만
```java
1 = BeanNameViewResolver → 빈 이름으로 뷰를 찾아 반환
2 = InternalResourceViewResolver → JSP를 처리할 수 있는 뷰를 반환
```

#### 동작 방식
1. 핸들러 어댑터 호출
   - 핸들러 어댑터를 통해 new-form이라는 논리 뷰 이름을 얻는다.
2. ViewResolver 호출
   - new-form이라는 뷰 이름으로 viewResolver를 순서대로 호출한다.
   - BeanNameViewResolver는 new-form이라는 이름의 스프링 빈으로 등록된 뷰를 찾아야 하는데 없으므로, InternalResourceViewResolver가<br> 호출된다.
3. InternalResourceViewResolver
   - 이 뷰 리졸버는 InternalResourceView를 반환한다. 
4. 뷰 - InternalResourceView
   - InternalResourceView는 JSP처럼 포워드 forward()를 호출해 처리할 수 있는 경우에 사용한다.
5. view.render()
   - view.render()가 호출되고 InternalResourceView는 forward()를 사용해서 JSP를 실행한다.
<br>
<br>
<br>
<br>

## 스프링 MVC 시작하기
### @RequestMapping
> 스프링은 애노테이션을 활용한 매우 유연하고 실용적인 컨트롤러를 만들었는데, 바로 @RequestMapping 애노테이션을 사용하는 컨트롤러이다.
- 가장 우선순위가 높은 핸들러 매핑과 핸들러 어댑터는 `RequestMappingHandlerMapping`, `RequestMappingHandlerAdapter`이다.
   - 지금 스프링에서 주로 사용하는 애노테이션 기반의 컨트롤러를 지원하는 핸들러 매핑과 어댑터이며, 실무에서는 99.9% 이 방식의 컨트롤러를 사용한다.
<br>
<br>
<br>
<br>

## 스프링 MVC - 컨트롤러 통합
> @RequestMapping을 보면 클래스 단위가 아닌 메소드 단위에 적용되어 있는데, 컨트롤러 클래스를 유연하게 하나로 통합할 수 있다.
### 컨트롤러 통합
### 조합
<br>
<br>
<br>
<br>

## 스프링 MVC - 실용적인 방식
> 스프링 MVC는 개발자가 편리하게 개발할 수 있도록 수 많은 편의 기능을 제공

#### 1. Model 파라미터
- save(), members()에서 Model을 파라미터로 받는 것을 볼 수 있는데, 스프링 MVC도 이러한 편의 기능을 제공한다.
<br>

#### 2. ViewName 직접 반환
- 뷰의 논리 이름을 반환할 수 있다.
<br>

#### 3. @RequestParam 사용
- 스프링은 HTTP 요청 파라미터를 `@RequestParam`으로 받을 수 있다. 
    - @RequestParam("username")은 request.getParameter("username")와 거의 같은 코드라 생각하면 된다.
    - GET 쿼리 파라미터, POST Form 방식을 모두 지원한다.
<br>

#### 4. @RequestMapping → @GetMapping, @PostMapping
- `@RequestMapping`은 URL만 매칭하는 것이 아니라, HTTP Method도 함께 구분할 수 있다. <br>
  예를 들어, URL이 /new-form 이고, HTTP Method가 GET인 경우를 모두 만족하는 매핑을 하려면 다음과 같이 처리하면 된다.
  ```java
   @RequestMapping(value = "/new-form", method = RequestMethod.GET)
  ```
- `@GetMapping`, `@PostMapping`으로 더 편리하게 사용할 수 있고, Get, Post, Put, Delete, Patch 모두 애노테이션이 준비되어 있다.
