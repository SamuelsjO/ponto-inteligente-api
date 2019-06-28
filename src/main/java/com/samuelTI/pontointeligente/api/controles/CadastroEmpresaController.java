package com.samuelTI.pontointeligente.api.controles;

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

import com.samuelTI.pontointeligente.api.dtos.CadastroEmpresaDto;
import com.samuelTI.pontointeligente.api.entities.Empresa;
import com.samuelTI.pontointeligente.api.responses.Response;
import com.samuelTI.pontointeligente.api.services.EmpresaService;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class CadastroEmpresaController {

	private static final Logger log = LoggerFactory.getLogger(CadastroEmpresaController.class);

	@Autowired
	private EmpresaService empresaService;

	public CadastroEmpresaController() {

	}

	/**
	 * Retorna uma empresa dado um CNPJ
	 * 
	 * @param cnpj
	 * @return ResponseEntity<Response<CadatroEmpresaDto>>
	 */

	@GetMapping(value = "/cnpj/{cnpj}")
	public ResponseEntity<Response<CadastroEmpresaDto>> buscarPorCnpj(@PathVariable("cnpj") String cnpj) {
		log.info("Buscando empresa por CNPJ: {}", cnpj);
		Response<CadastroEmpresaDto> response = new Response<CadastroEmpresaDto>();
		Optional<Empresa> empresa = empresaService.buscarPorCnpj(cnpj);

		if (!empresa.isPresent()) {
			log.info("Empresa não encontrada para o CNPJ" + cnpj);
			response.getErrors().add("Empresa não encontrada para o CNPJ " + cnpj);
			return ResponseEntity.badRequest().body(response);

		}

		response.setData(this.converteEmpresaDto(empresa.get()));
		return ResponseEntity.ok(response);
	}

	/**
	 * Popula um DTO com os dados de uma empresa.
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
