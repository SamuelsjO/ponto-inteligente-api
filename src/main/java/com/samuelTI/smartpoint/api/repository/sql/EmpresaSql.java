package com.samuelTI.smartpoint.api.repository.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmpresaSql {

    public static final String FIND_BY_CNPJ = """
            SELECT id, razao_social, cnpj, data_criacao, data_atualizacao
            FROM empresa
            WHERE cnpj = ?
            """;

    public static final String INSERT = """
            INSERT INTO empresa (razao_social, cnpj, data_criacao, data_atualizacao)
            VALUES (?, ?, ?, ?)
            """;

    public static final String UPDATE = """
            UPDATE empresa
            SET razao_social = ?, cnpj = ?, data_atualizacao = ?
            WHERE id = ?
            """;

    public static final String DELETE_ALL = "DELETE FROM empresa";
}
