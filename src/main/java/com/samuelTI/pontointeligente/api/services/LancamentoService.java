package com.samuelTI.pontointeligente.api.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.samuelTI.pontointeligente.api.entities.Lancamento;

public interface LancamentoService {

	Page<Lancamento> buscarPorFuncionario(Long funcionarioId, PageRequest pageRequest);

	Optional<Optional<Lancamento>> buscarById(Long id);

	Lancamento persitir(Lancamento lancamento);

	void remover(Long id);

}