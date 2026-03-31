package com.samuelTI.smartpoint.api.security.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenUtil {

	private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

	static final String CLAIM_KEY_USERNAME = "sub";
	static final String CLAIM_KEY_ROLE = "role";
	static final String CLAIM_KEY_CREATED = "created";

	private static final int MIN_SECRET_BYTES = 32; // 256-bit minimum for HMAC-SHA256

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private Long expiration;

	private SecretKey signingKey;

	@PostConstruct
	void init() {
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length < MIN_SECRET_BYTES) {
			throw new IllegalStateException(
					"jwt.secret deve ter no mínimo " + MIN_SECRET_BYTES + " bytes (256-bit). Atual: " + keyBytes.length);
		}
		this.signingKey = Keys.hmacShaKeyFor(keyBytes);
	}

	public String getUsernameFromToken(String token) {
		Claims claims = getClaimsFromToken(token);
		return claims != null ? claims.getSubject() : null;
	}

	public Date getExpirationDateFromToken(String token) {
		Claims claims = getClaimsFromToken(token);
		return claims != null ? claims.getExpiration() : null;
	}

	public String refreshToken(String token) {
		Claims claims = getClaimsFromToken(token);
		if (claims == null) {
			return null;
		}
		Map<String, Object> newClaims = new HashMap<>(claims);
		newClaims.put(CLAIM_KEY_CREATED, new Date());
		return gerarToken(newClaims);
	}

	public boolean tokenValido(String token) {
		if (token == null || token.isBlank()) {
			return false;
		}
		Claims claims = getClaimsFromToken(token);
		if (claims == null || claims.getSubject() == null) {
			return false;
		}
		Date exp = claims.getExpiration();
		// Token sem expiração é inválido
		return exp != null && !exp.before(new Date());
	}

	public String obterToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
		userDetails.getAuthorities().forEach(authority -> claims.put(CLAIM_KEY_ROLE, authority.getAuthority()));
		claims.put(CLAIM_KEY_CREATED, new Date());
		return gerarToken(claims);
	}

	private Claims getClaimsFromToken(String token) {
		if (token == null || token.isBlank()) {
			return null;
		}
		try {
			return Jwts.parser()
					.verifyWith(signingKey)
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (JwtException e) {
			log.debug("Token JWT inválido: {}", e.getMessage());
			return null;
		}
	}

	private Date gerarDataExpiracao() {
		return new Date(System.currentTimeMillis() + expiration * 1000);
	}

	private String gerarToken(Map<String, Object> claims) {
		return Jwts.builder()
				.claims(claims)
				.expiration(gerarDataExpiracao())
				.signWith(signingKey)
				.compact();
	}
}
