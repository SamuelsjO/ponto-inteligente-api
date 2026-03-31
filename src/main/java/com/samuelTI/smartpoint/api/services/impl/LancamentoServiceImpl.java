package com.samuelTI.smartpoint.api.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.samuelTI.smartpoint.api.dtos.PageResult;
import com.samuelTI.smartpoint.api.entities.Lancamento;
import com.samuelTI.smartpoint.api.repository.LancamentoRepository;
import com.samuelTI.smartpoint.api.services.LancamentoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class LancamentoServiceImpl implements LancamentoService {

	private final LancamentoRepository lancamentoRepository;

	@Override
	public PageResult<Lancamento> buscarPorFuncionario(Long funcionarioId, int page, int size) {
		log.info("Find launches by employee ID {}", funcionarioId);
		List<Lancamento> all = lancamentoRepository.findByFuncionarioId(funcionarioId);
		return PageResult.of(all, page, size);
	}

	@Override
	@Cacheable("launchById")
	public Optional<Lancamento> buscarById(Long id) {
		log.info("Find launch by ID {}", id);
		return lancamentoRepository.findById(id);
	}

	@Override
	@CachePut("launchById")
	public Lancamento persitir(Lancamento lancamento) {
		log.info("Persisting launch: {}", lancamento);
		var agora = LocalDateTime.now();
		if (lancamento.getId() == null) {
			lancamento.setDataCriacao(agora);
		}
		lancamento.setDataAtualizacao(agora);
		return lancamentoRepository.save(lancamento);
	}

	@Override
	public void remover(Long id) {
		log.info("Removing launch ID {}", id);
		lancamentoRepository.deleteById(id);
	}
}
