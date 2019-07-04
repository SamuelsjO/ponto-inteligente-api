package com.samuelTI.smartpoint.api.controllers;

import java.text.SimpleDateFormat;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.samuelTI.smartpoint.api.entities.Funcionario;
import com.samuelTI.smartpoint.api.entities.Lancamento;
import com.samuelTI.smartpoint.api.enums.TipoEnum;
import com.samuelTI.smartpoint.api.responses.Response;
import com.samuelTI.smartpoint.api.services.FuncionarioService;
import com.samuelTI.smartpoint.api.services.LancamentoService;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ParseException;

@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoController {

	private static final Logger log = LoggerFactory.getLogger(LancamentoController.class);
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private LancamentoService lancamentoService;

	@Autowired
	private FuncionarioService funcionarioService;

	@Value("${paginacao.qtd_por_pagina}")
	private int qtdPorPagina;

	public LancamentoController() {

	}

	/**
	 * Returns the list of releases of an official.
	 * 
	 * @param funcionarioId
	 * @param ResponseEntity<Response<CadastroLancamentoDto>>
	 */

	@GetMapping(value = "/funcionario/{funcionarioId}")
	public ResponseEntity<Response<Page<CadastroLancamentoDto>>> listarPorFuncionarioId(
			@PathVariable("funcionarioId") Long funcionarioId, 
			@RequestParam(value = "pag", defaultValue = "0") int pag,
			@RequestParam(value = "ord", defaultValue = "id") String ord,
			@RequestParam(value = "dir", defaultValue = "DESC") String dir) {
		log.info("Find Lancamento bi ID the employee: {}, page: {}", funcionarioId, pag);
		Response<Page<CadastroLancamentoDto>> response = new Response<Page<CadastroLancamentoDto>>();

		@SuppressWarnings("deprecation")
		PageRequest pageRequest = new PageRequest(pag, this.qtdPorPagina, Direction.valueOf(dir), ord);
		Page<Lancamento> lancaPage = this.lancamentoService.buscarPorFuncionario(funcionarioId, pageRequest);
		Page<CadastroLancamentoDto> lanPageDto = lancaPage.map(lancamento -> this.convertLancamentoDto(lancamento));

		response.setData(lanPageDto);
		return ResponseEntity.ok(response);
	}

	/**
	 * Returns a call by ID.
	 * 
	 * @param id
	 * @param ResponseEntity<Response<CadastroLancamentoDto>>
	 */

	@GetMapping(value = "/{id}")
	public ResponseEntity<Response<CadastroLancamentoDto>> listarPorId(@PathVariable("id") Long id) {
		log.info("Find lancamento by ID: {}", id);
		Response<CadastroLancamentoDto> response = new Response<CadastroLancamentoDto>();
		Optional<Lancamento> lancamento = this.lancamentoService.buscarById(id);

		if (!lancamento.isPresent()) {
			log.info("Lancamento not found for the ID: {}", id);
			response.getErrors().add("Lancamento not found for the ID " + id);
			return ResponseEntity.ok(response);
		}

		response.setData(this.convertLancamentoDto(lancamento.get()));
		return ResponseEntity.ok(response);
	}

	/**
	 * Adds a new launch.
	 * 
	 * @param lancamento
	 * @param result
	 * @return ResponseEntity<Response<LancamentoDto>>
	 * @throws java.text.ParseException 
	 * @throws ParseException 
	 */
	
	@PostMapping
	public ResponseEntity<Response<CadastroLancamentoDto>> adicionar(@Valid @RequestBody CadastroLancamentoDto cadastroLancamentoDto,
			BindingResult result) throws ParseException, java.text.ParseException{
		log.info("Adicionando lancamento: {}", cadastroLancamentoDto.toString());
		Response<CadastroLancamentoDto> response = new Response<CadastroLancamentoDto>();
		validaFuncionario(cadastroLancamentoDto, result);
		Lancamento lancamento = this.convertDtoByLancamento(cadastroLancamentoDto, result);
		
		if(result.hasErrors()){
			log.error("Error validating lancamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
			
		}
		lancamento = this.lancamentoService.persitir(lancamento);
		response.setData(this.convertLancamentoDto(lancamento));
		return ResponseEntity.ok(response);
		
	}
	/**
	 * Updates the data for a launch.
	 * 
	 * @param id
	 * @param lancamentoDto
	 * @return ResponseEntity<Response<Lancamento>>
	 * @throws ParseException 
	 */

	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<CadastroLancamentoDto>> atualizar(@PathVariable("id") Long id, 
			@Valid @RequestBody CadastroLancamentoDto cadastroLancamentoDto, BindingResult result)
			throws ParseException, java.text.ParseException {
		
		log.info("Updating lançamento: {}", cadastroLancamentoDto.toString());
		Response<CadastroLancamentoDto> response = new Response<CadastroLancamentoDto>();
		validaFuncionario(cadastroLancamentoDto, result);
		cadastroLancamentoDto.setId(Optional.of(id));
		Lancamento lancamento = this.convertDtoByLancamento(cadastroLancamentoDto, result);
		
		if(result.hasErrors()) {
			log.error("Error validating lançamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		lancamento = this.lancamentoService.persitir(lancamento);
		response.setData(this.convertLancamentoDto(lancamento));
		return ResponseEntity.ok(response);

	}
	
	/**
	 * Remove the data for a launch.
	 * 
	 * @param id
	 * @return ResponseEntity<Response<Lancamento>>
	 */
	
	@DeleteMapping(value = "/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<String>> remover(@PathVariable("id") Long id){
		log.info("Removing lancamento: {}", id);
		Response<String> response = new Response<String>();
		Optional<Lancamento> lancamento = this.lancamentoService.buscarById(id);
		
		if(!lancamento.isPresent()) {
			log.info("Error removing because of lancamento ID: {} to be invalid.", id);
			response.getErrors().add("Error removing lancamento. Record not found for id" + id);
			return ResponseEntity.badRequest().body(response);
		}
		
		this.lancamentoService.remover(id);
		return ResponseEntity.ok(new Response<String>());
	}

	/**
	 * Converts an entity release to its respective DTO.
	 * 
	 * @param lancamento
	 * @param CadastraLancamentoDto
	 * 
	 */

	private CadastroLancamentoDto convertLancamentoDto(Lancamento lancamento) {
		
		CadastroLancamentoDto cadastroLancamentoDto = new CadastroLancamentoDto();
		cadastroLancamentoDto.setId(Optional.of(lancamento.getId()));
		cadastroLancamentoDto.setData(this.dateFormat.format(lancamento.getData()));
		cadastroLancamentoDto.setTipo(lancamento.getTipo().toString());
		cadastroLancamentoDto.setDescricao(lancamento.getDescricao());
		cadastroLancamentoDto.setLocalizacao(lancamento.getLocalizacao());
		cadastroLancamentoDto.setFuncionarioId(lancamento.getFuncionario().getId());
		

		return cadastroLancamentoDto;
	}

	/**
	 * Validate an official, verifying that it is existing and valid in the system.
	 * 
	 * @param result
	 * @param CadastraLancamentoDto
	 * 
	 */

	private void validaFuncionario(CadastroLancamentoDto cadastroLancamentoDto, BindingResult result) {
		if (cadastroLancamentoDto.getFuncionarioId() == null) {
			result.addError(new ObjectError("Employee", "Employee not found"));
			return;
		}

		log.info("Validing employee id {}: ", cadastroLancamentoDto.getFuncionarioId());
		Optional<Funcionario> funcionario = this.funcionarioService
				.buscarPorId(cadastroLancamentoDto.getFuncionarioId());
		if (!funcionario.isPresent()) {
			result.addError(new ObjectError("Employee", "Employee not found. ID nonexistent."));
		}
	}
	
	/**
	 * Converts one CadastroLancamentoDto to an entity Launch
	 * 
	 * @param cadastroLancamentoDto
	 * @param result
	 * @param Lancamento
	 * @param ParseExecption
	 * @throws java.text.ParseException 
	 * 
	 */
	
	private Lancamento convertDtoByLancamento(CadastroLancamentoDto cadastroLancamentoDto, BindingResult result) throws ParseException, java.text.ParseException{
		Lancamento lancamento = new Lancamento();
		
		if(cadastroLancamentoDto.getId().isPresent()) {
			Optional<Lancamento> lanc = this.lancamentoService.buscarById(cadastroLancamentoDto.getId().get());
			if(lanc.isPresent()) {
				lancamento = lanc.get();
			}else {
				result.addError(new ObjectError("lancamento", "Launch not found."));
			}
			
		}else {
			lancamento.setFuncionario(new Funcionario());
			lancamento.getFuncionario().setId(cadastroLancamentoDto.getFuncionarioId());
		}
		
		lancamento.setDescricao(cadastroLancamentoDto.getDescricao());
		lancamento.setLocalizacao(cadastroLancamentoDto.getLocalizacao());
		lancamento.setData(this.dateFormat.parse(cadastroLancamentoDto.getData()));
		
		if(EnumUtils.isValidEnum(TipoEnum.class, cadastroLancamentoDto.getTipo())) {
			lancamento.setTipo(TipoEnum.valueOf(cadastroLancamentoDto.getTipo()));
			
		}else {
			result.addError(new ObjectError("Tipo" , "Invalid type"));
		}
		return lancamento;
	}
}
