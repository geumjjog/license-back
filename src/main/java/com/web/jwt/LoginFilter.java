package com.web.jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.entity.LoginRequest;
import com.web.jwt.dto.CustomUserDetails;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;

	public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}
	
	// 수정 코드
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			// request의 body에서 데이터 추출
			LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

			String username = loginRequest.getUsername();
			String password = loginRequest.getPassword();

			// 나머지 로직은 그대로 유지
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

			return authenticationManager.authenticate(authToken);
			
		} catch (IOException e) {
			throw new AuthenticationServiceException("Failed to parse login request body", e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) {
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

		String email = customUserDetails.getEmail();
		Long memberNum = customUserDetails.getMemberNum();
		String accessToken = jwtUtil.createAccessToken(email);
		
		response.addHeader(jwtUtil.getAccessHeader(), "Bearer " + accessToken);

	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) {
		// 로그인 실패시 401 응답 코드 반환
		response.setStatus(401);
		logger.error("Authentication failed: " + failed.getMessage(), failed);
	}
}