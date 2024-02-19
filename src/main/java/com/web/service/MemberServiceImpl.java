package com.web.service;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.web.domain.CheckMemberEmail;
import com.web.domain.JoinDTO;
import com.web.domain.Member;
import com.web.domain.Role;
import com.web.persistence.CheckMemberEmailRepository;
import com.web.persistence.MemberRepository;
@Service
public class MemberServiceImpl implements MemberService{
	
	
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private CheckMemberEmailRepository checkMemberEmailRepository;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
    private BCryptPasswordEncoder passwordEncoder;
	
	
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	public static String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(randomIndex));
        }

        return stringBuilder.toString();
    }
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	public MemberServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
	
	// 아이디 중복확인
	@Override
	public String checkId(String username) {
		// TODO Auto-generated method stub
		if(username.equals("")) {
			return "Empty";
		}
		// 해당 아이디로 검색했을 때 있으면 true 반환
		Boolean result = memberRepository.existsByUsername(username);
		if(result) {
			return "Exist";
		}
		return "사용가능한 아이디 입니다.";
	}
	
	// 이메일 중복확인
	@Override
	public String checkEmail(JoinDTO joinDTO) {
		// TODO Auto-generated method stub
		String email ="";
		if(joinDTO.getDomain().equals("")) {
			email = joinDTO.getEmail();
		}
		email = joinDTO.getEmail() + joinDTO.getDomain();
		System.out.println(email);
		if(joinDTO.getEmail().equals("")) {
			return "Empty";
		}
		// 해당 아이디로 검색했을 때 있으면 true 반환
		Boolean result = memberRepository.existsByEmail(email);
		if(result) {
			return "Exist";
		}
		return "사용가능한 이메일 입니다.";
	}

	// 이메일 인증 
	@Override
	public String checkMemberEmail(JoinDTO joinDTO) {
		// TODO Auto-generated method stub
		String email ="";
		String answer ="";
		if(joinDTO.getDomain().equals("직접입력")) {
			email = joinDTO.getEmail();
		} else {
			email = joinDTO.getEmail() + joinDTO.getDomain();
		}
		SimpleMailMessage message = new SimpleMailMessage();
		String randomInitial = generateRandomString(6);
		try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setSubject("CERTIFICATE.ZIP 회원가입 이메일 인증");
            helper.setTo(email);

            StringBuilder emailContent = new StringBuilder();
            emailContent.append("회원가입을 진행해주셔서 감사합니다.\r\n");
            emailContent.append("이메일 인증을 위해 아래의 코드를.\r\n");
            emailContent.append("회원가입 페이지 코드 입력칸에 입력해주세요.").append("\n");
            emailContent.append("코드 : "+randomInitial).append("\n");
            MimeMultipart multipart = new MimeMultipart();
            // 텍스트 내용 추가
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(emailContent.toString());
            multipart.addBodyPart(textBodyPart);
            mimeMessage.setContent(multipart);
            javaMailSender.send(mimeMessage);
            CheckMemberEmail checkMemberEmail = new CheckMemberEmail();
            checkMemberEmail.setEmail(email);
            checkMemberEmail.setRandomInitial(randomInitial);
            checkMemberEmailRepository.save(checkMemberEmail);
            
            answer = "Success";
        } catch (MessagingException e) {
            e.printStackTrace();
            answer = "Failure";
        }
		return answer;
	}
	
	// 이메일 인증 코드
	@Override
	public String checkCode(JoinDTO joinDTO) {
		// TODO Auto-generated method stub
		String email ="";
		if(joinDTO.getDomain().equals("직접입력")) {
			email = joinDTO.getEmail();
		} else {
			email = joinDTO.getEmail() + joinDTO.getDomain();
		}
		Optional<CheckMemberEmail> optional = checkMemberEmailRepository.findById(email);
		CheckMemberEmail checkMemberEmail = null;
		if(optional.isPresent()) {
			checkMemberEmail = optional.get();
			if(joinDTO.getRandomInitial().equals(checkMemberEmail.getRandomInitial())) {
				checkMemberEmailRepository.deleteById(email);
				return "이메일 인증에 성공하였습니다.";
			}
			return "이메일 인증에 실패하였습니다.";
		}
		return "이메일 인증에 실패하였습니다.";
	}

	// 회원가입
	@Override 
	public String join(JoinDTO joinDTO) {
		// TODO Auto-generated method stub
		Member member = Member.builder()
				.memberName(joinDTO.getMemberName())
				.username(joinDTO.getUsername())
				.email(joinDTO.getEmail()+joinDTO.getDomain())
				.password(bCryptPasswordEncoder.encode(joinDTO.getPassword()))
				.phoneNum(joinDTO.getPhoneNum())
				.membership("일반 회원")
				.role(Role.USER)
				.build();
		memberRepository.save(member);
		return "ok";
	}
	// 필요할 때 토큰 넘겨서 username(아이디)로 멤버 정보 불러오기
	@Override
	public Member getMemberInfo(String username) {
		// TODO Auto-generated method stub
		Member member = memberRepository.findByUsername(username);
		if(member != null) {
			// 아이디값과 일치하는 멤버객체를 반환
			return member; 
		}
		return null;
	}
	
	@Override
	public String editMemberInfo(JoinDTO joinDTO) {
		// TODO Auto-generated method stub
		Optional<Member> optional =  memberRepository.findById(joinDTO.getMemberNum());
		Member member = null;
		if(optional.isPresent()) { // 해당 이메일로 조회시 아이디 존재
			member = optional.get(); // 수정 전 정보
		}
		boolean checkSamePassword = bCryptPasswordEncoder.matches(joinDTO.getPassword(), member.getPassword());
		System.out.println(checkSamePassword);
		if(checkSamePassword) {
			System.out.println("진짜로");
			return "Equal Password";
		}
		if(!joinDTO.getPassword().equals("")) { 
			member.setPassword(bCryptPasswordEncoder.encode(joinDTO.getPassword()));
		}
		member.setMemberName(joinDTO.getMemberName());
		member.setEmail(joinDTO.getEmail());
		member.setPhoneNum(joinDTO.getPhoneNum());
		System.out.println("수정전" + member);
		memberRepository.save(member);
		return "Success";
	}
		//현재 사용중인 사용자 정보 가져오기
		public Member getCurrentUser(Principal principal) {
		    if (principal != null) {
		        String username = principal.getName();
		        return memberRepository.findByUsername(username);
		    }
		    return null;
		}
		
		// 아이디 찾기
		@Override
		public Map<String, Object> findId(JoinDTO joinDTO) {
			// TODO Auto-generated method stub
			Map<String, Object> map = new HashMap<>();
			try {
				Member findMember;
				if(joinDTO.getEmail().equals("")) { 
					// 전화번호로 찾기
					findMember = memberRepository.findByMemberNameAndPhoneNum(joinDTO.getMemberName(),joinDTO.getPhoneNum());
				} else {
					// 이메일로 찾기
					findMember = memberRepository.findByMemberNameAndEmail(joinDTO.getMemberName(),joinDTO.getEmail());
				}
				map.put("username", findMember.getUsername());
				map.put("result", "Success");
				return map;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			map.put("result", "Failure");
			return map;
		}
		// 비밀번호 찾기
		@Override
		public Map<String, Object> findPwd(JoinDTO joinDTO) {
			// TODO Auto-generated method stub
			Map<String, Object> map = new HashMap<>();
			try {
				Optional<Member> optional;
				if(joinDTO.getEmail().equals("")) { 
					// 전화번호로 찾기
					optional = memberRepository.findByUsernameAndMemberNameAndPhoneNum(joinDTO.getUsername(),joinDTO.getMemberName(),joinDTO.getPhoneNum());
				} else {
					// 이메일로 찾기
					optional = memberRepository.findByUsernameAndMemberNameAndEmail(joinDTO.getUsername(),joinDTO.getMemberName(),joinDTO.getEmail());
				}
				if(optional.isPresent()) {
					map.put("result", "Success");
				} else {
					map.put("result", "Failure");
				}
				return map;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return map;
		}
		// 비밀번호 재설정
		@Override
		public String editPwd(JoinDTO joinDTO) {
			// TODO Auto-generated method stub
			Member member =  memberRepository.findByUsername(joinDTO.getUsername());
			System.out.println(member);
			boolean checkSamePassword = bCryptPasswordEncoder.matches(joinDTO.getPassword(), member.getPassword());
			System.out.println(checkSamePassword);
			if(checkSamePassword) {
				return "Equal Password";
			}
			if(!joinDTO.getPassword().equals("")) { 
				member.setPassword(bCryptPasswordEncoder.encode(joinDTO.getPassword()));
			}
			memberRepository.save(member);
			return "Success";
		}
		
		@Override
		@Transactional
		public Page<Member> getMemberList(Pageable pageable) {
			Page<Member> member =  memberRepository.findAll(pageable);
			return member;
		}
		
		
		   // 회원을 삭제하는 메서드
	    public void deleteMemberById(Long memberNum) {
	        Optional<Member> optionalMember = memberRepository.findById(memberNum);
	        if (optionalMember.isPresent()) {
	            Member member = optionalMember.get();
	            memberRepository.delete(member);
	        } else {
	            throw new NoSuchElementException("회원이 존재하지 않습니다. memberId: " + memberNum);
	        }
	    }

	    public Member getMemberByUsername(String username) {
	        return memberRepository.findByUsername(username);
	    }
	    
	    @Override
	    public void updateMember(String username, String memberName, String email, String membership) {
	        Optional<Member> optionalMember = Optional.ofNullable(memberRepository.findByUsername(username));
	        if (optionalMember.isPresent()) {
	            Member member = optionalMember.get();
	            member.setMemberName(memberName);
	            member.setEmail(email);
	            member.setMembership(membership);
	            memberRepository.save(member);
	        } else {
	            throw new NoSuchElementException("Member not found with username: " + username);
	        }
	    }
	    
}
