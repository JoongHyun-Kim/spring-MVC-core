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

## V1 - 프론트 컨트롤러 도입 
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
<br>
<br>
<br>
<br>


```md
ControllerV1 인터페이스를 구현한 회원 등록, 저장, 조회(목록) 컨트롤러를 만들자!
```

#### MemberFormControllerV1
> 회원 등록 컨트롤러
```java
package hello.servlet.web.frontcontroller.v1.controller;

import hello.servlet.web.frontcontroller.v1.ControllerV1;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberFormControllerV1 implements ControllerV1 {

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String viewPath = "/WEB-INF/views/new-form.jsp"; //View 그대로 사용
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath); //Controller에서 View로 이동할 때 사용
        dispatcher.forward(request, response); //Controller가 View 호출
    }
}
```
<br>

#### MemberSaveControllerV1
> 회원 저장 컨트롤러
```java
package hello.servlet.web.frontcontroller.v1.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.v1.ControllerV1;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberSaveControllerV1 implements ControllerV1 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        System.out.println("member = " + member);
        memberRepository.save(member);

        //Model에 데이터를 보관
        request.setAttribute("member", member); //request 내부 저장소에 member 저장

        String viewPath = "/WEB-INF/views/save-result.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```
<br>

#### MemberListControllerV1
> 회원 목록 컨트롤러
```java
package hello.servlet.web.frontcontroller.v1.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.v1.ControllerV1;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MemberListControllerV1 implements ControllerV1 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("MvcMemberListServlet.service");
        List<Member> members = memberRepository.findAll();

        //Model에 담기
        request.setAttribute("members", members); //key, value

        String viewPath = "/WEB-INF/views/members.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```
- 내부 로직은 기존에 구현했던 서블릿과 거의 동일하다.
<br>
<br>
<br>
<br>

#### FrontControllerServletV1
> 프론트 컨트롤러
```java
package hello.servlet.web.frontcontroller.v1;

import hello.servlet.web.frontcontroller.v1.controller.MemberFormControllerV1;
import hello.servlet.web.frontcontroller.v1.controller.MemberListControllerV1;
import hello.servlet.web.frontcontroller.v1.controller.MemberSaveControllerV1;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "FrontControllerServletV1", urlPatterns = "/front-controller/v1/*")
public class FrontControllerServletV1 extends HttpServlet {

    private Map<String, ControllerV1> controllerMap = new HashMap<>(); //어떤 url이 호출되면 ControllerV1을 꺼내 호출해라

    public FrontControllerServletV1() { //이 생성자가 호출되면 각 url에 맞는 controller가 매핑된다.
        controllerMap.put("/front-controller/v1/members/new-form", new MemberFormControllerV1());
        controllerMap.put("/front-controller/v1/members/save", new MemberSaveControllerV1());
        controllerMap.put("/front-controller/v1/members", new MemberListControllerV1());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("FrontControllerServletV1.service");

        String requestURI = request.getRequestURI(); //request에서 url을 가져와 requestURI에 저장

        ControllerV1 controller = controllerMap.get(requestURI); //controllerV1 인터페이스를 이용해 각 URI에 매핑되는 controller를 찾을 수 있다.
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        controller.process(request, response); //다형성으로 인헤 오버라이드된 메소드가 실행된다.
        //앞에서 매핑된 컨트롤러의 process 메소드가 실행되므로, 해당 컨트롤러의 로직이 그대로 실행되는 것이다.
    }
}
```
<br>
<br>
<br>
<br>

## V2 - View 분리
> 모든 컨트롤러에서 뷰로 이동하는 부분에 중복이 있고 깔끔하지 않으므로, 뷰를 처리하는 객체를 별도로 만들자!
<br>

### V2 구조
<img width="600" alt="스크린샷 2022-08-11 오후 12 30 12" src="https://user-images.githubusercontent.com/80838501/184060562-6f2529ac-eacf-4a9e-9251-45ed3ae023ef.png">

- V1에서는 Controller에서 JSP로 직접 보내줬지만, V2에서는 View 역할을 하는 MyView라는 객체를 만들어 FrontController한테 반환을 한 뒤, <br>
  Frontcontroller가 MyView의 render()를 호출하면 MyView가 JSP를 forward하도록 구성한다.
- Controller가 더 이상 JSP forward에 대해 고민하지 않아도 되고, MyView만 생성해서 호출하면 된다.
<br>
<br>

#### MyView
> View 역할을 하는 MyView 객체
```java
package hello.servlet.web.frontcontroller;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyView {
    private String viewPath;

    public MyView(String viewPath) {
        this.viewPath = viewPath;
    }

    public void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```
<br>
<br>

#### ControllerV2
> V2 버전 Controller 인터페이스
```java
package hello.servlet.web.frontcontroller.v2;

import hello.servlet.web.frontcontroller.MyView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ControllerV2 {

    MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}

```
<br>
<br>

#### MemberFormControllerV2
> 회원 등록
```java
package hello.servlet.web.frontcontroller.v2.controller;

import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v2.ControllerV2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberFormControllerV2 implements ControllerV2 {

    @Override
    public MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return new MyView("/WEB-INF/views/new-form.jsp");
    }
}
```
- 이제 각 컨트롤러는 dispatcher.forward()를 직접 생성해 호출하지 않아도 된다. 단순히 MyView 객체를 생성해 뷰 이름만 넣고 반환하면 된다.
<br>
<br>

#### MemberSaveControllerV2
> 회원 저장
```java
package hello.servlet.web.frontcontroller.v2.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v2.ControllerV2;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MemberSaveControllerV2 implements ControllerV2 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        System.out.println("member = " + member);
        memberRepository.save(member);

        //Model에 데이터를 보관
        request.setAttribute("member", member); //request 내부 저장소에 member 저장

        return new MyView("/WEB-INF/views/save-result.jsp"); //view 객체 
    }
}
```
<br>
<br>

#### MemberListControllerV2
> 회원 목록 
```java
package hello.servlet.web.frontcontroller.v2.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v2.ControllerV2;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MemberListControllerV2 implements ControllerV2 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("MvcMemberListServlet.service");
        List<Member> members = memberRepository.findAll();

        //Model에 담기
        request.setAttribute("members", members); //key, value

        return new MyView("/WEB-INF/views/members.jsp"); //view 객체 
    }
}
```
<br>
<br>

#### FrontControllerServletV2
> 프론트 컨트롤러
```java
package hello.servlet.web.frontcontroller.v2;

import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v2.controller.MemberFormControllerV2;
import hello.servlet.web.frontcontroller.v2.controller.MemberListControllerV2;
import hello.servlet.web.frontcontroller.v2.controller.MemberSaveControllerV2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "FrontControllerServletV2", urlPatterns = "/front-controller/v2/*")
public class FrontControllerServletV2 extends HttpServlet {

    private Map<String, ControllerV2> controllerMap = new HashMap<>(); //어떤 url이 호출되면 ControllerV2를 꺼내 호출

    public FrontControllerServletV2() { //이 생성자가 호출되면 각 url에 맞는 controller가 매핑된다.
        controllerMap.put("/front-controller/v2/members/new-form", new MemberFormControllerV2());
        controllerMap.put("/front-controller/v2/members/save", new MemberSaveControllerV2());
        controllerMap.put("/front-controller/v2/members", new MemberListControllerV2());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String requestURI = request.getRequestURI(); //request에서 URI를 가져와 requestURI에 저장

        ControllerV2 controller = controllerMap.get(requestURI); //controllerV2 인터페이스를 이용해 각 URI에 매핑되는 controller를 찾을 수 있다.
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        MyView view = controller.process(request, response); //ControllerV2의 리턴 타입이 MyView이므로 MyView로 받는다.
        view.render(request, response); //render() 호출 시 forward 로직 수행해 JSP 실행
    }
}
```
```md
URL 패턴이 /front-controller/v2/* 와 같은 HTTP 요청이 들어오면 FrontController 서블릿이 실행된다.
FrontController는 해당 URL과 맞는 controller를 매핑하고, 조회한다.
매핑된 컨트롤러의 process() 메소드를 실행해 반환된 MyView 객체를 받아온다.
반환된 MyView 객체의 render() 메소드를 실행해 forward 로직을 수행하고 JSP를 실행한다.
```
→ 이제 각 컨트롤러는 MyView 객체를 생성해 반환하기만 하면 되고, 반환된 객체를 이용해 forward 로직을 수행해주는 것은 `FrontController`다.
<br>
<br>
<br>
<br>

## V3 - Model 추가
### 목표
```
1) 서블릿 종속성 제거
컨트롤러 입장에서 현재 HttpServletRequest와 HttpServletResponse가 필요 없다.
요청 파라미터의 정보는 자바의 Map을 활용해 넘기도록 하면, 지금 구조에서는 컨트롤러가 서블릿 기술을 몰라도 제대로 동작할 수 있다.
그리고, request 객체를 Model로 사용하는 대신, 별도의 Model 객체를 만들어 반환하면 된다. (여태까지는 request를 model로 보고 setAttribute를 이용해 안에 데이터를 담아 렌더링)
이렇게 변경해 컨트롤러가 서블릿 기술을 전혀 사용하지 않도록 해보자!

2) 뷰 이름 중복 제거
현재 컨트롤러에서 지정하는 뷰 이름에 중복이 있다.
컨트롤러는 뷰의 논리 이름을 반환하고, 물리 위치의 이름은 프론트 컨트롤러에서 처리하도록 단순화하자!
이렇게 변경하면 후에 뷰의 폴더 위치가 이동해도 여러 뷰를 직접 수정할 필요 없이 프론트 컨트롤러만 수정하면 된다.
```
<br>
<br>

### V3 구조
<img width="600" alt="스크린샷 2022-08-14 오전 2 35 47" src="https://user-images.githubusercontent.com/80838501/184504821-45f8a319-2eac-46a4-a7af-0eb65b423d2c.png">

- Controller에서 FrontController로 View가 아닌 `ModelView`(Model + View)를 반환한다.
- `viewResolver`에서 뷰의 논리 이름을 물리 위치의 이름으로 변경한다.
- Model을 직접 만들고 View의 이름까지 전달하는 객체인 `ModelView`를 만들자!
<br>
<br>

#### ModelView
```java
package hello.servlet.web.frontcontroller;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    private String viewName;
    private Map<String, Object> model = new HashMap<>();

    public ModelView(String viewName) {

        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
}
```
- 뷰의 이름인 viewName과, 뷰를 렌더링할 때 필요한 model 객체를 가지고 있다. 
- model은 map으로 되어있기 때문에 컨트롤러에서 뷰에 필요한 데이터를 key, value로 넣어주면 된다.
<br>
<br>

#### ControllerV3
> 인터페이스
```java
package hello.servlet.web.frontcontroller.v3;

import hello.servlet.web.frontcontroller.ModelView;

import java.util.Map;

public interface ControllerV3 {

    ModelView process(Map<String, String> paramMap);
}
```
- HttpServletRequest가 제공하던 request, response 파라미터는 프론트 컨트롤러가 paramMap에 담아 호출해주고, 그 응답 결과로 뷰 이름과<br>
  뷰에 전달할 데이터를 포함하는 ModelView 객체를 반환한다.
<br>
<br>

#### MemberFormControllerV3
> 회원 등록
```java
package hello.servlet.web.frontcontroller.v3.controller;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;

import java.util.Map;

public class MemberFormControllerV3 implements ControllerV3 {

    @Override
    public ModelView process(Map<String, String> paramMap) {

        return new ModelView("new-form"); //논리적 이름만 넣기
    }
}
```
- view의 논리 이름을 `new-form`으로 지정해 반환한다.
<br>
<br>

#### MemberSaveControllerV3
> 회원 저장
```java
package hello.servlet.web.frontcontroller.v3.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;

import java.util.Map;

public class MemberSaveControllerV3 implements ControllerV3 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public ModelView process(Map<String, String> paramMap) {
        String username = paramMap.get("username");
        int age = Integer.parseInt(paramMap.get("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        ModelView mv = new ModelView("save-result");
        mv.getModel().put("member", member);

        return mv;
    }
}
```
- map에서 username과 age를 조회해 Member 객체를 생성한다.
- view의 논리 이름을 `save-result`로 지정해주고, 모델에 member 객체를 담아 반환한다.
<br>
<br>

#### MemberListControllerV3
> 회원 목록
```java
package hello.servlet.web.frontcontroller.v3.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;

import java.util.List;
import java.util.Map;

public class MemberListControllerV3 implements ControllerV3 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public ModelView process(Map<String, String> paramMap) {
        List<Member> members = memberRepository.findAll();
        ModelView mv = new ModelView("members");
        mv.getModel().put("members", members);

        return mv;
    }
}
```
- 모든 member를 조회해 List에 저장한 뒤 모델에 결과를 담아 반환한다.
<br>
<br>
<br>

#### FrontControllerServletV3
```java
package hello.servlet.web.frontcontroller.v3;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "FrontControllerServletV3", urlPatterns = "/front-controller/v3/*")
public class FrontControllerServletV3 extends HttpServlet {

    private Map<String, ControllerV3> controllerMap = new HashMap<>(); //어떤 url이 호출되면 ControllerV2를 꺼내 호출

    public FrontControllerServletV3() { //이 생성자가 호출되면 각 url에 맞는 controller가 매핑된다.
        controllerMap.put("/front-controller/v3/members/new-form", new MemberFormControllerV3());
        controllerMap.put("/front-controller/v3/members/save", new MemberSaveControllerV3());
        controllerMap.put("/front-controller/v3/members", new MemberListControllerV3());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String requestURI = request.getRequestURI(); //request에서 URI를 가져와 requestURI에 저장

        ControllerV3 controller = controllerMap.get(requestURI); //controllerV2 인터페이스를 이용해 각 URI에 매핑되는 controller를 찾을 수 있다.
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Map<String, String> paramMap = createParamMap(request);
        ModelView mv = controller.process(paramMap);

        String viewName = mv.getViewName(); //논리 이름
        MyView view = viewResolver(viewName);

        view.render(mv.getModel(), request, response); //뷰 객체를 통해 HTML 화면 렌더링
    }

    //논리 이름을 가지고 실제 물리 이름 생성
    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }

    //HttpServletRequest에서 파라미터 정보를 꺼내 Map으로 변환
    private Map<String, String> createParamMap(HttpServletRequest request) { 
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator() //HttpServletRequest서 모든 parameter name을 다 가져 하나씩 돌리면서
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName))); //name을 key로 하면서 request.getParameter로 모든 value 다 꺼내오기

        return paramMap;
    }
}
```
<br>
<br>
<br>

#### MyView
```java
package hello.servlet.web.frontcontroller;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class MyView {
    private String viewPath;

    public MyView(String viewPath) {
        this.viewPath = viewPath;
    }

    public void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }

    
    public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        modelToRequestAttribute(model, request);
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }

    private void modelToRequestAttribute(Map<String, Object> model, HttpServletRequest request) {
        model.forEach((key, value) -> request.setAttribute(key, value));
    }
}
```
- JSP는 `request.getAttribute()`로 데이터를 조회하기 때문에 모델의 데이터를 꺼내 `request.setAttribute()`로 담아두는 단계가 필요하다.
<br>
<br>
<br>
<br>

## V4 - 단순하고 실용적인 컨트롤러
### V4 구조
<img width="600" alt="스크린샷 2022-08-15 오후 12 18 24" src="https://user-images.githubusercontent.com/80838501/184571515-3daafa1c-4ba2-4fcc-ac9f-053cc4ef164c.png">

- Controller가 `ModelView`를 반환하지 않고 `ViewName`만 반환한다.
<br>
<br>

#### ControllerV4
> 인터페이스
```java
package hello.servlet.web.frontcontroller.v4;

import java.util.Map;

public interface ControllerV4 {

    /**
     *
     * @param paramMap
     * @param model
     * @return viewName
     */
    String process(Map<String, String> paramMap, Map<String, Object> model);
}
```
- model 객체를 파라미터로 전달하면 되기 때문에 ModelView가 없고, 결과로 뷰의 이름만 반환해주면 된다.
<br>
<br>
<br>

#### MemberFormControllerV4
```java
package hello.servlet.web.frontcontroller.v4.controller;

import hello.servlet.web.frontcontroller.v4.ControllerV4;

import java.util.Map;

public class MemberFormControllerV4 implements ControllerV4 {

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        return "new-form"; //ModelView 반환할 필요 없이 논리 이름만 반환
    }
}
```
- 뷰의 논리 이름만 반환한다.
<br>
<br>
<br>

#### MemberSaveControllerV4
```java
package hello.servlet.web.frontcontroller.v4.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.v4.ControllerV4;

import java.util.Map;

public class MemberSaveControllerV4 implements ControllerV4 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        String username = paramMap.get("username");
        int age = Integer.parseInt(paramMap.get("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        model.put("member", member);
        return "save-result";
    }
}
```
- model이 파라미터로 전달되기 때문에 직접 모델을 생성하지 않아도 된다.
- 논리 이름을 반환한다.
<br>
<br>

#### MemberListControllerV4
```java
package hello.servlet.web.frontcontroller.v4.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.v4.ControllerV4;

import java.util.List;
import java.util.Map;

public class MemberListControllerV4 implements ControllerV4 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        List<Member> members = memberRepository.findAll();

        model.put("members", members);
        return "members";
    }
}
```
- 마찬가지로, 논리 이름을 반환한다.
<br>
<br>

#### FrontControllerServletV4
```java
package hello.servlet.web.frontcontroller.v4;

import hello.servlet.web.frontcontroller.MyView;

import hello.servlet.web.frontcontroller.v4.controller.MemberFormControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberListControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberSaveControllerV4;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "FrontControllerServletV4", urlPatterns = "/front-controller/v4/*")
public class FrontControllerServletV4 extends HttpServlet {

    private Map<String, ControllerV4> controllerMap = new HashMap<>(); //어떤 url이 호출되면 ControllerV2를 꺼내 호출

    public FrontControllerServletV4() { //이 생성자가 호출되면 각 url에 맞는 controller가 매핑된다.
        controllerMap.put("/front-controller/v4/members/new-form", new MemberFormControllerV4());
        controllerMap.put("/front-controller/v4/members/save", new MemberSaveControllerV4());
        controllerMap.put("/front-controller/v4/members", new MemberListControllerV4());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String requestURI = request.getRequestURI(); //request에서 URI를 가져와 requestURI에 저장

        ControllerV4 controller = controllerMap.get(requestURI); //controllerV2 인터페이스를 이용해 각 URI에 매핑되는 controller를 찾을 수 있다.
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Map<String, String> paramMap = createParamMap(request);
        Map<String, Object> model = new HashMap<>();

        String viewName = controller.process(paramMap, model);

        MyView view = viewResolver(viewName);

        view.render(model, request, response);
    }

    //논리 이름을 가지고 실제 물리 이름 생성
    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator() //HttpServletRequest서 모든 parameter name을 다 가져 하나씩 돌리면서
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName))); //name을 key로 하면서 request.getParameter로 모든 value 다 꺼내오기

        return paramMap;
    }
}
```
- 모델 객체를 프론트 컨트롤러가 직접 생성해 넘겨주는 것으로 수정한다. 각 컨트롤러에서 모델 객체에 값을 담으면 된다.
- V3와 동일하게 컨트롤러가 반환한 논리 이름을 이용해 실제 물리 이름을 찾을 수 있다.
<br>
<br>
<br>
<br>

## V5 - 유연한 컨트롤러
> 만약 한 프로젝트에서 여러 Controller 버전으로 개발하고 싶은 경우에는 어떻게 해야 할까?
<br>

### 어댑터 패턴
여태까지는 프론트 컨트롤러가 한 가지 방식의 컨트롤러 인터페이스만 사용할 수 있었다. 이제 어댑터 패턴을 사용해 프로트 컨트롤러가 다양한 방식의 컨트롤러를 처리할 수 있도록 변경해보자!
<br>
<br>

### V5 구조
<img width="841" alt="스크린샷 2022-08-15 오후 2 00 37" src="https://user-images.githubusercontent.com/80838501/184579198-879c7eb3-f275-4116-af1d-d5a92f2d6e1b.png">

```
핸들러 어댑터: 중간에서 어댑터 역할을 하며 다양한 종류의 컨트롤러를 호출할 수 있다.
핸들러: 컨트롤러를 더 넓은 범위를 의미하는 핸들러로 이름을 변경했다. 어댑터가 있기 때문에 꼭 컨트롤러뿐만 아니라 해당하는 종류의 어댑터만 있으면 다 처리할 수 있기 때문이다.
```
<br>
<br>

#### MyHandlerAdapter
> 어댑터 인터페이스
```java
package hello.servlet.web.frontcontroller.v5;

import hello.servlet.web.frontcontroller.ModelView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;

public interface MyHandlerAdapter {

    boolean supports(Object handler); //어댑터가 해당 컨트롤러(핸들러)를 처리할 수 있는지 판단

    ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws SerialException, IOException;
}
```
- 어댑터는 실제 컨트롤러(핸들러)를 호출하고, 결과로 ModelView를 반환해야 한다.
- 전에는 프론트 컨트롤러가 각 컨트롤러를 호출했지만, 이제는 어댑터를 통해 실제 컨트롤러가 호출된다.
<br>
<br>
<br>

ControllerV3를 지원하는 어댑터를 구현해보자!
#### ControllerV3HandlerAdapter
```java
package hello.servlet.web.frontcontroller.v5.adapter;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;
import hello.servlet.web.frontcontroller.v5.MyHandlerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ControllerV3HandlerAdapter implements MyHandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof ControllerV3); //ControllerV3 인터페이스를 구현한 무언가가 넘어오게 되면 True 반환. 다른 것을 구현한 것들은 전부 False 반환.
    }

    @Override
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        ControllerV3 controller = (ControllerV3) handler; //이미 ControllerV3만 걸러진 상태이기 때문에 casting해서 사용해도 된다.

        Map<String, String> paramMap = createParamMap(request);
        ModelView mv = controller.process(paramMap);

        return mv;
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator() //HttpServletRequest서 모든 parameter name을 다 가져 하나씩 돌리면서
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName))); //name을 key로 하면서 request.getParameter로 모든 value 다 꺼내오기

        return paramMap;
    }
}
```
<br>
<br>
<br>

#### FrontControllerServletV5
```java
package hello.servlet.web.frontcontroller.v5;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;
import hello.servlet.web.frontcontroller.v5.adapter.ControllerV3HandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "frontControllerServletV5" ,urlPatterns = "/front-controller/v5/*")
public class FrontControllerServletV5 extends HttpServlet {

    private final Map<String, Object> handlerMappingMap = new HashMap<>(); //아무 버전이나 다 들어갈 수 있도록
    private final List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();//List가 여러 개 담겨있고 그 중에 찾아 써야되기 때문에

    public FrontControllerServletV5() {
        initHandlerMappingMap();
        initHandlerAdapters();
    }

    private void initHandlerMappingMap() { //핸들러 매핑 초기화
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());
    }

    private void initHandlerAdapters() { //어댑터 초기화
        handlerAdapters.add(new ControllerV3HandlerAdapter());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //handler 찾기
        Object handler = getHandler(request);
        if (handler == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //HandlerAdapter 찾기
        MyHandlerAdapter adapter = getHandlerAdapter(handler);

        //실제 어댑터 호출
        //어댑터는 핸들러를 호출하고 그 결과를 어댑터에 맞추어 반환
        ModelView mv = null;
        try {
            mv = adapter.handle(request, response, handler);
        } catch (SerialException e) {
            e.printStackTrace();
        }

        String viewName = mv.getViewName(); //논리 이름
        MyView view = viewResolver(viewName);

        view.render(mv.getModel(), request, response);
    }

    private Object getHandler(HttpServletRequest request) { //handlerMappingMap에서 URL에 매핑된 핸들러 객체를 찾아 반환
        String requestURI = request.getRequestURI();
        return handlerMappingMap.get(requestURI); //요청이 오면 handler를 찾는다.
    }

    private MyHandlerAdapter getHandlerAdapter(Object handler) { //어댑터 찾기
        for (MyHandlerAdapter adapter : handlerAdapters) {
            if (adapter.supports(handler)) {
                return adapter;
            }
        }
        throw new IllegalArgumentException("handler adapter를 찾을 수 없습니다. handler = " + handler);
    }

    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }
}
```
