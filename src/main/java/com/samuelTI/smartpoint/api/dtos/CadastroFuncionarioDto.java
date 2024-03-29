package com.samuelTI.smartpoint.api.dtos;

import java.util.Optional;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

public class CadastroFuncionarioDto {

	private Long id;
	private String nome;
	private String email;
	private Optional<String> senha = Optional.empty();
	private Optional<String> valorHora = Optional.empty();
	private Optional<String> qtdHorasTrabalhadoDia = Optional.empty();
	private Optional<String> qtdHorasAlmoco = Optional.empty();

	public CadastroFuncionarioDto() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@NotEmpty(message = "Name can not be empty.")
	@Length(min = 3, max = 200, message = "Name must contain between 3 and 200 characters.")
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@NotEmpty(message = "Email can not be empty.")
	@Length(min = 5, max = 200, message = "Email must contain between 5 and 200 characters.")
	@Email(message = "Invalid Email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Optional<String> getSenha() {
		return senha;
	}

	public void setSenha(Optional<String> senha) {
		this.senha = senha;
	}

	public Optional<String> getValorHora() {
		return valorHora;
	}

	public void setValorHora(Optional<String> valorHora) {
		this.valorHora = valorHora;
	}

	public Optional<String> getQtdHorasTrabalhadoDia() {
		return qtdHorasTrabalhadoDia;
	}

	public void setQtdHorasTrabalhadoDia(Optional<String> qtdHorasTrabalhadoDia) {
		this.qtdHorasTrabalhadoDia = qtdHorasTrabalhadoDia;
	}

	public Optional<String> getQtdHorasAlmoco() {
		return qtdHorasAlmoco;
	}

	public void setQtdHorasAlmoco(Optional<String> qtdHorasAlmoco) {
		this.qtdHorasAlmoco = qtdHorasAlmoco;
	}

	@Override
	public String toString() {
		return "CadastroFuncionarioDto [id=" + id + ", nome=" + nome + ", email=" + email + ", senha=" + senha
				+ ", valorHora=" + valorHora + ", qtdHorasTrabalhadoDia=" + qtdHorasTrabalhadoDia + ", qtdHorasAlmoco="
				+ qtdHorasAlmoco + "]";
	}

}
