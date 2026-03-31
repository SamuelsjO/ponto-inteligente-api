package com.samuelTI.smartpoint.api.dtos;

import jakarta.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "senha")
public class CadastroPessoaJuridicaDto {

	private Long id;

	@NotEmpty(message = "Name can not be empty.")
	@Length(min = 3, max = 200, message = "Name must contain between 3 and 200 characters.")
	private String nome;

	@NotEmpty(message = "Email can not be empty.")
	@Length(min = 5, max = 200, message = "Email must contain between 5 and 200 characters.")
	private String email;

	@NotEmpty(message = "Password can not be empty.")
	private String senha;

	@NotEmpty(message = "Social reason can not be empty.")
	@Length(min = 5, max = 200, message = "Social reason must contain between 5 and 200 characters.")
	private String razaoSocial;

	@NotEmpty(message = "CNPJ can not be empty.")
	@CNPJ(message = "Invalid CNPJ.")
	private String cnpj;

	@NotEmpty(message = "CPF can not be empty.")
	@CPF(message = "Invalid CPF.")
	private String cpf;
}
