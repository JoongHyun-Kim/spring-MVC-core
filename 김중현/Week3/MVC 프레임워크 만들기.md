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
