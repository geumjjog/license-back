package com.web.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.web.domain.Member;
import com.web.domain.SocialType;

public interface MemberRepository extends JpaRepository<Member, Long>{

	Member findByUsername(String username);
	

	Boolean existsByUsername(String username);
	
	Boolean existsByEmail(String email);

	Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String id);

	Member findByEmail(String email);
	
	Optional<Member> findByRefreshToken(String refreshToken);
	
	Member findByPhoneNum(String data);
	
	
	// 아이디 찾기 - 전화번호로 찾기
	Member findByMemberNameAndPhoneNum(String memberName, String phoneNum);
	// 아이디 찾기 - 이메일로 찾기
	Member findByMemberNameAndEmail(String memberName, String email);
	
	// 비밀번호 찾기 - 전화번호로 찾기
	Optional<Member> findByUsernameAndMemberNameAndPhoneNum(String username, String memberName, String phoneNum);
	// 비밀번호 찾기 - 이메일로 찾기
	Optional<Member> findByUsernameAndMemberNameAndEmail(String username, String memberName, String email);
}
