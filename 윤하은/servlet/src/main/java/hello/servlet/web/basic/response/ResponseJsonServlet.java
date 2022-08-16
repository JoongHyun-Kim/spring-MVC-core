package hello.servlet.web.basic.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.servlet.web.basic.HelloData;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "responseJsonServlet", urlPatterns = "/response-json")
public class ResponseJsonServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Content-type : application/json;charset=utf-8

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        HelloData helloData = new HelloData();
        helloData.setUsername("haeun");
        helloData.setAge(24);

        //객체 -> json으로 형태를 바꿔줘야함
        //{"username":"kim","age":20}로 바꿔줘야함

        //객체를 가지고 문자로 바꿔라 <- 이때 json 형식의 문자로 바꿔주는 건지?
        String result = objectMapper.writeValueAsString(helloData);
        response.getWriter().write(result);



    }
}
