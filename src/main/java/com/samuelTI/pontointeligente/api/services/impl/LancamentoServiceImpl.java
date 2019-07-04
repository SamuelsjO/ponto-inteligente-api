package com.samuelTI.pontointeligente.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.samuelTI.pontointeligente.api.entities.Lancamento;
import com.samuelTI.pontointeligente.api.repository.LancamentoRepository;
import com.samuelTI.pontointeligente.api.services.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService{

	private static final Logger log = LoggerFactory.getLogger(LancamentoServiceImpl.class);
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Override
	public Page<Lancamento> buscarPorFuncionario(Long funcionarioId, PageRequest pageRequest){
		log.info("Buscando lançamento ID {}", funcionarioId);
		return this.lancamentoRepository.findByFuncionarioId(funcionarioId, pageRequest);
		
	}
	
	@Override
	@Cacheable("lancamentoPorId")
	public Optional<Lancamento> buscarById(Long id){
		log.info("Buscando um lançamento pelo ID {}", id);
		return this.lancamentoRepository.findById(id);
	}
	
	@Override
	@CachePut("lancamentoPorId")
	public Lancamento persitir(Lancamento lancamento) {
		log.info("Persitindo o lançamento: {}", lancamento);
		return this.lancamentoRepository.save(lancamento);
	}
	
	@Override
	public void remover(Long id) {
		log.info("Removendoo lançamento ID {}", id);
		this.lancamentoRepository.deleteById(id);
	}


}
