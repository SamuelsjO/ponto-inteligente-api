package com.samuelTI.smartpoint.api.controllers;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "*")
public class CadastroFuncionarioController {

	private static final Logger log = LoggerFactory.getLogger(CadastroFuncionarioController.class);

	@Autowired
	private FuncionarioService funcionarioService;

	public CadastroFuncionarioController() {

	}

	/**
	 * Updates an employee's data.
	 * 
	 * @param id
	 * @param funcionarioDto
	 * @param result
	 * @return ResponseEntity<Response<funcionarioDto>>
	 * @throws NoSuchAlgorithmExecption
	 */

	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<CadastroFuncionarioDto>> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody CadastroFuncionarioDto cadastroFuncionarioDto, BindingResult result)
			throws NoSuchAlgorithmException {
		log.info("Updgrading employee: {}", cadastroFuncionarioDto.toString());
		Response<CadastroFuncionarioDto> response = new Response<CadastroFuncionarioDto>();

		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(id);
		if (!funcionario.isPresent()) {
			result.addError(new ObjectError("funcionario", "Employee not found."));
		}

		this.atualizarDadosFunc(funcionario.get(), cadastroFuncionarioDto, result);

		if (result.hasErrors()) {
			log.error("Error validating employee: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		this.funcionarioService.persitirFunc(funcionario.get());
		response.setData(this.convertFuncionarioDto(funcionario.get()));

		return ResponseEntity.ok(response);
	}

	/**
	 * Updates employee data based on data found DTO
	 * 
	 * @param funcionario
	 * @param cadastroFuncionarioDto
	 * @param result
	 * @throws NoSuchAlgorithmException
	 */

	private void atualizarDadosFunc(Funcionario funcionario, CadastroFuncionarioDto cadastroFuncionarioDto,
			BindingResult result) throws NoSuchAlgorithmException {
		funcionario.setNome(cadastroFuncionarioDto.getNome());

		if (!funcionario.getEmail().equals(cadastroFuncionarioDto.getEmail())) {
			this.funcionarioService.buscarPorEmail(cadastroFuncionarioDto.getEmail())
					.ifPresent(func -> result.addError(new ObjectError("Email", "Existing Email.")));

			funcionario.setEmail(cadastroFuncionarioDto.getEmail());
		}

		funcionario.setQtdHorasAlmoco(null);
		cadastroFuncionarioDto.getQtdHorasAlmoco()
				.ifPresent(qtdHorasHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasHorasAlmoco)));
		
		funcionario.setQtdHorasTrabalhadas_dia(null);
		cadastroFuncionarioDto.getQtdHorasTrabalhadoDia().ifPresent(qtdHorasTrabDia -> funcionario.setQtdHorasTrabalhadas_dia(Float.valueOf(qtdHorasTrabDia)));
		
		funcionario.setValorHora(null);
		cadastroFuncionarioDto.getValorHora().ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));
		
		if(cadastroFuncionarioDto.getSenha().isPresent()) {
			funcionario.setSenha(PasswordUtils.gerarByCrypt(cadastroFuncionarioDto.getSenha().get()));
			
		}

	}
	
	/**
	 * Populate a DTO with the data of a employee.
	 * 
	 * @param funcionario
	 * @param CadastroFuncionarioDto
	 * 
	 */
	
	private CadastroFuncionarioDto convertFuncionarioDto(Funcionario funcionario) {
		CadastroFuncionarioDto cadastroFuncionarioDto = new CadastroFuncionarioDto();
		cadastroFuncionarioDto.setId(funcionario.getId());
		cadastroFuncionarioDto.setEmail(funcionario.getEmail());
		cadastroFuncionarioDto.setNome(funcionario.getNome());
		funcionario.getQtdHorasAlmocoOpt().ifPresent(
				qtdHorasAlmoco -> cadastroFuncionarioDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
		
		funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(
				qtdHorasTrabDia -> cadastroFuncionarioDto.setQtdHorasTrabalhadoDia(Optional.of(Float.toString(qtdHorasTrabDia))));
		
		funcionario.getValorHoraOpt().ifPresent(
				valorHora -> cadastroFuncionarioDto.setValorHora(Optional.of(valorHora.toString())));
		
		return cadastroFuncionarioDto;
		
	}
}
