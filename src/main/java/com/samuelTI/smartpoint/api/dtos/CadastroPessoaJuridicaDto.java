package com.samuelTI.smartpoint.api.dtos;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

public class CadastroPessoaJuridicaDto {

	private Long id;
	private String nome;
	private String email;
	private String senha;
	private String razaoSocial;
	private String cnpj;
	private String cpf;

	@NotEmpty(message = "CPF can not be empty.")
	@CPF(message = "Invalid CPF.")
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public CadastroPessoaJuridicaDto() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@NotEmpty(message = "Name can not be empty")
	@Length(min = 3, max = 200, message = "Name must contain between 3 and 200 characters.")
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@NotEmpty(message = "Email can not be empty.")
	@Length(min = 5, max = 200, message = "Email must contain between 5 and 200 characters.")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@NotEmpty(message = "Password can not be empty")
	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@NotEmpty(message = "Social reason can not be empty.")
	@Length(min = 5, max = 200, message = "Social reason must contain between 5 and 200 characteres.")
	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	@NotEmpty(message = "CNPJ can not be empty.")
	@CNPJ(message = "Invalid CNPJ.")
	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	@Override
	public String toString() {
		return "CadastroPJDto [id=" + id + ", nome=" + nome + ", email=" + email + ", senha=" + senha + ", razaoSocial="
				+ razaoSocial + ", cnpj=" + cnpj + "]";
	}
	

}
