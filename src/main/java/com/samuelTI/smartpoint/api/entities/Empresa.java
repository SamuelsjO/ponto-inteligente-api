package com.samuelTI.smartpoint.api.entities;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Empresa {

	private Long id;
	private String razaoSocial;
	private String cnpj;
	private LocalDateTime dataCriacao;
	private LocalDateTime dataAtualizacao;
}
