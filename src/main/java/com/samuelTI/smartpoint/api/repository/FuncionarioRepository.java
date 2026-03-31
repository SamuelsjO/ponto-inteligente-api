package com.samuelTI.smartpoint.api.repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import com.samuelTI.smartpoint.api.entities.Funcionario;
import com.samuelTI.smartpoint.api.enums.PerfilEnum;
import com.samuelTI.smartpoint.api.repository.sql.FuncionarioSql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class FuncionarioRepository {

    private final JdbcTemplate jdbc;

    public Optional<Funcionario> findById(Long id) {
        return jdbc.query(FuncionarioSql.FIND_BY_ID, this::mapRow, id)
                .stream().findFirst();
    }

    public Optional<Funcionario> findByCpf(String cpf) {
        return jdbc.query(FuncionarioSql.FIND_BY_CPF, this::mapRow, cpf)
                .stream().findFirst();
    }

    public Optional<Funcionario> findByEmail(String email) {
        return jdbc.query(FuncionarioSql.FIND_BY_EMAIL, this::mapRow, email)
                .stream().findFirst();
    }

    public Optional<Funcionario> findByCpfOrEmail(String cpf, String email) {
        return jdbc.query(FuncionarioSql.FIND_BY_CPF_OR_EMAIL, this::mapRow, cpf, email)
                .stream().findFirst();
    }

    public Funcionario save(Funcionario funcionario) {
        if (funcionario.getId() == null) {
            return insert(funcionario);
        }
        return update(funcionario);
    }

    public void deleteAll() {
        jdbc.update(FuncionarioSql.DELETE_ALL);
    }

    private Funcionario insert(Funcionario funcionario) {
        var keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var ps = connection.prepareStatement(FuncionarioSql.INSERT, new String[]{"id"});
            ps.setString(1, funcionario.getNome());
            ps.setString(2, funcionario.getEmail());
            ps.setString(3, funcionario.getSenha());
            ps.setString(4, funcionario.getCpf());
            setNullableBigDecimal(ps, 5, funcionario.getValorHora());
            setNullableFloat(ps, 6, funcionario.getQtdHorasTrabalhoDia());
            setNullableFloat(ps, 7, funcionario.getQtdHorasAlmoco());
            ps.setString(8, funcionario.getPerfilString());
            ps.setTimestamp(9, Timestamp.valueOf(funcionario.getDataCriacao()));
            ps.setTimestamp(10, Timestamp.valueOf(funcionario.getDataAtualizacao()));
            setNullableLong(ps, 11, funcionario.getEmpresaId());
            return ps;
        }, keyHolder);
        funcionario.setId(keyHolder.getKey().longValue());
        return funcionario;
    }

    private Funcionario update(Funcionario funcionario) {
        jdbc.update(connection -> {
            var ps = connection.prepareStatement(FuncionarioSql.UPDATE);
            ps.setString(1, funcionario.getNome());
            ps.setString(2, funcionario.getEmail());
            ps.setString(3, funcionario.getSenha());
            ps.setString(4, funcionario.getCpf());
            setNullableBigDecimal(ps, 5, funcionario.getValorHora());
            setNullableFloat(ps, 6, funcionario.getQtdHorasTrabalhoDia());
            setNullableFloat(ps, 7, funcionario.getQtdHorasAlmoco());
            ps.setString(8, funcionario.getPerfilString());
            ps.setTimestamp(9, Timestamp.valueOf(funcionario.getDataAtualizacao()));
            setNullableLong(ps, 10, funcionario.getEmpresaId());
            ps.setLong(11, funcionario.getId());
            return ps;
        });
        return funcionario;
    }

    private Funcionario mapRow(ResultSet rs, int rowNum) throws SQLException {
        var f = new Funcionario();
        f.setId(rs.getLong("id"));
        f.setNome(rs.getString("nome"));
        f.setEmail(rs.getString("email"));
        f.setSenha(rs.getString("senha"));
        f.setCpf(rs.getString("cpf"));

        BigDecimal valorHora = rs.getBigDecimal("valor_hora");
        f.setValorHora(valorHora);

        float qtdHorasDia = rs.getFloat("qtd_horas_trabalho_dia");
        f.setQtdHorasTrabalhoDia(rs.wasNull() ? null : qtdHorasDia);

        float qtdAlmoco = rs.getFloat("qtd_horas_almoco");
        f.setQtdHorasAlmoco(rs.wasNull() ? null : qtdAlmoco);

        f.setPerfil(PerfilEnum.valueOf(rs.getString("perfil")));
        f.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        f.setDataAtualizacao(rs.getTimestamp("data_atualizacao").toLocalDateTime());

        long empresaId = rs.getLong("empresa_id");
        f.setEmpresaId(rs.wasNull() ? null : empresaId);

        return f;
    }

    private void setNullableBigDecimal(java.sql.PreparedStatement ps, int idx, BigDecimal val) throws SQLException {
        if (val != null) ps.setBigDecimal(idx, val);
        else ps.setNull(idx, Types.DECIMAL);
    }

    private void setNullableFloat(java.sql.PreparedStatement ps, int idx, Float val) throws SQLException {
        if (val != null) ps.setFloat(idx, val);
        else ps.setNull(idx, Types.REAL);
    }

    private void setNullableLong(java.sql.PreparedStatement ps, int idx, Long val) throws SQLException {
        if (val != null) ps.setLong(idx, val);
        else ps.setNull(idx, Types.BIGINT);
    }
}
