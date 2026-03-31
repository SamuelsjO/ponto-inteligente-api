package com.samuelTI.smartpoint.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

@SpringBootTest
@ActiveProfiles("test")
public class EmpresaRepositoryTest {

	@Autowired
	private EmpresaRepository empresaRepository;

	private static final String COMPANYTEST = "HERE, COMPANY EXAMPLE";
	private static final String CNPJ = "51463645000100";

	@BeforeEach
	public void setUp() {
		Empresa empresa = new Empresa();
		empresa.setRazaoSocial(COMPANYTEST);
		empresa.setCnpj(CNPJ);
		empresa.setDataCriacao(LocalDateTime.now());
		empresa.setDataAtualizacao(LocalDateTime.now());
		this.empresaRepository.save(empresa);
	}

	@AfterEach
	public void tearDown() {
		this.empresaRepository.deleteAll();
	}

	@Test
	public void testBuscarPorCnpj() {
		Optional<Empresa> empresa = this.empresaRepository.findByCnpj(CNPJ);
		assertTrue(empresa.isPresent());
		assertEquals(CNPJ, empresa.get().getCnpj());
	}
}
