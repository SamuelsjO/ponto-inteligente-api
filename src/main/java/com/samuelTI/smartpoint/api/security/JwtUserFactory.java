package com.samuelTI.smartpoint.api.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.samuelTI.smartpoint.api.entities.Funcionario;
import com.samuelTI.smartpoint.api.enums.PerfilEnum;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUserFactory {

	public static JwtUser create(Funcionario funcionario) {
		return new JwtUser(
				funcionario.getId(),
				funcionario.getEmail(),
				funcionario.getSenha(),
				mapToGrantedAuthorities(funcionario.getPerfil()));
	}

	private static List<GrantedAuthority> mapToGrantedAuthorities(PerfilEnum perfilEnum) {
		return List.of(new SimpleGrantedAuthority(perfilEnum.toString()));
	}
}
