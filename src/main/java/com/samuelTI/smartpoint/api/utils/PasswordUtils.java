package com.samuelTI.smartpoint.api.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PasswordUtils {

	private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

	public static String gerarByCrypt(String senha) {
		if (senha == null) {
			return null;
		}
		return ENCODER.encode(senha);
	}
}
