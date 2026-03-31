package com.samuelTI.smartpoint.api.services.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.samuelTI.smartpoint.api.entities.Funcionario;
import com.samuelTI.smartpoint.api.repository.FuncionarioRepository;
import com.samuelTI.smartpoint.api.services.FuncionarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class FuncionarioServiceImpl implements FuncionarioService {

	private final FuncionarioRepository funcionarioRepository;

	@Override
	public Funcionario persitirFunc(Funcionario funcionario) {
		log.info("Persisting employee: {}", funcionario);
		var agora = LocalDateTime.now();
		if (funcionario.getId() == null) {
			funcionario.setDataCriacao(agora);
		}
		funcionario.setDataAtualizacao(agora);
		return funcionarioRepository.save(funcionario);
	}

	@Override
	public Optional<Funcionario> buscarPorCpf(String cpf) {
		log.info("Find employee by CPF {}", cpf);
		return funcionarioRepository.findByCpf(cpf);
	}

	@Override
	public Optional<Funcionario> buscarPorEmail(String email) {
		log.info("Find employee by email {}", email);
		return funcionarioRepository.findByEmail(email);
	}

	@Override
	public Optional<Funcionario> buscarPorId(Long id) {
		log.info("Find employee by ID {}", id);
		return funcionarioRepository.findById(id);
	}
}
