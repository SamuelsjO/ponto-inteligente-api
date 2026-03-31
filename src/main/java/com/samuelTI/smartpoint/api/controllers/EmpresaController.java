package com.samuelTI.smartpoint.api.controllers;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samuelTI.smartpoint.api.dtos.CadastroEmpresaDto;
import com.samuelTI.smartpoint.api.entities.Empresa;
import com.samuelTI.smartpoint.api.responses.Response;
import com.samuelTI.smartpoint.api.services.EmpresaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

	private final EmpresaService empresaService;

	@GetMapping(value = "/cnpj/{cnpj}")
	public ResponseEntity<Response<CadastroEmpresaDto>> buscarPorCnpj(@PathVariable("cnpj") String cnpj) {
		log.info("Find company by CNPJ: {}", cnpj);
		var response = new Response<CadastroEmpresaDto>();
		Optional<Empresa> empresa = empresaService.buscarPorCnpj(cnpj);

		if (empresa.isEmpty()) {
			log.info("Company not found for the CNPJ {}", cnpj);
			response.getErrors().add("Company not found for the CNPJ " + cnpj);
			return ResponseEntity.badRequest().body(response);
		}

		response.setData(converteEmpresaDto(empresa.get()));
		return ResponseEntity.ok(response);
	}

	private CadastroEmpresaDto converteEmpresaDto(Empresa empresa) {
		return new CadastroEmpresaDto(empresa.getId(), empresa.getRazaoSocial(), empresa.getCnpj());
	}
}
