package hello.servlet.web.frontcontroller.v4;

import java.util.Map;

public interface ControllerV4 {
    //v4에서는 ModelView 반환이 아니라 viewName(논리이름) 반환
    //로직 수행하려면 파라미터로 요청 파라미터 정보를 담은 맵을 받아야함
    //회원저장, 회원 목록 로직에서 model 맵에 회원 정보를 담아야해서 모델맵을 파라미터로 받아야함

    String process(Map<String, String> paramMap, Map<String, Object> model);
}
