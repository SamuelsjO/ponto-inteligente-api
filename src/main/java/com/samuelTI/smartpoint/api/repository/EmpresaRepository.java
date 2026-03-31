package com.samuelTI.smartpoint.api.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import com.samuelTI.smartpoint.api.entities.Empresa;
import com.samuelTI.smartpoint.api.repository.sql.EmpresaSql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class EmpresaRepository {

    private final JdbcTemplate jdbc;

    public Optional<Empresa> findByCnpj(String cnpj) {
        return jdbc.query(EmpresaSql.FIND_BY_CNPJ, this::mapRow, cnpj)
                .stream().findFirst();
    }

    public Empresa save(Empresa empresa) {
        if (empresa.getId() == null) {
            return insert(empresa);
        }
        return update(empresa);
    }

    public void deleteAll() {
        jdbc.update(EmpresaSql.DELETE_ALL);
    }

    private Empresa insert(Empresa empresa) {
        var keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var ps = connection.prepareStatement(EmpresaSql.INSERT, new String[]{"id"});
            ps.setString(1, empresa.getRazaoSocial());
            ps.setString(2, empresa.getCnpj());
            ps.setTimestamp(3, Timestamp.valueOf(empresa.getDataCriacao()));
            ps.setTimestamp(4, Timestamp.valueOf(empresa.getDataAtualizacao()));
            return ps;
        }, keyHolder);
        empresa.setId(keyHolder.getKey().longValue());
        return empresa;
    }

    private Empresa update(Empresa empresa) {
        jdbc.update(EmpresaSql.UPDATE,
                empresa.getRazaoSocial(),
                empresa.getCnpj(),
                Timestamp.valueOf(empresa.getDataAtualizacao()),
                empresa.getId());
        return empresa;
    }

    private Empresa mapRow(ResultSet rs, int rowNum) throws SQLException {
        var empresa = new Empresa();
        empresa.setId(rs.getLong("id"));
        empresa.setRazaoSocial(rs.getString("razao_social"));
        empresa.setCnpj(rs.getString("cnpj"));
        empresa.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        empresa.setDataAtualizacao(rs.getTimestamp("data_atualizacao").toLocalDateTime());
        return empresa;
    }
}
