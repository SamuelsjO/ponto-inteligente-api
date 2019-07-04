package com.samuelTI.smartpoint.api.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samuelTI.smartpoint.api.dtos.CadastroEmpresaDto;
import com.samuelTI.smartpoint.api.entities.Empresa;
import com.samuelTI.smartpoint.api.responses.Response;
import com.samuelTI.smartpoint.api.services.EmpresaService;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

	private static final Logger log = LoggerFactory.getLogger(EmpresaController.class);

	@Autowired
	private EmpresaService empresaService;

	public EmpresaController() {

	}

	/**
	 * Return a company given one CNPJ.
	 * 
	 * @param cnpj
	 * @return ResponseEntity<Response<CadatroEmpresaDto>>
	 */

	@GetMapping(value = "/cnpj/{cnpj}")
	public ResponseEntity<Response<CadastroEmpresaDto>> buscarPorCnpj(@PathVariable("cnpj") String cnpj) {
		log.info("Find company by CNPJ: {}", cnpj);
		Response<CadastroEmpresaDto> response = new Response<CadastroEmpresaDto>();
		Optional<Empresa> empresa = empresaService.buscarPorCnpj(cnpj);

		if (!empresa.isPresent()) {
			log.info("Company not found for the CNPJ" + cnpj);
			response.getErrors().add("Company not found for the CNPJ " + cnpj);
			return ResponseEntity.badRequest().body(response);

		}

		response.setData(this.converteEmpresaDto(empresa.get()));
		return ResponseEntity.ok(response);
	}

	/**
	 * Populate a DTO with the data of a company.
	 * 
	 * @param empresa
	 * @return EmpresaDto
	 */

	private CadastroEmpresaDto converteEmpresaDto(Empresa empresa) {

		CadastroEmpresaDto empresaDto = new CadastroEmpresaDto();
		empresaDto.setId(empresa.getId());
		empresaDto.setCnpj(empresa.getCnpj());
		empresaDto.setRazaoSocial(empresa.getRazaoSocial());

		return empresaDto;
	}
}
