package com.library.controller;

import com.library.model.Member;
import com.library.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(memberService.getAllMembers(search));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addMember(@RequestBody Member member) {
        try {
            memberService.addMember(member);
            return ResponseEntity.ok(Map.of("message", "Member registered successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<Map<String, String>> updateMember(@PathVariable int memberId, @RequestBody Member member) {
        try {
            Member updated = memberService.updateMember(memberId, member);
            if (updated == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Member not found."));
            }
            return ResponseEntity.ok(Map.of("message", "Member updated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Map<String, String>> deleteMember(@PathVariable int memberId) {
        try {
            if (!memberService.deleteMember(memberId)) {
                return ResponseEntity.status(404).body(Map.of("error", "Member not found."));
            }  
            return ResponseEntity.ok(Map.of("message", "Member deleted successfully!"));
        } catch (Exception e) {  
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }                                                               
    }                                                 
}                                                                                                                                                                                                                                                                                    
     