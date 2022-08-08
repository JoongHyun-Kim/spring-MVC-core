# Section 3. 서블릿, JSP, MVC 패턴
> 코어와 모듈을 만든 다음, 서블릿, JSP, MVC 패턴을 차례차례 사용해 애플리케이션 발전시켜 나가기
## 회원 관리 웹 애플리케이션 요구사항
```
회원 정보
  이름: username
  나이: age
기능 요구사항
  회원 저장
  회원 목록 조회
```
<br>
<br>

### 회원 도메인
#### Member
> 회원 도메인
```java
package hello.servlet.domain.member;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Member {
    private Long id;
    private String username;
    private int age;

    public Member() { //기본 생성자
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
```
<br>
<br>

#### MemberRepository
```java
package hello.servlet.domain.member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 동시성 문제가 고려되어 있지 않기 때문에 실무에서는 ConcurrentHashMap, AtomicLong 사용 고려한다.
 */
public class MemberRepository {
    private static Map<Long, Member> store = new HashMap<>(); //static 사용
    private static long sequence = 0L; //static 사용해 하나만 사용되도록

    private static final MemberRepository instance = new MemberRepository(); //싱글톤
    public static MemberRepository getInstance() {
        return instance;
    }

    private MemberRepository() { //싱글톤일 때는 private으로 막아 아무나 생성하지 못하도록 해주기
    }

    //회원 저장
    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    //Id로 회원 조회
    public Member findById(Long id) {
        return store.get(id);
    }

    //회원 전체 조회
    public List<Member> findAll() {
        return new ArrayList<>(store.values()); //store에 있는 모든 값들을 꺼내 새로운 ArrayList에 담아 return
        //밖에서 ArrayList에 값을 넣거나 조작해도 store에 있는 값을 건들고 싶지 않기 때문
    }

    //store clear (테스트 사용)
    public void clearStore() {
        store.clear();
    }
}
```
<br>
<br>

### 테스트 
#### MemberRepositoryTest
```java
package hello.servlet.domain.member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class MemberRepositoryTest {
    MemberRepository memberRepository = MemberRepository.getInstance(); //싱글톤이기 때문에 new X

    @AfterEach
    void afterEach() { //테스트 끝날 때마다 초기화
        memberRepository.clearStore();
    }

    /* 회원 저장 텍스트 */
    @Test
    void save() {
        //given
        Member member = new Member("hello", 20);

        //when
        Member savedMember = memberRepository.save(member);

        //then
        Member findMember = memberRepository.findById(savedMember.getId());
        assertThat(findMember).isEqualTo(savedMember);
    }

    /* 회원 조회 테스트 */
    @Test
    void findAll() {
        //given
        Member member1 = new Member("member1", 20);
        Member member2 = new Member("member2", 30);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> result = memberRepository.findAll();

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(member1, member2);
    }
}
```
