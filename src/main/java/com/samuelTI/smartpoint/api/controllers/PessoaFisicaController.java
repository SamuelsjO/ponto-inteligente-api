package com.samuelTI.smartpoint.api.controllers;

import java.math.BigDecimal;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samuelTI.smartpoint.api.dtos.CadastroPessoaFisicaDto;
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
@RequestMapping("/api/cadastra-pf")
public class PessoaFisicaController {

	private final EmpresaService empresaService;
	private final FuncionarioService funcionarioService;

	@PostMapping
	public ResponseEntity<Response<CadastroPessoaFisicaDto>> cadastrar(
			@Valid @RequestBody CadastroPessoaFisicaDto dto, BindingResult result) {
		log.info("PF registering: {}", dto);
		var response = new Response<CadastroPessoaFisicaDto>();

		validarDadosExistentes(dto, result);
		Funcionario funcionario = convertDtoByFuncionario(dto);

		if (result.hasErrors()) {
			log.info("Error validating registration data of PF: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		empresaService.buscarPorCnpj(dto.getCnpj())
				.ifPresent(emp -> funcionario.setEmpresaId(emp.getId()));
		funcionarioService.persitirFunc(funcionario);

		response.setData(convertCadastroPFDto(funcionario, dto.getCnpj()));
		return ResponseEntity.ok(response);
	}

	private void validarDadosExistentes(CadastroPessoaFisicaDto dto, BindingResult result) {
		if (empresaService.buscarPorCnpj(dto.getCnpj()).isEmpty()) {
			result.addError(new ObjectError("empresa", "Company not registered"));
		}
		funcionarioService.buscarPorCpf(dto.getCpf())
				.ifPresent(func -> result.addError(new ObjectError("funcionario", "Existing CPF")));
		funcionarioService.buscarPorEmail(dto.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("funcionario", "Existing Email.")));
	}

	private CadastroPessoaFisicaDto convertCadastroPFDto(Funcionario funcionario, String cnpj) {
		var dto = new CadastroPessoaFisicaDto();
		dto.setId(funcionario.getId());
		dto.setNome(funcionario.getNome());
		dto.setEmail(funcionario.getEmail());
		dto.setCpf(funcionario.getCpf());
		dto.setCnpj(cnpj);
		funcionario.getQtdHorasAlmocoOpt().ifPresent(v -> dto.setQtdHorasAlmoco(Optional.of(Float.toString(v))));
		funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(v -> dto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(v))));
		funcionario.getValorHoraOpt().ifPresent(v -> dto.setValorHora(Optional.of(v.toString())));
		return dto;
	}

	private Funcionario convertDtoByFuncionario(CadastroPessoaFisicaDto dto) {
		var funcionario = new Funcionario();
		funcionario.setNome(dto.getNome());
		funcionario.setEmail(dto.getEmail());
		funcionario.setCpf(dto.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		funcionario.setSenha(PasswordUtils.gerarByCrypt(dto.getSenha()));
		dto.getQtdHorasAlmoco().ifPresent(v -> funcionario.setQtdHorasAlmoco(Float.valueOf(v)));
		dto.getQtdHorasTrabalhoDia().ifPresent(v -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(v)));
		dto.getValorHora().ifPresent(v -> funcionario.setValorHora(new BigDecimal(v)));
		return funcionario;
	}
}
