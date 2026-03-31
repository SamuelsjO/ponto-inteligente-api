package com.samuelTI.smartpoint.api.controllers;

import java.math.BigDecimal;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samuelTI.smartpoint.api.dtos.CadastroFuncionarioDto;
import com.samuelTI.smartpoint.api.entities.Funcionario;
import com.samuelTI.smartpoint.api.responses.Response;
import com.samuelTI.smartpoint.api.services.FuncionarioService;
import com.samuelTI.smartpoint.api.utils.PasswordUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

	private final FuncionarioService funcionarioService;

	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<CadastroFuncionarioDto>> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody CadastroFuncionarioDto dto, BindingResult result) {
		log.info("Upgrading employee: {}", dto);
		var response = new Response<CadastroFuncionarioDto>();

		Optional<Funcionario> funcionario = funcionarioService.buscarPorId(id);
		if (funcionario.isEmpty()) {
			result.addError(new ObjectError("funcionario", "Employee not found."));
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		atualizarDadosFunc(funcionario.get(), dto, result);

		if (result.hasErrors()) {
			log.error("Error validating employee: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		funcionarioService.persitirFunc(funcionario.get());
		response.setData(convertFuncionarioDto(funcionario.get()));
		return ResponseEntity.ok(response);
	}

	private void atualizarDadosFunc(Funcionario funcionario, CadastroFuncionarioDto dto, BindingResult result) {
		funcionario.setNome(dto.getNome());

		if (!funcionario.getEmail().equals(dto.getEmail())) {
			funcionarioService.buscarPorEmail(dto.getEmail())
					.ifPresent(func -> result.addError(new ObjectError("Email", "Existing Email.")));
			funcionario.setEmail(dto.getEmail());
		}

		funcionario.setQtdHorasAlmoco(null);
		dto.getQtdHorasAlmoco().ifPresent(v -> funcionario.setQtdHorasAlmoco(Float.valueOf(v)));

		funcionario.setQtdHorasTrabalhoDia(null);
		dto.getQtdHorasTrabalhadoDia().ifPresent(v -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(v)));

		funcionario.setValorHora(null);
		dto.getValorHora().ifPresent(v -> funcionario.setValorHora(new BigDecimal(v)));

		dto.getSenha().ifPresent(s -> funcionario.setSenha(PasswordUtils.gerarByCrypt(s)));
	}

	private CadastroFuncionarioDto convertFuncionarioDto(Funcionario funcionario) {
		var dto = new CadastroFuncionarioDto();
		dto.setId(funcionario.getId());
		dto.setEmail(funcionario.getEmail());
		dto.setNome(funcionario.getNome());
		funcionario.getQtdHorasAlmocoOpt().ifPresent(v -> dto.setQtdHorasAlmoco(Optional.of(Float.toString(v))));
		funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(v -> dto.setQtdHorasTrabalhadoDia(Optional.of(Float.toString(v))));
		funcionario.getValorHoraOpt().ifPresent(v -> dto.setValorHora(Optional.of(v.toString())));
		return dto;
	}
}
