package com.samuelTI.smartpoint.api.services;

import java.util.Optional;

import com.samuelTI.smartpoint.api.entities.Empresa;

public interface EmpresaService {

	/**
	 * Return one company by CNPJ
	 * 
	 * @param cnpj
	 * @return Optional<Empresa>
	 */
	
	Optional<Empresa> buscarPorCnpj(String cnpj);
		/**
		 * Registered one new company in the data base
		 * 
		 * @param empresa
		 * @return Empresa
		 * 
		 */
		
	Empresa persitEmpresa(Empresa empresa);
	
}
