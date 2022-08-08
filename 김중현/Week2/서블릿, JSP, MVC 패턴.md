# Section 3. 서블릿, JSP, MVC 패턴
> 코어와 모듈을 만든 다음, 서블릿, JSP, MVC 패턴을 차례차례 사용해 애플리케이션 발전시켜 나가기
## 회원 관리 웹 애플리케이션 요구사항
```
회원 정보
  이름: username
  나이: age
기능 요구사항
  회원 저장
  회원 목록 조회
```
<br>
<br>

### 회원 도메인
#### Member
> 회원 도메인
```java
package hello.servlet.domain.member;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Member {
    private Long id;
    private String username;
    private int age;

    public Member() { //기본 생성자
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
```
<br>
<br>

#### MemberRepository
```java
package hello.servlet.domain.member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 동시성 문제가 고려되어 있지 않기 때문에 실무에서는 ConcurrentHashMap, AtomicLong 사용 고려한다.
 */
public class MemberRepository {
    private static Map<Long, Member> store = new HashMap<>(); //static 사용
    private static long sequence = 0L; //static 사용해 하나만 사용되도록

    private static final MemberRepository instance = new MemberRepository(); //싱글톤
    public static MemberRepository getInstance() {
        return instance;
    }

    private MemberRepository() { //싱글톤일 때는 private으로 막아 아무나 생성하지 못하도록 해주기
    }

    //회원 저장
    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    //Id로 회원 조회
    public Member findById(Long id) {
        return store.get(id);
    }

    //회원 전체 조회
    public List<Member> findAll() {
        return new ArrayList<>(store.values()); //store에 있는 모든 값들을 꺼내 새로운 ArrayList에 담아 return
        //밖에서 ArrayList에 값을 넣거나 조작해도 store에 있는 값을 건들고 싶지 않기 때문
    }

    //store clear (테스트 사용)
    public void clearStore() {
        store.clear();
    }
}
```
<br>
<br>

### 테스트 
#### MemberRepositoryTest
```java
package hello.servlet.domain.member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class MemberRepositoryTest {
    MemberRepository memberRepository = MemberRepository.getInstance(); //싱글톤이기 때문에 new X

    @AfterEach
    void afterEach() { //테스트 끝날 때마다 초기화
        memberRepository.clearStore();
    }

    /* 회원 저장 텍스트 */
    @Test
    void save() {
        //given
        Member member = new Member("hello", 20);

        //when
        Member savedMember = memberRepository.save(member);

        //then
        Member findMember = memberRepository.findById(savedMember.getId());
        assertThat(findMember).isEqualTo(savedMember);
    }

    /* 회원 조회 테스트 */
    @Test
    void findAll() {
        //given
        Member member1 = new Member("member1", 20);
        Member member2 = new Member("member2", 30);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> result = memberRepository.findAll();

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(member1, member2);
    }
}
```
<br>
<br>
<br>
<br>

## 서블릿으로 회원 관리 웹 애플리케이션 만들기
### 회원 등록 HTML Form
> 응답으로 HTML 뿌리기
#### MemberFormServlet
```java
package hello.servlet.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "memberFormServlet", urlPatterns = "/servlet/members/new-form")
public class MemberFormServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        PrintWriter w = response.getWriter();
        w.write("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<form action=\"/servlet/members/save\" method=\"post\">\n" +
                "    username: <input type=\"text\" name=\"username\" />\n" +
                "    age:      <input type=\"text\" name=\"age\" />\n" +
                " <button type=\"submit\">전송</button>\n" + "</form>\n" +
                "</body>\n" +
                "</html>\n");
    }
}
```
- 서블릿 사용 시, 자바 코드를 하나하나 다 쳐야되기 때문에 굉장히 불편하다.
- 회원 정보 입력 후 버튼 클릭 시 /servlet/members/save 경로로 POST 요청
<br>
<br>

### 회원 
#### MemberSaveServlet
```java
package hello.servlet.web.servlet;
import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "memberSaveServlet", urlPatterns = "/servlet/members/save")
public class MemberSaveServlet extends HttpServlet {
    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("MemberSaveServlet.service");
        //username과 age 꺼내기
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age")); //숫자 타입으로 변환

        Member member = new Member(username, age); //member 객체 생성
        System.out.println("member = " + member);

        memberRepository.save(member); //회원 저장

        //저장이 잘되었는지 확인하기 위해 응답을 HTML 코드로 내려주기
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        //id, username, age 출력해보기
        PrintWriter w = response.getWriter();
        w.write("<html>\n" +
                "<head>\n" +
                " <meta charset=\"UTF-8\">\n" + "</head>\n" +
                "<body>\n" +
                "성공\n" +
                "<ul>\n" +
                "    <li>id=" + member.getId() + "</li>\n" +
                "    <li>username=" + member.getUsername() + "</li>\n" +
                " <li>age=" + member.getAge() + "</li>\n" + "</ul>\n" +
                "<a href=\"/index.html\">메인</a>\n" + "</body>\n" +
                "</html>");
    }
}
```
```
- 파라미터를 조회해 Member 객체를 생성
- Member 객체를 MemberRepository를 통해 저장
- Member 객체를 사용해 결과 화면 HTML을 동적으로 만들어 응답
```
<br>

#### 회원 정보 입력 후 버튼 클릭 시(save) 결과 화면  
<img width="152" alt="스크린샷 2022-08-08 오전 11 53 07" src="https://user-images.githubusercontent.com/80838501/183329139-f8b14288-3d3b-4047-a55c-f2ad1131029f.png">

<br>
<br>

### 회원 목록 조회
#### MemberListServlet
```java
package hello.servlet.web.servlet;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "memberListServlet", urlPatterns = "/servlet/members")
public class MemberListServlet extends HttpServlet {
    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        List<Member> members = memberRepository.findAll(); //전체 회원 조회

        PrintWriter w = response.getWriter();
        w.write("<html>");
        w.write("<head>");
        w.write("    <meta charset=\"UTF-8\">");
        w.write("    <title>Title</title>");
        w.write("</head>");
        w.write("<body>");
        w.write("<a href=\"/index.html\">메인</a>");
        w.write("<table>");
        w.write("    <thead>");
        w.write("    <th>id</th>");
        w.write("    <th>username</th>");
        w.write("    <th>age</th>");
        w.write("    </thead>");
        w.write("    <tbody>");
        //정적인 형태
        /*
        w.write("    <tr>");
        w.write("       <td>1</td>");
        w.write("       <td>userA</td>");
        w.write("       <td>10</td>");
        w.write("   </tr>");
         */
        for (Member member : members) { //member 객체를 읽어 동적으로 돌리기
            w.write("    <tr>");
            w.write("        <td>" + member.getId() + "</td>");
            w.write("        <td>" + member.getUsername() + "</td>");
            w.write("        <td>" + member.getAge() + "</td>");
            w.write("    </tr>");
        }
        w.write("    </tbody>");
        w.write("</table>");
        w.write("</body>");
        w.write("</html>");
    }
}
```
```
- memberRepository.findAll()으로 모든 회원을 조회
- 회원 목록 HTML을 for 루프를 통해 회원 수만큼 동적으로 생성 및 응답
```
<br>

#### 회원 목록 조회 결과 화면  
<img width="145" alt="스크린샷 2022-08-08 오후 12 21 02" src="https://user-images.githubusercontent.com/80838501/183332011-cefd3c62-1e81-4b06-9dd7-9952adb8d28c.png">

<br>
<br>

### 템플릿 엔진
```
지금까지 서블릿과 자바 코드만으로 HTML을 만들었는데,서블릿을 이용해 HTML을 동적으로 만들 수 있었다. 하지만 java 코드로 HTML을 만드는 것은 복잡하고
비효율적이기 때문에 차라리 HTML 문서에 동적으로 변경해야 하는 부분에만 java 코드를 넣으면 더 편리할 것이다. `템플릿 엔진`을 사용하면 HTML 문서에서
필요한 곳만 코드를 적용해 동적으로 변경할 수 있다.
템플릿 엔진에는 JSP, Thymeleaf, Freemarker, Velocity등이 있다. JSP로 동일한 작업을 더욱 간단하게 해보자!
```
