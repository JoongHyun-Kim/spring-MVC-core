package hello.springmvc.basic.requestmapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/mapping/users")
public class MappingClassController {

    //회원 목록
    @GetMapping
    public String users() {
        return "get users";
    }

    //회원 저장
    @PostMapping("/{userId}")
    public String addUser(@PathVariable("userId") String id) {
        return "post userId=" + id;
    }

    //회원 조회
    @GetMapping("/{userId}")
    public String getUser(@PathVariable("userId") String id) {
        return "get userId=" + id;
    }

    //회원 수정
    @PatchMapping("/{userId}")
    public String updateUser(@PathVariable("userId") String id) {
        return "update userId=" + id;
    }

    //회원 삭제
    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable("userId") String id) {
        return "delete userId="+id;
    }
}
