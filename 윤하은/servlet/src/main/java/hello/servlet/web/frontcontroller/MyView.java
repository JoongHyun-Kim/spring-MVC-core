package hello.servlet.web.frontcontroller;

import org.apache.coyote.Request;

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


    public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        modelToRequestAttribute(model, request);
        //forward 로직
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);

    }
    public void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        //forward 로직
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }

    private void modelToRequestAttribute(Map<String, Object> model, HttpServletRequest request) {
        //request 객체에 model 에 담긴 회원 정보 다 넣어주기
        model.forEach((key, value) -> request.setAttribute(key, value));
    }
}
