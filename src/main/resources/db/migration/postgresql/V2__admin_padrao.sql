INSERT INTO empresa (razao_social, cnpj, data_criacao, data_atualizacao)
VALUES ('Samuel IT hungry', '82198127000121', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO funcionario (nome, email, senha, cpf, perfil, data_criacao, data_atualizacao, empresa_id)
VALUES ('ADMIN', 'faculdade@gmail.com',
        '$2a$06$xIvBeNRfS65L1N17I7JzgefzxEuLAL0Xk0wFAgIkoNqu9WD6rmp4m',
        '10636132641', 'ROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        (SELECT id FROM empresa WHERE cnpj = '82198127000121'));
