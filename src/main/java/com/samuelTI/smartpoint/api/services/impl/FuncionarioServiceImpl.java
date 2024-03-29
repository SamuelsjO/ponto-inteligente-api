package com.samuelTI.smartpoint.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.samuelTI.smartpoint.api.entities.Funcionario;
import com.samuelTI.smartpoint.api.repository.FuncionarioRepository;
import com.samuelTI.smartpoint.api.services.FuncionarioService;

@Service
public class FuncionarioServiceImpl implements FuncionarioService{

	private static final Logger log = LoggerFactory.getLogger(FuncionarioServiceImpl.class);
	
	@Autowired
	private FuncionarioRepository funcionarioRepository;
	
	@Override
	public Funcionario persitirFunc(Funcionario funcionario) {
		log.info("Persisting employee: {}", funcionario);
		return this.funcionarioRepository.save(funcionario);
	}
	
	@Override
	public Optional<Funcionario> buscarPorCpf(String cpf) {
		log.info("Find employee by cpf{}", cpf);
		return Optional.ofNullable(this.funcionarioRepository.findByCpf(cpf));
	}
	
	@Override
	public Optional<Funcionario> buscarPorEmail(String email) {
		log.info("Find employee by email {}", email);
		return Optional.ofNullable(this.funcionarioRepository.findByEmail(email));
		
	}
	
	@Override
	public Optional<Funcionario> buscarPorId(Long id){
		log.info("Find employee by id {}", id);
		return this.funcionarioRepository.findById(id);
	}
}
