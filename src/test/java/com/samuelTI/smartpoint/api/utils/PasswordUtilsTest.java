package com.samuelTI.smartpoint.api.utils;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.samuelTI.smartpoint.api.utils.PasswordUtils;

public class PasswordUtilsTest {

	private static final String PASSWORD = "12345";
	private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	
	@Test
	public void testSenhaNula() throws Exception{
		assertNull(PasswordUtils.gerarByCrypt(null));
	}
	
	@Test
	public void testGerarHashSenha() throws Exception {
		String hash = PasswordUtils.gerarByCrypt(PASSWORD);
		
		assertTrue(bCryptPasswordEncoder.matches(PASSWORD, hash));
	}
}
