package com.samuelTI.smartpoint.api.services.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.samuelTI.smartpoint.api.entities.Empresa;
import com.samuelTI.smartpoint.api.repository.EmpresaRepository;
import com.samuelTI.smartpoint.api.services.EmpresaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmpresaServiceImpl implements EmpresaService {

	private final EmpresaRepository empresaRepository;

	@Override
	public Optional<Empresa> buscarPorCnpj(String cnpj) {
		log.info("Find company by CNPJ {}", cnpj);
		return empresaRepository.findByCnpj(cnpj);
	}

	@Override
	public Empresa persitEmpresa(Empresa empresa) {
		log.info("Persisting company: {}", empresa);
		var agora = LocalDateTime.now();
		if (empresa.getId() == null) {
			empresa.setDataCriacao(agora);
		}
		empresa.setDataAtualizacao(agora);
		return empresaRepository.save(empresa);
	}
}
