package com.samuelTI.smartpoint.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.samuelTI.smartpoint.api.entities.Funcionario;

@Transactional(readOnly = true)
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long>{

	/**
	 * Search employee by cpf
	 * @param cpf
	 * @return
	 */
	Funcionario findByCpf(String cpf);
	
	/**
	 * Search employee by email
	 * @param email
	 * @return
	 */
	Funcionario findByEmail(String email);
	
	/**
	 * Search employee by cpf or email
	 * @param cpf
	 * @param email
	 * @return
	 */
	Funcionario findByCpfOrEmail(String cpf, String email);
}
