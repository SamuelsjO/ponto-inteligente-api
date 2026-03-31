package com.samuelTI.smartpoint.api.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import jakarta.validation.Valid;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.samuelTI.smartpoint.api.dtos.CadastroLancamentoDto;
import com.samuelTI.smartpoint.api.dtos.PageResult;
import com.samuelTI.smartpoint.api.entities.Lancamento;
import com.samuelTI.smartpoint.api.enums.TipoEnum;
import com.samuelTI.smartpoint.api.responses.Response;
import com.samuelTI.smartpoint.api.services.FuncionarioService;
import com.samuelTI.smartpoint.api.services.LancamentoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final LancamentoService lancamentoService;
	private final FuncionarioService funcionarioService;

	@Value("${paginacao.qtd_por_pagina}")
	private int qtdPorPagina;

	@GetMapping(value = "/funcionario/{funcionarioId}")
	public ResponseEntity<Response<PageResult<CadastroLancamentoDto>>> listarPorFuncionarioId(
			@PathVariable("funcionarioId") Long funcionarioId,
			@RequestParam(value = "pag", defaultValue = "0") int pag) {
		log.info("Find Lancamento by employee ID: {}, page: {}", funcionarioId, pag);
		var response = new Response<PageResult<CadastroLancamentoDto>>();

		PageResult<CadastroLancamentoDto> lanPageDto = lancamentoService
				.buscarPorFuncionario(funcionarioId, pag, qtdPorPagina)
				.map(this::convertLancamentoDto);

		response.setData(lanPageDto);
		return ResponseEntity.ok(response);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<Response<CadastroLancamentoDto>> listarPorId(@PathVariable("id") Long id) {
		log.info("Find lancamento by ID: {}", id);
		var response = new Response<CadastroLancamentoDto>();
		Optional<Lancamento> lancamento = lancamentoService.buscarById(id);

		if (lancamento.isEmpty()) {
			log.info("Lancamento not found for the ID: {}", id);
			response.getErrors().add("Lancamento not found for the ID " + id);
			return ResponseEntity.ok(response);
		}

		response.setData(convertLancamentoDto(lancamento.get()));
		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<Response<CadastroLancamentoDto>> adicionar(
			@Valid @RequestBody CadastroLancamentoDto dto, BindingResult result) {
		log.info("Adicionando lancamento: {}", dto);
		var response = new Response<CadastroLancamentoDto>();
		validaFuncionario(dto, result);
		Lancamento lancamento = convertDtoByLancamento(dto, result);

		if (result.hasErrors()) {
			log.error("Error validating lancamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		lancamento = lancamentoService.persitir(lancamento);
		response.setData(convertLancamentoDto(lancamento));
		return ResponseEntity.ok(response);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<CadastroLancamentoDto>> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody CadastroLancamentoDto dto, BindingResult result) {
		log.info("Updating lancamento: {}", dto);
		var response = new Response<CadastroLancamentoDto>();
		validaFuncionario(dto, result);
		dto.setId(Optional.of(id));
		Lancamento lancamento = convertDtoByLancamento(dto, result);

		if (result.hasErrors()) {
			log.error("Error validating lancamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		lancamento = lancamentoService.persitir(lancamento);
		response.setData(convertLancamentoDto(lancamento));
		return ResponseEntity.ok(response);
	}

	@DeleteMapping(value = "/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<String>> remover(@PathVariable("id") Long id) {
		log.info("Removing lancamento: {}", id);
		var response = new Response<String>();
		Optional<Lancamento> lancamento = lancamentoService.buscarById(id);

		if (lancamento.isEmpty()) {
			log.info("Error removing because of lancamento ID: {} to be invalid.", id);
			response.getErrors().add("Error removing lancamento. Record not found for id " + id);
			return ResponseEntity.badRequest().body(response);
		}

		lancamentoService.remover(id);
		return ResponseEntity.ok(new Response<>());
	}

	private CadastroLancamentoDto convertLancamentoDto(Lancamento lancamento) {
		var dto = new CadastroLancamentoDto();
		dto.setId(Optional.of(lancamento.getId()));
		dto.setData(lancamento.getData().format(FORMATTER));
		dto.setTipo(lancamento.getTipo().toString());
		dto.setDescricao(lancamento.getDescricao());
		dto.setLocalizacao(lancamento.getLocalizacao());
		dto.setFuncionarioId(lancamento.getFuncionarioId());
		return dto;
	}

	private void validaFuncionario(CadastroLancamentoDto dto, BindingResult result) {
		if (dto.getFuncionarioId() == null) {
			result.addError(new ObjectError("Employee", "Employee not found"));
			return;
		}
		log.info("Validating employee id {}: ", dto.getFuncionarioId());
		funcionarioService.buscarPorId(dto.getFuncionarioId())
				.orElseGet(() -> {
					result.addError(new ObjectError("Employee", "Employee not found. ID nonexistent."));
					return null;
				});
	}

	private Lancamento convertDtoByLancamento(CadastroLancamentoDto dto, BindingResult result) {
		var lancamento = new Lancamento();

		if (dto.getId().isPresent()) {
			Optional<Lancamento> lanc = lancamentoService.buscarById(dto.getId().get());
			if (lanc.isPresent()) {
				lancamento = lanc.get();
			} else {
				result.addError(new ObjectError("lancamento", "Launch not found."));
			}
		} else {
			lancamento.setFuncionarioId(dto.getFuncionarioId());
		}

		lancamento.setDescricao(dto.getDescricao());
		lancamento.setLocalizacao(dto.getLocalizacao());
		lancamento.setData(LocalDateTime.parse(dto.getData(), FORMATTER));

		if (EnumUtils.isValidEnum(TipoEnum.class, dto.getTipo())) {
			lancamento.setTipo(TipoEnum.valueOf(dto.getTipo()));
		} else {
			result.addError(new ObjectError("Tipo", "Invalid type"));
		}
		return lancamento;
	}
}
