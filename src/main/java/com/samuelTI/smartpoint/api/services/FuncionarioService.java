package com.samuelTI.smartpoint.api.services;

import java.util.Optional;

import com.samuelTI.smartpoint.api.entities.Funcionario;

public interface FuncionarioService {

	/**
	 * Persisting one employee in the data base
	 * 
	 * @param funcionario
	 * @return Funcionario
	 */
	Funcionario persitirFunc(Funcionario funcionario);
	
	/**
	 * Search and return on employee given CPF
	 * 
	 * @param cpf
	 * @return Optional<Funcionario>
	 */
	Optional<Funcionario> buscarPorCpf(String cpf);
	
	/**
	 * Search and return one employee given a email.
	 * 
	 * @param email
	 * @return Optional<Funcionario>
	 */
	Optional<Funcionario> buscarPorEmail(String email);
	
	/**
	 * Search and return a employee by ID.
	 * 
	 * @param id
	 * @return Optional<Funcionario>
	 */
	Optional<Funcionario> buscarPorId(Long id);

	
}
