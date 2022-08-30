package hello.springmvc.basic.request;

import hello.springmvc.basic.requestmapping.HelloData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Controller
public class RequestParamController {

    /**
     * 반환 타입이 없으면서 이렇게 응답에 값을 직접 집어넣으면, view 조회X
     */
    @RequestMapping("/request-param-v1")
    public void requestParamV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        log.info("username={}", username);
        log.info("age={}", age);

        response.getWriter().write("ok");

    }
    @ResponseBody
    @RequestMapping("/request-param-v2")
    public String requestParamV2(@RequestParam("username") String memberName,
                               @RequestParam("age") int memberAge)  {

        log.info("username={}", memberName);
        log.info("age={}", memberAge);
        return "ok";

    }

    //http 요청 파라미터 변수명과 메소드 파라미터 변수명이 같으면 @RequestParam(name="xxx") 에서 name 생략가능
    @ResponseBody
    @RequestMapping("/request-param-v3")
    public String requestParamV3(@RequestParam String username,
                                 @RequestParam int age)  {

        log.info("username={}", username);
        log.info("age={}", age);
        return "ok";

    }


    //타입이 기본형이면 @RequestParam 생략가능
    @ResponseBody
    @RequestMapping("/request-param-v4")
    public String requestParamV4(String username,
                                 int age)  {

        log.info("username={}", username);
        log.info("age={}", age);
        return "ok";

    }

    @ResponseBody
    @RequestMapping("/request-param-required")
    public String requestParamRequired(
            @RequestParam(required = true) String username,
            @RequestParam(required = false) Integer age)  {


        log.info("username={}", username);
        log.info("age={}", age);
        return "ok";

    }

    @ResponseBody
    @RequestMapping("/request-param-default")
    public String requestParamDefault(
            @RequestParam(required = true, defaultValue = "guest") String username,
            @RequestParam(required = false, defaultValue = "-1") int age)  {


        log.info("username={}", username);
        log.info("age={}", age);
        return "ok";

    }

    @ResponseBody
    @RequestMapping("/request-param-map")
    public String requestParamMap(
            @RequestParam Map<String, String> paramMap)  {

        log.info("username={}, age={}", paramMap.get("username"), paramMap.get("age"));
        return "ok";

    }

    /*@ResponseBody
    @RequestMapping("/model-attribute-v1")
    public String modelAttribute(@RequestParam String username,
                                 @RequestParam int age) {
        HelloData data = new HelloData();
        data.setUsername(username);
        data.setAge(age);

        log.info("username={}, age={}", data.getUsername(), data.getAge());
        log.info("data={}", data);

        return "ok";
    }*/

    @ResponseBody
    @RequestMapping("/model-attribute-v1")
    public String modelAttribute(@ModelAttribute HelloData helloData) {

        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());
        log.info("data={}", helloData);

        return "ok";
    }

    @ResponseBody
    @RequestMapping("/model-attribute-v2")
    public String modelAttributeV2(HelloData helloData) {

        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());
        log.info("data={}", helloData);

        return "ok";
    }



}
