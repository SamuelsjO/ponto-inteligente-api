
package com.samuelTI.pontointeligente.api.controles;

import java.math.BigDecimal;
import java.util.Optional;

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

import com.samuelTI.pontointeligente.api.dtos.CadastroPessoaFisicaDto;
import com.samuelTI.pontointeligente.api.entities.Empresa;
import com.samuelTI.pontointeligente.api.entities.Funcionario;
import com.samuelTI.pontointeligente.api.enums.PerfilEnum;
import com.samuelTI.pontointeligente.api.responses.Response;
import com.samuelTI.pontointeligente.api.services.EmpresaService;
import com.samuelTI.pontointeligente.api.services.FuncionarioService;
import com.samuelTI.pontointeligente.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/cadastra-pf")
@CrossOrigin(origins = "*")
public class CadastroPessoaFisicaController {

	private static final Logger log = LoggerFactory.getLogger(CadastroPessoaFisicaController.class);

	@Autowired
	private EmpresaService empresaService;

	@Autowired
	private FuncionarioService funcionarioService;

	public CadastroPessoaFisicaController() {

	}
	/**
	 * Cadastrar um funcionario pessoa fisica no sistema
	 * 
	 * @param cadastroPFDto
	 * @param result 
	 * @param ResponseEntity<Response<CadastroPFDto>>
	 * @throws NoSuchAlgorithmException
	 * 
	 */

	@PostMapping
	public ResponseEntity<Response<CadastroPessoaFisicaDto>> cadastrar(@Valid @RequestBody CadastroPessoaFisicaDto cadastroPFDto,
			BindingResult result) throws NoSuchMethodException {
		log.info("Cadastrando PF: {}", cadastroPFDto.toString());
		Response<CadastroPessoaFisicaDto> response = new Response<CadastroPessoaFisicaDto>();

		validarDadosExistentes(cadastroPFDto, result);
		Funcionario funcionario = this.convertDtoByFuncionario(cadastroPFDto, result);

		if (result.hasErrors()) {
			log.info("Erro validando dados de Cadastro de PF: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPFDto.getCnpj());
		empresa.ifPresent(emp -> funcionario.setEmpresa(emp));
		this.funcionarioService.persitirFunc(funcionario);

		response.setData(this.convertCadastroPFDto(funcionario));
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Verifica se a empresa está cadastrada e se o funcionario não existe na base
	 * de dados
	 * 
	 * @param cadastroPFDto
	 * @param result
	 */
	private void validarDadosExistentes(CadastroPessoaFisicaDto cadastroPFDto, BindingResult result) {
		Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPFDto.getCnpj());
		if (!empresa.isPresent()) {
			result.addError(new ObjectError("empresa", "Empresa não cadastrada"));
		}

		this.funcionarioService.buscarPorCpf(cadastroPFDto.getCpf())
				.ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente")));

		this.funcionarioService.buscarPorEmail(cadastroPFDto.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente.")));

	}
	
	/**
	 * Popula o DTO de cadastro com os dados do funcionario e empresa.
	 *  
	 * @param funcionario 
	 * @return CadastroPFDto
	 */

	private CadastroPessoaFisicaDto convertCadastroPFDto(Funcionario funcionario) {

		CadastroPessoaFisicaDto cadastroPFDto = new CadastroPessoaFisicaDto();
		cadastroPFDto.setId(funcionario.getId());
		cadastroPFDto.setNome(funcionario.getNome());
		cadastroPFDto.setEmail(funcionario.getEmail());
		cadastroPFDto.setCpf(funcionario.getCpf());
		cadastroPFDto.setCnpj(funcionario.getCpf());
		
		funcionario.getQtdHorasAlmocoOpt().ifPresent(
				qtdHorasAlmoco -> cadastroPFDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
		
		funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(
				qtdHorasTrabDia -> cadastroPFDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabDia))));

		funcionario.getValorHoraOpt()
				.ifPresent(valorHora -> cadastroPFDto.setValorHora(Optional.of(valorHora.toString())));

		return cadastroPFDto;
	}

	/**
	 * Converte os dados do DTO para funcionario.
	 * 
	 * @param cadastroPFDto
	 * @param result 
	 * @param Funcionario 
	 * @throws NoSuchAlgorithmException
	 */
	private Funcionario convertDtoByFuncionario(@Valid CadastroPessoaFisicaDto cadastroPFDto, BindingResult result) {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(cadastroPFDto.getNome());
		funcionario.setEmail(cadastroPFDto.getEmail());
		funcionario.setCpf(cadastroPFDto.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		funcionario.setSenha(PasswordUtils.gerarByCrypt(cadastroPFDto.getSenha()));
		cadastroPFDto.getQtdHorasAlmoco()
				.ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));
		cadastroPFDto.getQtdHorasTrabalhoDia()
				.ifPresent(qtdHorasTrabDia -> funcionario.setQtdHorasTrabalhadas_dia(Float.valueOf(qtdHorasTrabDia)));
		cadastroPFDto.getValorHora().ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));

		return funcionario;
	}

}
