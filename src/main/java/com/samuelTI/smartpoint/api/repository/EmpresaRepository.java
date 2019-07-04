package com.samuelTI.smartpoint.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.samuelTI.smartpoint.api.entities.Empresa;


public interface EmpresaRepository extends JpaRepository<Empresa, Long>{

	/**
	 * Search company by cnpj
	 * @param cnpj
	 * @return
	 */
	@Transactional(readOnly = true)
	Empresa findByCnpj(String cnpj);
}
