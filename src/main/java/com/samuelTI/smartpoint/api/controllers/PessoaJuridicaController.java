package com.samuelTI.smartpoint.api.controllers;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samuelTI.smartpoint.api.dtos.CadastroPessoaJuridicaDto;
import com.samuelTI.smartpoint.api.entities.Empresa;
import com.samuelTI.smartpoint.api.entities.Funcionario;
import com.samuelTI.smartpoint.api.enums.PerfilEnum;
import com.samuelTI.smartpoint.api.responses.Response;
import com.samuelTI.smartpoint.api.services.EmpresaService;
import com.samuelTI.smartpoint.api.services.FuncionarioService;
import com.samuelTI.smartpoint.api.utils.PasswordUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/cadastra-pj")
public class PessoaJuridicaController {

	private final FuncionarioService funcionarioService;
	private final EmpresaService empresaService;

	@PostMapping
	public ResponseEntity<Response<CadastroPessoaJuridicaDto>> cadastrar(
			@Valid @RequestBody CadastroPessoaJuridicaDto dto, BindingResult result) {
		log.info("PJ registering: {}", dto);
		var response = new Response<CadastroPessoaJuridicaDto>();

		validaDadosExistentes(dto, result);
		var empresa = convertDtoByEmpresa(dto);
		var funcionario = convertDtoByFuncionario(dto);

		if (result.hasErrors()) {
			log.error("Error validating registration data of PJ: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		empresaService.persitEmpresa(empresa);
		funcionario.setEmpresaId(empresa.getId());
		funcionarioService.persitirFunc(funcionario);

		response.setData(convertCadastroPJDto(funcionario, empresa));
		return ResponseEntity.ok(response);
	}

	private void validaDadosExistentes(CadastroPessoaJuridicaDto dto, BindingResult result) {
		empresaService.buscarPorCnpj(dto.getCnpj())
				.ifPresent(emp -> result.addError(new ObjectError("empresa", "Existing Company")));
		funcionarioService.buscarPorCpf(dto.getCpf())
				.ifPresent(func -> result.addError(new ObjectError("Funcionario", "Existing CPF")));
		funcionarioService.buscarPorEmail(dto.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("Funcionario", "Existing Email")));
	}

	private Empresa convertDtoByEmpresa(CadastroPessoaJuridicaDto dto) {
		var empresa = new Empresa();
		empresa.setCnpj(dto.getCnpj());
		empresa.setRazaoSocial(dto.getRazaoSocial());
		return empresa;
	}

	private Funcionario convertDtoByFuncionario(CadastroPessoaJuridicaDto dto) {
		var funcionario = new Funcionario();
		funcionario.setNome(dto.getNome());
		funcionario.setEmail(dto.getEmail());
		funcionario.setCpf(dto.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
		funcionario.setSenha(PasswordUtils.gerarByCrypt(dto.getSenha()));
		return funcionario;
	}

	private CadastroPessoaJuridicaDto convertCadastroPJDto(Funcionario funcionario, Empresa empresa) {
		var dto = new CadastroPessoaJuridicaDto();
		dto.setId(funcionario.getId());
		dto.setNome(funcionario.getNome());
		dto.setEmail(funcionario.getEmail());
		dto.setCpf(funcionario.getCpf());
		dto.setRazaoSocial(empresa.getRazaoSocial());
		dto.setCnpj(empresa.getCnpj());
		return dto;
	}
}
