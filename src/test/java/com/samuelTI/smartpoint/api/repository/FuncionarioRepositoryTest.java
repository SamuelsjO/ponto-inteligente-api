package com.samuelTI.smartpoint.api.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.samuelTI.smartpoint.api.entities.Empresa;
import com.samuelTI.smartpoint.api.entities.Funcionario;
import com.samuelTI.smartpoint.api.enums.PerfilEnum;
import com.samuelTI.smartpoint.api.utils.PasswordUtils;

@SpringBootTest
@ActiveProfiles("test")
public class FuncionarioRepositoryTest {

	@Autowired
	private FuncionarioRepository funcionarioRepository;

	@Autowired
	private EmpresaRepository empresaRepository;

	private static final String EMAIL = "samucagm@rocketmail.com";
	private static final String CPF = "1063621641";

	@BeforeEach
	public void setUp() {
		Empresa empresa = this.empresaRepository.save(obterDadosEmpresa());
		this.funcionarioRepository.save(obterDadosFuncionario(empresa.getId()));
	}

	@AfterEach
	public void tearDown() {
		this.funcionarioRepository.deleteAll();
		this.empresaRepository.deleteAll();
	}

	@Test
	public void testBuscarFuncionarioPorEmail() {
		Optional<Funcionario> funcionario = this.funcionarioRepository.findByEmail(EMAIL);
		assertTrue(funcionario.isPresent());
	}

	@Test
	public void testBuscarFuncionarioByCpf() {
		Optional<Funcionario> funcionario = this.funcionarioRepository.findByCpf(CPF);
		assertTrue(funcionario.isPresent());
	}

	@Test
	public void testBuscarFuncionarioByCpfEmail() {
		Optional<Funcionario> funcionario = this.funcionarioRepository.findByCpfOrEmail(CPF, EMAIL);
		assertTrue(funcionario.isPresent());
	}

	@Test
	public void testBuscarFuncionarioByEmailInvalid() {
		Optional<Funcionario> funcionario = this.funcionarioRepository.findByCpfOrEmail(CPF, "email@invalido.com");
		assertTrue(funcionario.isPresent());
	}

	@Test
	public void testBuscarFuncionarioByCpfInvalid() {
		Optional<Funcionario> funcionario = this.funcionarioRepository.findByCpfOrEmail("1234567878", EMAIL);
		assertTrue(funcionario.isPresent());
	}

	private Funcionario obterDadosFuncionario(Long empresaId) {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome("Samuel");
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		funcionario.setSenha(PasswordUtils.gerarByCrypt("12345"));
		funcionario.setCpf(CPF);
		funcionario.setEmail(EMAIL);
		funcionario.setEmpresaId(empresaId);
		funcionario.setDataCriacao(LocalDateTime.now());
		funcionario.setDataAtualizacao(LocalDateTime.now());
		return funcionario;
	}

	private Empresa obterDadosEmpresa() {
		Empresa empresa = new Empresa();
		empresa.setRazaoSocial("SFSTecnologia");
		empresa.setCnpj("51463645000100");
		empresa.setDataCriacao(LocalDateTime.now());
		empresa.setDataAtualizacao(LocalDateTime.now());
		return empresa;
	}
}
