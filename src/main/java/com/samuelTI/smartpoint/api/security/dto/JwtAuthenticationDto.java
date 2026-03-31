package com.samuelTI.smartpoint.api.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record JwtAuthenticationDto(
		@NotEmpty(message = "Email can not be empty.")
		@Email(message = "Invalid Email.")
		String email,

		@NotEmpty(message = "Password can not be empty.")
		String senha
) {
}
