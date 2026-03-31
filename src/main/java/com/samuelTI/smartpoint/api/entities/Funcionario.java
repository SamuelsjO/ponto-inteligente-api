package com.samuelTI.smartpoint.api.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.samuelTI.smartpoint.api.enums.PerfilEnum;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "senha")
public class Funcionario {

	private Long id;
	private String nome;
	private String email;
	private String senha;
	private String cpf;
	private BigDecimal valorHora;
	private Float qtdHorasTrabalhoDia;
	private Float qtdHorasAlmoco;
	private String perfil;
	private LocalDateTime dataCriacao;
	private LocalDateTime dataAtualizacao;
	private Long empresaId;

	public Optional<BigDecimal> getValorHoraOpt() {
		return Optional.ofNullable(valorHora);
	}

	public Optional<Float> getQtdHorasTrabalhoDiaOpt() {
		return Optional.ofNullable(qtdHorasTrabalhoDia);
	}

	public Optional<Float> getQtdHorasAlmocoOpt() {
		return Optional.ofNullable(qtdHorasAlmoco);
	}

	public PerfilEnum getPerfil() {
		return perfil == null ? null : PerfilEnum.valueOf(perfil);
	}

	public void setPerfil(PerfilEnum perfil) {
		this.perfil = perfil == null ? null : perfil.name();
	}

	public String getPerfilString() {
		return perfil;
	}
}
