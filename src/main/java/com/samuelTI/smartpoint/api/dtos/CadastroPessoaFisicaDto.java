package com.samuelTI.smartpoint.api.dtos;

import java.util.Optional;

import jakarta.validation.constraints.Email;
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
public class CadastroPessoaFisicaDto {

	private Long id;

	@NotEmpty(message = "Name can not be empty.")
	@Length(min = 3, max = 50, message = "Name must contain between 3 and 50 characters.")
	private String nome;

	@NotEmpty(message = "Email can not be empty.")
	@Length(min = 3, max = 50, message = "Email must contain between 3 and 50 characters.")
	@Email(message = "Invalid Email")
	private String email;

	@NotEmpty(message = "Password can not be empty.")
	private String senha;

	@NotEmpty(message = "CPF can not be empty.")
	@CPF(message = "Invalid CPF.")
	private String cpf;

	private Optional<String> valorHora = Optional.empty();
	private Optional<String> qtdHorasTrabalhoDia = Optional.empty();
	private Optional<String> qtdHorasAlmoco = Optional.empty();

	@NotEmpty(message = "CNPJ can not be empty.")
	@CNPJ(message = "Invalid CNPJ.")
	private String cnpj;
}
