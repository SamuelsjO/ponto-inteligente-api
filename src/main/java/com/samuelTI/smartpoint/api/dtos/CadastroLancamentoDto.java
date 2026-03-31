package com.samuelTI.smartpoint.api.dtos;

import java.util.Optional;

import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CadastroLancamentoDto {

	private Optional<Long> id = Optional.empty();

	@NotEmpty(message = "Date can not be empty.")
	private String data;

	private String tipo;
	private String descricao;
	private String localizacao;
	private Long funcionarioId;
}
