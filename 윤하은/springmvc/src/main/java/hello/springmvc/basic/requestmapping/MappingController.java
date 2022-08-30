package hello.springmvc.basic.requestmapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class MappingController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    //단순 url 요청 매핑
    @RequestMapping({"/hello-basic", "/hello-go"})
    public String helloBasic() {
        log.info("hello-basic");
        return "ok";
    }

    //경로변수 요청 매핑
    @RequestMapping("/mapping/{userId}")
    public String mappingPath(@PathVariable("userId") String id){
        log.info("userId = {}", id);
        return "ok";
    }

    //경로 변수명과 파라미터 변수명 일치하면 생략가능
    /*@RequestMapping("/mapping/{userId}")
    public String mappingPath(@PathVariable String userId){
        log.info("userId = {}", userId);
        return "ok";
    }*/

    //경로 변수 다중 사용
    @RequestMapping("/mapping/users/{userId}/orders/{orderNum}")
    public String mappingPath(@PathVariable("userId") String id, @PathVariable int orderNum) {
        log.info("userId = {}, orderNum = {}", id, orderNum);
        return "ok";
    }





    //http 요청 메소드 조건 추가
    @RequestMapping(value = "/mapping-get-v1", method = RequestMethod.GET)
    public String mappingGetV1() {
        log.info("mappingGetV1");
        return "ok";
    }

    //http 요청 메소드 편리한 애노테이션 사용
    @GetMapping(value = "/mapping-get-v2")
    public String mappingGetV2() {
        log.info("mappingGetV2");
        return "ok";
    }

    //쿼리파라미터 조건 추가
    @RequestMapping(value = "/mapping-param", params = "mode=debug")
    public String mappingParam() {
        log.info("mappingParam");
        return "ok";
    }
    //특정헤더 조건 추가
    @RequestMapping(value = "/mapping-header", headers = "mode=debug")
    public String mappingHeader() {
        log.info("mappingHeader");
        return "ok";
    }

    //미디어 타입 조건 추가 - contentType 헤더, consumes
    @RequestMapping(value = "/mapping-consume", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String mappingConsumes() {
        log.info("mappingConsumes");
        return "ok";
    }

    //미디어 타입 조건 추가 - accept 헤더, produces
    @RequestMapping(value = "/mapping-produce", produces = MediaType.TEXT_HTML_VALUE)
    public String mappingProduces() {
        log.info("mappingProduces");
        return "ok";
    }


}
