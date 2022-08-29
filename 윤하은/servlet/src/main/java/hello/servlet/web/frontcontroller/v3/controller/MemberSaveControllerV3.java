package hello.servlet.web.frontcontroller.v3.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;
import org.springframework.boot.Banner;

import java.util.Map;

public class MemberSaveControllerV3 implements ControllerV3 {

    MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    public ModelView process(Map<String, String> paramMap) {

        //요청 파라미터 정보를 담은 맵에서 파라미터 정보 얻어오기
        String username = paramMap.get("username");
        int age = Integer.parseInt(paramMap.get("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        //모델뷰 객체에 논리이름 담아서 생성
        ModelView mv = new ModelView("save-result");

        //새로 저장한 멤버를 mv에 저장
        mv.getModel().put("member", member);
        return mv;
    }
}
