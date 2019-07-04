package com.samuelTI.smartpoint.api.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.samuelTI.smartpoint.api.entities.Lancamento;

public interface LancamentoService {

	/**
	 * Search launch by employee
	 * @param funcionarioId
	 * @param pageRequest
	 * @return
	 */
	Page<Lancamento> buscarPorFuncionario(Long funcionarioId, PageRequest pageRequest);

	/**
	 * Persisting launch
	 * @param lancamento
	 * @return
	 */
	Lancamento persitir(Lancamento lancamento);

	/**
	 * Remove launch
	 * @param id
	 */
	void remover(Long id);

	/**
	 * Find launch by ID
	 * @param id
	 * @return
	 */
	Optional<Lancamento> buscarById(Long id);

}
