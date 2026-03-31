package com.samuelTI.smartpoint.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.samuelTI.smartpoint.api.entities.Empresa;
import com.samuelTI.smartpoint.api.entities.Funcionario;
import com.samuelTI.smartpoint.api.entities.Lancamento;
import com.samuelTI.smartpoint.api.enums.PerfilEnum;
import com.samuelTI.smartpoint.api.enums.TipoEnum;
import com.samuelTI.smartpoint.api.utils.PasswordUtils;

@SpringBootTest
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private FuncionarioRepository funcionarioRepository;

	@Autowired
	private EmpresaRepository empresaRepository;

	private Long funcionarioId;

	@BeforeEach
	public void setUp() {
		Empresa empresa = this.empresaRepository.save(obterDadosEmpresa());
		Funcionario funcionario = this.funcionarioRepository.save(obterDadosFuncionario(empresa.getId()));
		this.funcionarioId = funcionario.getId();

		this.lancamentoRepository.save(obterDadosLancamentos(funcionarioId));
		this.lancamentoRepository.save(obterDadosLancamentos(funcionarioId));
	}

	@AfterEach
	public void tearDown() {
		this.lancamentoRepository.deleteAll();
		this.funcionarioRepository.deleteAll();
		this.empresaRepository.deleteAll();
	}

	@Test
	public void testBuscarLancamentosPorFuncionarioId() {
		List<Lancamento> lancamentos = this.lancamentoRepository.findByFuncionarioId(funcionarioId);
		assertEquals(2, lancamentos.size());
	}

	private Lancamento obterDadosLancamentos(Long funcionarioId) {
		Lancamento lancamento = new Lancamento();
		lancamento.setData(LocalDateTime.now());
		lancamento.setTipo(TipoEnum.START_LUNCH);
		lancamento.setFuncionarioId(funcionarioId);
		lancamento.setDataCriacao(LocalDateTime.now());
		lancamento.setDataAtualizacao(LocalDateTime.now());
		return lancamento;
	}

	private Funcionario obterDadosFuncionario(Long empresaId) {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome("Samuel");
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		funcionario.setSenha(PasswordUtils.gerarByCrypt("12345"));
		funcionario.setCpf("10636132641");
		funcionario.setEmail("samucagm@rockemail.com");
		funcionario.setEmpresaId(empresaId);
		funcionario.setDataCriacao(LocalDateTime.now());
		funcionario.setDataAtualizacao(LocalDateTime.now());
		return funcionario;
	}

	private Empresa obterDadosEmpresa() {
		Empresa empresa = new Empresa();
		empresa.setRazaoSocial("SFTecnologia");
		empresa.setCnpj("514636450000100");
		empresa.setDataCriacao(LocalDateTime.now());
		empresa.setDataAtualizacao(LocalDateTime.now());
		return empresa;
	}
}
