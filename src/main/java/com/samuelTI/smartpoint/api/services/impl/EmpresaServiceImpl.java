package com.samuelTI.smartpoint.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.samuelTI.smartpoint.api.entities.Empresa;
import com.samuelTI.smartpoint.api.repository.EmpresaRepository;
import com.samuelTI.smartpoint.api.services.EmpresaService;

@Service
public class EmpresaServiceImpl implements EmpresaService{
	
	private static final Logger log = LoggerFactory.getLogger(EmpresaServiceImpl.class);
	
	@Autowired
	private EmpresaRepository empresaRepository;
	
	@Override
	public Optional<Empresa> buscarPorCnpj(String cnpj){
		log.info("Find one employee for the CNPJ {}", cnpj);
		return Optional.ofNullable(empresaRepository.findByCnpj(cnpj));
	}
	
	@Override
	public Empresa persitEmpresa(Empresa empresa) {
		log.info("Persisting company {}", empresa);
		return this.empresaRepository.save(empresa);
	}

}
