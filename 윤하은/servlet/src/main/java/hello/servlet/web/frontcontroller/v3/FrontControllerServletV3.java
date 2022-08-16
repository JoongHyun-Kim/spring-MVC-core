package hello.servlet.web.frontcontroller.v3;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v2.controller.MemberFormControllerV2;
import hello.servlet.web.frontcontroller.v2.controller.MemberListControllerV2;
import hello.servlet.web.frontcontroller.v2.controller.MemberSaveControllerV2;
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

@WebServlet(name = "frontControllerServletV3", urlPatterns = "/front-controller/v3/*")
public class FrontControllerServletV3 extends HttpServlet {

    Map<String, ControllerV3> controllerMap = new HashMap<>();

    public FrontControllerServletV3() {
        controllerMap.put("/front-controller/v3/members/new-form", new MemberFormControllerV3());
        controllerMap.put("/front-controller/v3/members/save", new MemberSaveControllerV3());
        controllerMap.put("/front-controller/v3/members", new MemberListControllerV3());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //요청 url 얻어와서 url에 매핑된 컨트롤러 찾기
        String requestURI = request.getRequestURI();
        ControllerV3 controller = controllerMap.get(requestURI);

        if(controller == null)
        {
            //404 NOT FOUND
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        //요청 파라미터 모두 담아서 전달하기
        Map<String, String> paramMap = createParamMap(request);

        //컨트롤러 로직 실행
        //mv는 렌더링해야하는 뷰의 논리 이름 담고 있음
        //회원 저장/회원 목록 컨트롤러가 실행됐다면 mv의 model 맵에 회원 정보가 저장되어있음
        ModelView mv = controller.process(paramMap);

        //viewResolver : 논리이름을 가지고 전체 jsp 경로로 변환해줌
        /*String viewPath = "WEB-INF/" + mv.getViewName()+".jsp";
        MyView myView = new MyView(viewPath);

        myView.render(request, response);*/

        String viewPath = mv.getViewName();
        MyView view = viewResolver(viewPath);
        view.render(mv.getModel(), request, response);

    }

    private MyView viewResolver(String viewPath){

        return new MyView("/WEB-INF/views/" + viewPath +".jsp");
    }
    private Map<String, String> createParamMap(HttpServletRequest request) {

        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
