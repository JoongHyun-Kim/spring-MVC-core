package hello.servlet.domain.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MemberRepositoryTest {

    MemberRepository memberRepository = MemberRepository.getInstance();

    @AfterEach
    void afterEach()
    {
        memberRepository.clearStore();
    }

    @Test
    void save()
    {
        //given
        Member savedMember = new Member("haeun", 24);

        //when
        memberRepository.save(savedMember);

        //then
        Member findMember = memberRepository.findById(savedMember.getId());
        //assertThat(findMember).isEqualTo(savedMember);
        Assertions.assertThat(findMember).isEqualTo(savedMember);

    }

    @Test
    void findAll()
    {
        //given
        Member member1 = new Member("haeun", 24);
        Member member2 = new Member("eunha", 25);

        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> list = memberRepository.findAll();

        //then
        Assertions.assertThat(list.size()).isEqualTo(2);
        Assertions.assertThat(list).contains(member1, member2);

    }
}
