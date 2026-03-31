package com.samuelTI.smartpoint.api.repository.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FuncionarioSql {

    public static final String SELECT_ALL_COLUMNS = """
            SELECT id, nome, email, senha, cpf, valor_hora,
                   qtd_horas_trabalho_dia, qtd_horas_almoco, perfil,
                   data_criacao, data_atualizacao, empresa_id
            FROM funcionario
            """;

    public static final String FIND_BY_ID = SELECT_ALL_COLUMNS + "WHERE id = ?";

    public static final String FIND_BY_CPF = SELECT_ALL_COLUMNS + "WHERE cpf = ?";

    public static final String FIND_BY_EMAIL = SELECT_ALL_COLUMNS + "WHERE email = ?";

    public static final String FIND_BY_CPF_OR_EMAIL = SELECT_ALL_COLUMNS + "WHERE cpf = ? OR email = ?";

    public static final String INSERT = """
            INSERT INTO funcionario (nome, email, senha, cpf, valor_hora,
                                     qtd_horas_trabalho_dia, qtd_horas_almoco, perfil,
                                     data_criacao, data_atualizacao, empresa_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    public static final String UPDATE = """
            UPDATE funcionario
            SET nome = ?, email = ?, senha = ?, cpf = ?, valor_hora = ?,
                qtd_horas_trabalho_dia = ?, qtd_horas_almoco = ?, perfil = ?,
                data_atualizacao = ?, empresa_id = ?
            WHERE id = ?
            """;

    public static final String DELETE_ALL = "DELETE FROM funcionario";
}
