package com.samuelTI.smartpoint.api.entities;

import java.time.LocalDateTime;

import com.samuelTI.smartpoint.api.enums.TipoEnum;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Lancamento {

	private Long id;
	private LocalDateTime data;
	private String descricao;
	private String localizacao;
	private LocalDateTime dataCriacao;
	private LocalDateTime dataAtualizacao;
	private String tipo;
	private Long funcionarioId;

	public TipoEnum getTipo() {
		return tipo == null ? null : TipoEnum.valueOf(tipo);
	}

	public void setTipo(TipoEnum tipo) {
		this.tipo = tipo == null ? null : tipo.name();
	}

	public String getTipoString() {
		return tipo;
	}
}
