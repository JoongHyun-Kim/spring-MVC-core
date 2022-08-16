package hello.servlet.web.basic.response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

@WebServlet(name = "ResponseHeaderServlet", urlPatterns = "/response-header")
public class ResponseHeaderServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //[status-line]
        //응답 코드 지정
        //직접 숫자 적는 것보다 상수 사용하는거 추천
        response.setStatus(HttpServletResponse.SC_OK);

        //[response-headers]
        response.setHeader("Content-type", "text/plain;charset=utf-8");
        //content-type 설정
        //cache 설정 - 캐시 무효화
        response.setHeader("cache-control", "no-cache, no-store, mustrevalidate");
        response.setHeader("Pragma", "no-cache");
        //임의의 헤더 설정
        response.setHeader("myHeader", "good~");
        //f12 개발자 도구에서 response header 확인하기

        //[Header Content 편의 메소드]
        content(response);

        //[cookie 편의 메소드]
        cookie(response);

        //[redirect 편의 메소드]
        redirect(response);

        //바디 생성
        PrintWriter writer = response.getWriter();
        writer.println("Today is Saturday.");

    }

    private void redirect(HttpServletResponse response) throws IOException {
        //Status Code 302
        //Location: /basic/hello-form.html

        //response.setStatus(HttpServletResponse.SC_FOUND);
        //response.setHeader("Location", "/basic/hello-form.html");
        response.sendRedirect("/basic/hello-form.html");
    }

    private void cookie(HttpServletResponse response) {
        //Set-Cookie: myCookie=good; Max-Age=600;
        //max-age 단위는 sec
        //response.setHeader("Set-Cookie", "myCookie=good;Max-age=600");
        Cookie cookie = new Cookie("myCookie", "good");
        cookie.setMaxAge(600);//600초
        response.addCookie(cookie);
    }

    private void content(HttpServletResponse response) {

        //Content-Type : text/plain;charset=utf-8
        //Content-length : 2
        //response.setHeader("Content-type", "text/plain;charset=utf-8");
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");

        //response.setContentLength(3);//생략시 자동 생성
    }
}
