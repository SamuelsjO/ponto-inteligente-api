package com.samuelTI.smartpoint.api.controllers;

import java.security.NoSuchAlgorithmException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@RestController
@RequestMapping(path = "/api/cadastra-pj")
@CrossOrigin(origins = "*")
public class PessoaJuridicaController {

	private static final Logger log = LoggerFactory.getLogger(PessoaJuridicaController.class);

	@Autowired
	private FuncionarioService funcionarioService;

	@Autowired
	private EmpresaService empresaService;

	public PessoaJuridicaController() {

	}

	/**
	 * Register a new legal person in the system
     *
	 * @param cadastroPJDto
	 * @param result 
	 * @return ResponseEntity<Response<CadastroPJDto>>
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping
	public ResponseEntity<Response<CadastroPessoaJuridicaDto>> cadastrar(@Valid @RequestBody CadastroPessoaJuridicaDto cadastroPJDto,
			BindingResult result) throws NoSuchAlgorithmException {
		log.info("PJ registering: {}", cadastroPJDto.toString());
		Response<CadastroPessoaJuridicaDto> response = new Response<CadastroPessoaJuridicaDto>();

		validaDadosExistentes(cadastroPJDto, result);
		Empresa empresa = this.convertDtoByEmpresa(cadastroPJDto);
		Funcionario funcionario = this.convertDtoByFuncionario(cadastroPJDto, result);
		
		if(result.hasErrors()) {
			log.error("Error validating registration data of PJ: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		this.empresaService.persitEmpresa(empresa);
		funcionario.setEmpresa(empresa);
		this.funcionarioService.persitirFunc(funcionario);
		
		response.setData(this.convertCadastroPJDto(funcionario));
		return ResponseEntity.ok(response);
	}

	/**
	 * Checks if the company or official already exists in the database.
	 * 
	 * @param cadastroPJDto
	 * @param result
	 * 
	 */

	private void validaDadosExistentes(CadastroPessoaJuridicaDto cadastroPJDto, BindingResult result) {

		this.empresaService.buscarPorCnpj(cadastroPJDto.getCnpj())
				.ifPresent(emp -> result.addError(new ObjectError("empresa", "Existing Company")));
		this.funcionarioService.buscarPorCpf(cadastroPJDto.getCpf())
				.ifPresent(func -> result.addError(new ObjectError("Funcionario", "Existing CPF")));
		this.funcionarioService.buscarPorEmail(cadastroPJDto.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("Funcionario", "Existing Email")));

	}

	/**
	 * Converts DTO data to enterprise.
	 * 
	 * @param cadastroPJDto
	 * @param result
	 * 
	 */
	private Empresa convertDtoByEmpresa(CadastroPessoaJuridicaDto cadastroPJDto) {
		Empresa empresa = new Empresa();
		empresa.setCnpj(cadastroPJDto.getCnpj());
		empresa.setRazaoSocial(cadastroPJDto.getRazaoSocial());

		return empresa;
	}

	/**
	 * Converts DTO data to official
	 * 
	 * @param cadastroPJDto 
	 * @param result 
	 * @param Funcionario
	 * @param NoSuchAlgorithmExecption
	 * @return funcionario
	 * 
	 */
	private Funcionario convertDtoByFuncionario(CadastroPessoaJuridicaDto cadastroPJDto, BindingResult result)
			throws NoSuchAlgorithmException {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(cadastroPJDto.getNome());
		funcionario.setEmail(cadastroPJDto.getEmail());
		funcionario.setCpf(cadastroPJDto.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
		funcionario.setSenha(PasswordUtils.gerarByCrypt(cadastroPJDto.getSenha()));

		return funcionario;
	}

	/**
	 * Populate the DTO to register with the data of the official and company
	 * 
	 * @param funcionario 
	 * @return cadastroPJDto
	 * 
	 */
	private CadastroPessoaJuridicaDto convertCadastroPJDto(Funcionario funcionario) {
		CadastroPessoaJuridicaDto cadastroPJDto = new CadastroPessoaJuridicaDto();
		cadastroPJDto.setId(funcionario.getId());
		cadastroPJDto.setNome(funcionario.getNome());
		cadastroPJDto.setEmail(funcionario.getEmail());
		cadastroPJDto.setCpf(funcionario.getCpf());
		cadastroPJDto.setRazaoSocial(funcionario.getEmpresa().getRazaoSocial());
		cadastroPJDto.setCnpj(funcionario.getEmpresa().getCnpj());

		return cadastroPJDto;
	}

}
