package com.samuelTI.smartpoint.api.security.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.samuelTI.smartpoint.api.security.JwtUserFactory;
import com.samuelTI.smartpoint.api.services.FuncionarioService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

	private final FuncionarioService funcionarioService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return funcionarioService.buscarPorEmail(username)
				.map(JwtUserFactory::create)
				.orElseThrow(() -> new UsernameNotFoundException("Email not found."));
	}
}
