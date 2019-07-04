package com.samuelTI.smartpoint.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.samuelTI.smartpoint.api.entities.Lancamento;
import com.samuelTI.smartpoint.api.repository.LancamentoRepository;
import com.samuelTI.smartpoint.api.services.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService{

	private static final Logger log = LoggerFactory.getLogger(LancamentoServiceImpl.class);
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Override
	public Page<Lancamento> buscarPorFuncionario(Long funcionarioId, PageRequest pageRequest){
		log.info("Find launch ID {}", funcionarioId);
		return this.lancamentoRepository.findByFuncionarioId(funcionarioId, pageRequest);
		
	}
	
	@Override
	@Cacheable("launchById")
	public Optional<Lancamento> buscarById(Long id){
		log.info("Find on launch by ID{}", id);
		return this.lancamentoRepository.findById(id);
	}
	
	@Override
	@CachePut("launchById")
	public Lancamento persitir(Lancamento lancamento) {
		log.info("Persisting the launch: {}", lancamento);
		return this.lancamentoRepository.save(lancamento);
	}
	
	@Override
	public void remover(Long id) {
		log.info("Remove launch ID{}", id);
		this.lancamentoRepository.deleteById(id);
	}


}
