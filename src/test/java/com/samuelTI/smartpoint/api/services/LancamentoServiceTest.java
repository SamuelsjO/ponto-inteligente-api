package com.samuelTI.smartpoint.api.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.samuelTI.smartpoint.api.dtos.PageResult;
import com.samuelTI.smartpoint.api.entities.Lancamento;
import com.samuelTI.smartpoint.api.repository.LancamentoRepository;

@SpringBootTest
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@MockBean
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private LancamentoService lancamentoService;

	@BeforeEach
	public void setUp() {
		BDDMockito.given(this.lancamentoRepository.findByFuncionarioId(Mockito.anyLong()))
				.willReturn(new ArrayList<Lancamento>());
		BDDMockito.given(this.lancamentoRepository.findById(Mockito.anyLong()))
				.willReturn(Optional.of(new Lancamento()));
		BDDMockito.given(this.lancamentoRepository.save(Mockito.any(Lancamento.class)))
				.willReturn(new Lancamento());
	}

	@Test
	public void testBuscarLancamentoByFuncionarioId() {
		PageResult<Lancamento> lancamento = this.lancamentoService.buscarPorFuncionario(1L, 0, 10);
		assertNotNull(lancamento);
	}

	@Test
	public void testBuscarLancamentoById() {
		Optional<Lancamento> lancamento = this.lancamentoService.buscarById(1L);
		assertTrue(lancamento.isPresent());
	}

	@Test
	public void testPersitirLancamento() {
		Lancamento lancamento = this.lancamentoService.persitir(new Lancamento());
		assertNotNull(lancamento);
	}
}
