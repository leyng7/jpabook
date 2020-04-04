package me.unryeong.jpashop.service;

import me.unryeong.jpashop.domain.Member;
import me.unryeong.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입")
    public void join() {
        //Given
        Member member = new Member();
        member.setName("kim");

        //When
        Long saveId = memberService.join(member);

        //Then
        assertEquals(member, memberRepository.findOne(saveId));
    }

    @Test
    @DisplayName("중복 회원 예외 처리")
    public void duplication() {
        //Given
        Member member = new Member();
        member.setName("kim");

        Member member1 = new Member();
        member1.setName("kim");

        //When
        memberService.join(member);

        //Then
        assertThrows(IllegalStateException.class, () -> memberService.join(member1));
    }

}