package com.samuelTI.pontointeligente.api.dtos;

import java.util.Optional;

import javax.validation.constraints.NotEmpty;

public class CadastroLancamentoDto {

	private Optional<Long> id = Optional.empty();
	private String data;
	private String tipo;
	private String descricao;
	private String localazicao;
	private Long funcionarioId;

	public CadastroLancamentoDto() {

	}

	public Optional<Long> getId() {
		return id;
	}

	public void setId(Optional<Long> id) {
		this.id = id;
	}

	@NotEmpty(message = "Data não pode ser vazia")
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getLocalazicao() {
		return localazicao;
	}

	public void setLocalazicao(String localazicao) {
		this.localazicao = localazicao;
	}

	public Long getFuncionarioId() {
		return funcionarioId;
	}

	public void setFuncionarioId(Long funcionarioId) {
		this.funcionarioId = funcionarioId;
	}

	@Override
	public String toString() {
		return "CadastroLancamentoDto [id=" + id + ", data=" + data + ", tipo=" + tipo + ", descricao=" + descricao
				+ ", localazicao=" + localazicao + ", funcionarioId=" + funcionarioId + "]";
	}

}