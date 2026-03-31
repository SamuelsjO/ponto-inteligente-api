package com.samuelTI.smartpoint.api.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import com.samuelTI.smartpoint.api.entities.Lancamento;
import com.samuelTI.smartpoint.api.enums.TipoEnum;
import com.samuelTI.smartpoint.api.repository.sql.LancamentoSql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class LancamentoRepository {

    private final JdbcTemplate jdbc;

    public Optional<Lancamento> findById(Long id) {
        return jdbc.query(LancamentoSql.FIND_BY_ID, this::mapRow, id)
                .stream().findFirst();
    }

    public List<Lancamento> findByFuncionarioId(Long funcionarioId) {
        return jdbc.query(LancamentoSql.FIND_BY_FUNCIONARIO_ID, this::mapRow, funcionarioId);
    }

    public Lancamento save(Lancamento lancamento) {
        if (lancamento.getId() == null) {
            return insert(lancamento);
        }
        return update(lancamento);
    }

    public void deleteById(Long id) {
        jdbc.update(LancamentoSql.DELETE_BY_ID, id);
    }

    public void deleteAll() {
        jdbc.update(LancamentoSql.DELETE_ALL);
    }

    private Lancamento insert(Lancamento lancamento) {
        var keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var ps = connection.prepareStatement(LancamentoSql.INSERT, new String[]{"id"});
            ps.setTimestamp(1, Timestamp.valueOf(lancamento.getData()));
            ps.setString(2, lancamento.getDescricao());
            ps.setString(3, lancamento.getLocalizacao());
            ps.setTimestamp(4, Timestamp.valueOf(lancamento.getDataCriacao()));
            ps.setTimestamp(5, Timestamp.valueOf(lancamento.getDataAtualizacao()));
            ps.setString(6, lancamento.getTipoString());
            ps.setLong(7, lancamento.getFuncionarioId());
            return ps;
        }, keyHolder);
        lancamento.setId(keyHolder.getKey().longValue());
        return lancamento;
    }

    private Lancamento update(Lancamento lancamento) {
        jdbc.update(LancamentoSql.UPDATE,
                Timestamp.valueOf(lancamento.getData()),
                lancamento.getDescricao(),
                lancamento.getLocalizacao(),
                Timestamp.valueOf(lancamento.getDataAtualizacao()),
                lancamento.getTipoString(),
                lancamento.getFuncionarioId(),
                lancamento.getId());
        return lancamento;
    }

    private Lancamento mapRow(ResultSet rs, int rowNum) throws SQLException {
        var l = new Lancamento();
        l.setId(rs.getLong("id"));
        l.setData(rs.getTimestamp("data").toLocalDateTime());
        l.setDescricao(rs.getString("descricao"));
        l.setLocalizacao(rs.getString("localizacao"));
        l.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        l.setDataAtualizacao(rs.getTimestamp("data_atualizacao").toLocalDateTime());
        l.setTipo(TipoEnum.valueOf(rs.getString("tipo")));
        l.setFuncionarioId(rs.getLong("funcionario_id"));
        return l;
    }
}
