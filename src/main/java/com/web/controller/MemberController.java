package com.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web.domain.CheckMemberEmail;
import com.web.domain.JoinDTO;
import com.web.domain.Member;
import com.web.domain.Role;
import com.web.jwt.JWTUtil;
import com.web.persistence.MemberRepository;
import com.web.service.MemberService;
import com.web.service.TokenService;

@RestController
public class MemberController {
	// 회원정보 서비스
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private TokenService tokenService;
	
	
	private final JWTUtil jwtUtil;
    public MemberController(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
	

	@PostMapping("/checkId")
	public String checkId(@RequestBody JoinDTO joinDTO) {
		String res = memberService.checkId(joinDTO.getUsername());
		return res;
	}
	

	@PostMapping("/checkEmail")
	public String checkEmail(@RequestBody JoinDTO joinDTO) {
		String res = memberService.checkEmail(joinDTO);
		return res;
	}
	

	@PostMapping("/checkMemberEmail")
	public String checkMemberEmail(@RequestBody JoinDTO joinDTO) {
		String res = memberService.checkMemberEmail(joinDTO);
		return res;
	}
	

	@PostMapping("/checkCode")
	public String checkCode(@RequestBody JoinDTO joinDTO) {
		String res = memberService.checkCode(joinDTO);
		return res;
	}
	

	@PostMapping("/join")
	public String join(@RequestBody JoinDTO joinDTO) {
		String res = memberService.join(joinDTO);
		return res;
	}
	

	@PostMapping("/getIdRole")
    public Map<String, Object> getCurrentMember(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token) {
		Map<String, Object> map = new HashMap<>();
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            

            Role role = jwtUtil.getRole(jwtToken);
            map.put("Role", role);
            

            String username = jwtUtil.getUsername(jwtToken);
            Member member = memberService.getMemberInfo(username);
            if(member != null) {
            	map.put("result", "Success");
            	map.put("member", member);
            } else {
            	map.put("result", "failure");
            }
        }
        return map;
    }
	

		@PostMapping("/getMemberInfo")
		public Map<String, Object> getMemberInfo(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token){
			System.out.println("token 값 : " + token);
			Map<String, Object> map = new HashMap<>();
			boolean validTocken = tokenService.existMember(token);
			if(validTocken) {
				Member currentMember = tokenService.getMemberByMemberNum(token);
				map.put("result", "Success");
				map.put("currentMember", currentMember);
				System.out.println("성공");
			} else {
				map.put("result", "Failure");
				System.out.println("실패");
			}
			return map;
		}

		
	// 아이디 찾기
		@GetMapping("/getUserName")
	    public String getMemberName(@RequestParam String data, @RequestParam String searchMethod) {
	        Member member = null;
	        if ("email".equals(searchMethod)) {
	            member = memberRepo.findByEmail(data);
	        } else if ("phone".equals(searchMethod)) {
	            member = memberRepo.findByPhoneNum(data);
	        }
	        return member != null ? member.getUsername() : null;
	    }
		
		// 회원정보 수정
		@PostMapping("/editMemberInfo")
		public String editMemberInfo(@RequestBody JoinDTO joinDTO) {
			// 멤버넘버로 찾자
			String res = memberService.editMemberInfo(joinDTO);
			return res;
		}
		
		// Pwd 찾기 모달 띄우기 
		@PostMapping("/findPwd")
		public Map<String, Object> findPwd(@RequestBody JoinDTO joinDTO) {
			System.out.println(joinDTO);
			Map<String, Object> resultMap = memberService.findPwd(joinDTO);
			return resultMap;
		}
		// Pwd 재설정 
		@PostMapping("/editPassword")
		public String editPassword(@RequestBody JoinDTO joinDTO) {
			System.out.println(joinDTO);
			String result = memberService.editPwd(joinDTO);
			return result;
		}
		
}