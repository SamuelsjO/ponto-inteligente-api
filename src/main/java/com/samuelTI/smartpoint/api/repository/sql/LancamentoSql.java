package com.samuelTI.smartpoint.api.repository.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LancamentoSql {

    public static final String SELECT_ALL_COLUMNS = """
            SELECT id, data, descricao, localizacao,
                   data_criacao, data_atualizacao, tipo, funcionario_id
            FROM lancamento
            """;

    public static final String FIND_BY_ID = SELECT_ALL_COLUMNS + "WHERE id = ?";

    public static final String FIND_BY_FUNCIONARIO_ID = SELECT_ALL_COLUMNS + "WHERE funcionario_id = ?";

    public static final String INSERT = """
            INSERT INTO lancamento (data, descricao, localizacao,
                                    data_criacao, data_atualizacao, tipo, funcionario_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    public static final String UPDATE = """
            UPDATE lancamento
            SET data = ?, descricao = ?, localizacao = ?,
                data_atualizacao = ?, tipo = ?, funcionario_id = ?
            WHERE id = ?
            """;

    public static final String DELETE_BY_ID = "DELETE FROM lancamento WHERE id = ?";

    public static final String DELETE_ALL = "DELETE FROM lancamento";
}
