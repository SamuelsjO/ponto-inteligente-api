package com.samuelTI.pontointeligente.api.controles;

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

import com.samuelTI.pontointeligente.api.dtos.CadastroPJDto;
import com.samuelTI.pontointeligente.api.entities.Empresa;
import com.samuelTI.pontointeligente.api.entities.Funcionario;
import com.samuelTI.pontointeligente.api.enums.PerfilEnum;
import com.samuelTI.pontointeligente.api.responses.Response;
import com.samuelTI.pontointeligente.api.services.EmpresaService;
import com.samuelTI.pontointeligente.api.services.FuncionarioService;
import com.samuelTI.pontointeligente.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/cadastrapj")
@CrossOrigin(origins = "*")
public class CadastroPJController {

	private static final Logger log = LoggerFactory.getLogger(CadastroPJController.class);

	@Autowired
	private FuncionarioService funcionarioService;

	@Autowired
	private EmpresaService empresaService;

	public CadastroPJController() {

	}

	/*
	 * Cadastra uma nova pessoa juridica no sistema
	 * 
	 * @param cadastroPJDto
	 * 
	 * @param result
	 * 
	 * @return ResponseEntity<Response<CadastroPJDto>>
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping
	public ResponseEntity<Response<CadastroPJDto>> cadastrar(@Valid @RequestBody CadastroPJDto cadastroPJDto,
			BindingResult result) throws NoSuchAlgorithmException {
		log.info("Cadastrando PJ: {}", cadastroPJDto.toString());
		Response<CadastroPJDto> response = new Response<CadastroPJDto>();

		validaDadosExistentes(cadastroPJDto, result);
		Empresa empresa = this.convertDtoByEmpresa(cadastroPJDto);
		Funcionario funcionario = this.convertDtoByFuncionario(cadastroPJDto, result);
		
		if(result.hasErrors()) {
			log.error("Erro validadando dados de cadastro PJ: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		this.empresaService.persitEmpresa(empresa);
		funcionario.setEmpresa(empresa);
		this.funcionarioService.persitirFunc(funcionario);
		
		response.setData(this.convertCadastroPJDto(funcionario));
		return ResponseEntity.ok(response);
	}

	/*
	 * Verifica se a a empresa ou funcionario ja existe na base de dados.
	 * 
	 * @param cadastroPJDto
	 * 
	 * @param result
	 * 
	 */

	private void validaDadosExistentes(CadastroPJDto cadastroPJDto, BindingResult result) {

		this.empresaService.buscarPorCnpj(cadastroPJDto.getCnpj())
				.ifPresent(emp -> result.addError(new ObjectError("empresa", "Empresa já exisente")));
		this.funcionarioService.buscarPorCpf(cadastroPJDto.getCpf())
				.ifPresent(func -> result.addError(new ObjectError("Funcionario", "CPF ja existente")));
		this.funcionarioService.buscarPorEmail(cadastroPJDto.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("Funcionario", "Email já existnte")));

	}

	/*
	 * Converte os dados do DTO para empresa..
	 * 
	 * @param cadastroPJDto
	 * 
	 * @param result
	 * 
	 */
	private Empresa convertDtoByEmpresa(CadastroPJDto cadastroPJDto) {
		Empresa empresa = new Empresa();
		empresa.setCnpj(cadastroPJDto.getCnpj());
		empresa.setRazaoSocial(cadastroPJDto.getRazaoSocial());

		return empresa;
	}

	/*
	 * Converte os dados do DTO para funcionario
	 * 
	 * @param cadastroPJDto
	 * 
	 * @param result
	 * 
	 * @param Funcionario
	 * 
	 * @param NoSuchAlgorithmExecption
	 * 
	 * @return funcionario
	 * 
	 */
	private Funcionario convertDtoByFuncionario(CadastroPJDto cadastroPJDto, BindingResult result)
			throws NoSuchAlgorithmException {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(funcionario.getNome());
		funcionario.setEmail(funcionario.getEmail());
		funcionario.setCpf(funcionario.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
		funcionario.setSenha(PasswordUtils.gerarByCrypt(funcionario.getSenha()));

		return funcionario;
	}

	/*
	 * Popula o DTO de cadastro com os dados do funcionario e empresa
	 * 
	 * @param funcionario
	 * 
	 * @return cadastroPJDto
	 * 
	 */
	private CadastroPJDto convertCadastroPJDto(Funcionario funcionario) {
		CadastroPJDto cadastroPJDto = new CadastroPJDto();
		cadastroPJDto.setId(funcionario.getId());
		cadastroPJDto.setNome(funcionario.getNome());
		cadastroPJDto.setEmail(funcionario.getEmail());
		cadastroPJDto.setCpf(funcionario.getCpf());
		cadastroPJDto.setRazaoSocial(funcionario.getEmpresa().getRazaoSocial());
		cadastroPJDto.setCnpj(funcionario.getEmpresa().getCnpj());

		return cadastroPJDto;
	}

}
