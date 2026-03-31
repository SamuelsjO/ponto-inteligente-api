package com.samuelTI.smartpoint.api.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;


import com.samuelTI.smartpoint.api.entities.Funcionario;
import com.samuelTI.smartpoint.api.repository.FuncionarioRepository;


@SpringBootTest
@ActiveProfiles("test")
public class FuncionarioServiceTest {

	@MockBean
	private FuncionarioRepository funcionarioRepository;

	@Autowired
	private FuncionarioService funcionarioService;

	private static final String EMAIL_EMPLOYEE = "faculdadesjs@gmail.com";
	private static final String CPF_EMPLOYEE = "10636132641";

	@BeforeEach
	public void setUp() throws Exception {
		BDDMockito.given(this.funcionarioRepository.save(Mockito.any(Funcionario.class)))
				.willReturn(new Funcionario());
		BDDMockito.given(this.funcionarioRepository.findById(Mockito.anyLong()))
				.willReturn(Optional.of(new Funcionario()));
		BDDMockito.given(this.funcionarioRepository.findByEmail(Mockito.anyString()))
				.willReturn(Optional.of(new Funcionario()));
		BDDMockito.given(this.funcionarioRepository.findByCpf(Mockito.anyString()))
				.willReturn(Optional.of(new Funcionario()));
	}

	@Test
	public void testPersitirFuncionario() {
		Funcionario funcionario = this.funcionarioService.persitirFunc(new Funcionario());
		assertNotNull(funcionario);
	}

	@Test
	public void testBuscarFuncionarioPorId() {
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(1L);
		assertTrue(funcionario.isPresent());
	}

	@Test
	public void testBuscarFuncionarioPorEmail() {
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorEmail(EMAIL_EMPLOYEE);
		assertTrue(funcionario.isPresent());
	}

	@Test
	public void testBuscarFuncionarioPorCpf() {
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorCpf(CPF_EMPLOYEE);
		assertTrue(funcionario.isPresent());
	}
}
