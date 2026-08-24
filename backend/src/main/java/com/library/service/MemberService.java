package com.library.service;

import com.library.model.Member;
import com.library.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public List<Member> getAllMembers(String search) {
        if (search != null && !search.trim().isEmpty()) {
            return memberRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByMemberId(search, search);
        }
        return memberRepository.findAllByOrderByMemberId();
    }

    public void addMember(Member member) {
        memberRepository.save(member);
    }

    public Member updateMember(int memberId, Member member) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            return null;
        }
        Member existing = optionalMember.get();
        existing.setName(member.getName());
        existing.setEmail(member.getEmail());
        existing.setPhone(member.getPhone());
        return memberRepository.save(existing);
    }

    public boolean deleteMember(int memberId) {
        if (!memberRepository.existsById(memberId)) {    
            return false;
        }
        memberRepository.deleteById(memberId);
        return true;
    }
}
