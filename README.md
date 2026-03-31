# Ponto Inteligente API

API REST para controle de ponto eletrônico de funcionários. Permite o cadastro de empresas e funcionários, registro de lançamentos de ponto (início/fim de expediente, almoço e pausas) e autenticação via JWT.

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.4.4 |
| Persistência | JdbcTemplate puro (sem ORM) |
| Banco de dados | PostgreSQL 16 |
| Migração | Flyway |
| Autenticação | JWT (jjwt 0.12) + Spring Security 6 |
| Cache | Caffeine |
| Documentação | SpringDoc OpenAPI 3 (Swagger UI) |
| Build | Maven + Maven Wrapper |
| Container | Docker (multi-stage, ~140 MB) |
| CI | GitHub Actions |
| Testes | JUnit 5 + Mockito + H2 |

## Arquitetura

```
src/main/java/com/samuelTI/smartpoint/api/
├── config/                  # OpenAPI config
├── controllers/             # REST controllers
├── dtos/                    # DTOs + PageResult (record)
├── entities/                # POJOs puros (sem annotations de framework)
├── enums/                   # PerfilEnum, TipoEnum
├── repository/              # Classes concretas com JdbcTemplate
│   └── sql/                 # SQL em constantes isoladas
├── responses/               # Response wrapper
├── security/                # JWT auth (filter, config, utils)
│   ├── config/
│   ├── controllers/
│   ├── dto/
│   ├── filters/
│   ├── services/
│   └── utils/
├── services/                # Interfaces de serviço
│   └── impl/                # Implementações
└── utils/                   # BCrypt utils
```

**Decisões de design:**
- **Sem ORM** — Repositórios são classes `@Repository` com `JdbcTemplate` + `RowMapper` manual. Nenhuma dependência de Spring Data.
- **SQL explícito** — Queries vivem em classes no pacote `repository.sql` como constantes `String`. Sem mágica, sem geração automática.
- **Entidades são POJOs** — Sem `@Table`, `@Id`, `@Entity`. Nenhuma annotation de framework nas classes de domínio.
- **Paginação própria** — `PageResult<T>` é um `record` que substitui `Page` do Spring Data.

## Modelo de dados

```
┌──────────────┐       ┌──────────────────┐       ┌──────────────┐
│   empresa    │       │   funcionario    │       │  lancamento  │
├──────────────┤       ├──────────────────┤       ├──────────────┤
│ id           │◄──────│ empresa_id       │       │ id           │
│ razao_social │       │ id               │◄──────│ funcionario_id│
│ cnpj         │       │ nome             │       │ data         │
│ data_criacao │       │ email            │       │ tipo         │
│ data_atuali… │       │ senha (bcrypt)   │       │ descricao    │
└──────────────┘       │ cpf              │       │ localizacao  │
                       │ perfil           │       │ data_criacao │
                       │ valor_hora       │       │ data_atuali… │
                       │ qtd_horas_trab…  │       └──────────────┘
                       │ qtd_horas_almoco │
                       │ data_criacao     │
                       │ data_atuali…     │
                       └──────────────────┘
```

**Tipos de lançamento:** `START_WORK`, `TERM_WORK`, `START_LUNCH`, `TERM_LUNCH`, `START_BREAK`, `TERM_BREAK`

**Perfis:** `ROLE_ADMIN`, `ROLE_USUARIO`

## Endpoints

### Autenticação (público)

| Método | Path | Descrição |
|---|---|---|
| `POST` | `/auth` | Gerar token JWT |
| `POST` | `/auth/refresh` | Renovar token JWT |

### Cadastro (público)

| Método | Path | Descrição |
|---|---|---|
| `POST` | `/api/cadastra-pj` | Cadastrar empresa + funcionário (PJ) |
| `POST` | `/api/cadastra-pf` | Cadastrar funcionário em empresa existente (PF) |

### Empresa (autenticado)

| Método | Path | Descrição |
|---|---|---|
| `GET` | `/api/empresas/cnpj/{cnpj}` | Buscar empresa por CNPJ |

### Funcionário (autenticado)

| Método | Path | Descrição |
|---|---|---|
| `PUT` | `/api/funcionarios/{id}` | Atualizar dados do funcionário |

### Lançamento (autenticado)

| Método | Path | Descrição |
|---|---|---|
| `POST` | `/api/lancamentos` | Registrar lançamento de ponto |
| `GET` | `/api/lancamentos/{id}` | Buscar lançamento por ID |
| `PUT` | `/api/lancamentos/{id}` | Atualizar lançamento |
| `DELETE` | `/api/lancamentos/{id}` | Remover lançamento *(ADMIN)* |
| `GET` | `/api/lancamentos/funcionario/{id}?pag=0` | Listar lançamentos paginados por funcionário |

## Quick Start

### Pré-requisitos

- Java 21+ ([SDKMAN](https://sdkman.io/): `sdk install java 21.0.9-amzn`)
- Docker e Docker Compose

### 1. Subir o banco

```bash
docker compose up -d postgres
```

PostgreSQL 16 disponível em `localhost:5433`. O Flyway cria as tabelas e o usuário admin automaticamente no primeiro boot.

### 2. Rodar a aplicação

```bash
./mvnw spring-boot:run
```

A API sobe na porta **8090**. Swagger UI disponível em:

```
http://localhost:8090/swagger-ui.html
```

### 3. Autenticar

O Flyway já cria um usuário admin:

```bash
curl -s -X POST http://localhost:8090/auth \
  -H 'Content-Type: application/json' \
  -d '{"email":"faculdade@gmail.com","senha":"123456"}'
```

Resposta:
```json
{
  "data": { "token": "eyJhbG..." },
  "errors": []
}
```

Use o token nos requests autenticados:
```bash
curl -H "Authorization: Bearer <token>" http://localhost:8090/api/empresas/cnpj/82198127000121
```

No Swagger UI, clique no botão **Authorize** (cadeado) e cole o token.

### 4. Exemplo completo — registrar ponto

```bash
TOKEN="<seu_token>"

# Registrar início do expediente
curl -X POST http://localhost:8090/api/lancamentos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "data": "2026-03-30 08:00:00",
    "tipo": "START_WORK",
    "descricao": "Inicio do expediente",
    "localizacao": "Home Office",
    "funcionarioId": 1
  }'

# Listar lançamentos do funcionário
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8090/api/lancamentos/funcionario/1
```

## Testes

```bash
./mvnw test
```

25 testes — repositórios, services e controllers. Usa H2 em modo PostgreSQL como banco em memória (profile `test`).

## Docker

### Build e run via Docker Compose

```bash
docker compose up --build
```

Sobe PostgreSQL + aplicação. A app espera o healthcheck do Postgres antes de iniciar.

### Só o build da imagem

```bash
docker build -t ponto-inteligente-api .
```

Imagem multi-stage com Eclipse Temurin 21 Alpine, non-root user, ZGC. ~140 MB.

## Configuração

Variáveis de ambiente para produção:

| Variável | Obrigatória | Default | Descrição |
|---|---|---|---|
| `DB_URL` | Sim | — | JDBC URL do PostgreSQL |
| `DB_USER` | Sim | — | Usuário do banco |
| `DB_PASSWORD` | Sim | — | Senha do banco |
| `JWT_SECRET` | Sim | — | Chave secreta para assinar tokens JWT (mín. 32 chars) |
| `JWT_EXPIRATION` | Não | `604800` | Duração do token em segundos (padrão: 7 dias) |
| `CORS_ALLOWED_ORIGINS` | Não | `http://localhost:3000` | Origins permitidas para CORS |
| `PORT` | Não | `8090` | Porta do servidor |

## CI/CD

GitHub Actions (`.github/workflows/ci.yml`):

- **Build** — JDK 21 Temurin, cache Maven, `./mvnw verify`
- **Docker** — Build da imagem em push na `main`

## Licença

[MIT](LICENSE)
