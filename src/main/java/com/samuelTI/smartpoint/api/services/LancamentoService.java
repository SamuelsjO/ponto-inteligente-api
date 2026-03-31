package com.samuelTI.smartpoint.api.services;

import java.util.Optional;

import com.samuelTI.smartpoint.api.dtos.PageResult;
import com.samuelTI.smartpoint.api.entities.Lancamento;

public interface LancamentoService {

	PageResult<Lancamento> buscarPorFuncionario(Long funcionarioId, int page, int size);

	Lancamento persitir(Lancamento lancamento);

	void remover(Long id);

	Optional<Lancamento> buscarById(Long id);
}
