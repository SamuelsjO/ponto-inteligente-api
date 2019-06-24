package com.samuelTI.pontointeligente.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.samuelTI.pontointeligente.api.entities.Funcionario;
import com.samuelTI.pontointeligente.api.repository.FuncionarioRepository;
import com.samuelTI.pontointeligente.api.services.FuncionarioService;

@Service
public class FuncionarioServiceImpl implements FuncionarioService{

	private static final Logger log = LoggerFactory.getLogger(FuncionarioServiceImpl.class);
	
	@Autowired
	private FuncionarioRepository funcionarioRepository;
	
	@Override
	public Funcionario persitirFunc(Funcionario funcionario) {
		log.info("Persitindo funcionario: {}", funcionario);
		return this.funcionarioRepository.save(funcionario);
	}
	
	@Override
	public Optional<Funcionario> buscarPorCpf(String cpf) {
		log.info("Buscando funcionario pelo cpf {}", cpf);
		return Optional.ofNullable(this.funcionarioRepository.findByCpf(cpf));
	}
	
	@Override
	public Optional<Funcionario> buscarPorEmail(String email) {
		log.info("Buscando funcionario pelo email {}", email);
		return Optional.ofNullable(this.funcionarioRepository.findByEmail(email));
		
	}
	
	@Override
	public Optional<Funcionario> buscarPorId(Long id){
		log.info("Buscando funcionario pelo id {}", id);
		return this.funcionarioRepository.findById(id);
	}
}
