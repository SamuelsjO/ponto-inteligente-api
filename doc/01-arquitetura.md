# Arquitetura — Ponto Inteligente API

> Documento vivo. Atualizar conforme o projeto evolui.

## 1. Visão Geral

**Ponto Inteligente** é uma API REST de controle de jornada de trabalho.
Empresas cadastram funcionários, que registram lançamentos de ponto
(início/fim de trabalho, almoço e intervalo).

| Item | Valor |
|------|-------|
| Pacote base | `com.samuelTI.smartpoint.api` |
| Build | Maven (wrapper) |
| Linguagem | Java 9 → **Java 21** (migração planejada) |
| Framework | Spring Boot 2.1.5 → **Spring Boot 3.4** (migração planejada) |
| Banco | MySQL 8+ (dev/prod), H2 (testes) |
| Persistência | ~~Spring Data JPA~~ → **Spring Data JDBC** (migração planejada) |
| Cache | ~~EhCache 2~~ → **Caffeine** (migração planejada) |
| Segurança | Spring Security + JWT (jjwt) |
| Documentação API | ~~Springfox/Swagger 2~~ → **SpringDoc/OpenAPI 3** (migração planejada) |
| CI/CD | ~~Travis CI / Heroku~~ → **GitHub Actions / Docker** (migração planejada) |

---

## 2. Modelo de Domínio

```
┌──────────┐       1:N       ┌──────────────┐       1:N       ┌─────────────┐
│  Empresa │────────────────▶│ Funcionario   │────────────────▶│ Lancamento  │
│          │                 │               │                 │             │
│ id       │                 │ id            │                 │ id          │
│ cnpj     │                 │ nome          │                 │ data        │
│ razaoSoc │                 │ email         │                 │ tipo        │
│ dataCri  │                 │ cpf           │                 │ descricao   │
│ dataAtua │                 │ senha (bcrypt)│                 │ localizacao │
└──────────┘                 │ perfil        │                 │ dataCriacao │
                             │ valorHora     │                 │ dataAtualiz │
                             │ qtdHorasTrab  │                 │ funcionar_id│
                             │ qtdHorasAlm   │                 └─────────────┘
                             │ empresa_id    │
                             └──────────────┘
```

### Enums

- **PerfilEnum**: `ROLE_ADMIN`, `ROLE_USUARIO`
- **TipoEnum**: `START_WORK`, `TERM_WORK`, `START_LUNCH`, `TERM_LUNCH`, `START_BREAK`, `TERM_BREAK`

### Tabelas (MySQL)

Gerenciadas via Flyway:
- `V1__init.sql` — DDL: empresa, funcionario, lancamento + PKs + FKs
- `V2__admin_padrao.sql` — Seed: empresa + admin padrão

---

## 3. Camadas da Aplicação

```
Controller (REST)
    │
    ▼
Service (interface + impl)
    │
    ▼
Repository (Spring Data JDBC)
    │
    ▼
MySQL / H2
```

Não existe camada de "domain service" ou "use case" separada.
Para o escopo desse projeto didático, a estrutura 3-tier é suficiente.

---

## 4. Endpoints da API

### Públicos (sem autenticação)

| Método | Path | Descrição |
|--------|------|-----------|
| POST | `/auth` | Login — retorna JWT |
| POST | `/auth/refresh` | Renova token JWT |
| POST | `/api/cadastra-pj` | Cadastro Pessoa Jurídica (cria empresa + admin) |
| POST | `/api/cadastra-pf` | Cadastro Pessoa Física (vincula funcionário a empresa) |

### Autenticados (JWT Bearer)

| Método | Path | Descrição | Perfil |
|--------|------|-----------|--------|
| GET | `/api/empresas/cnpj/{cnpj}` | Buscar empresa por CNPJ | Qualquer |
| PUT | `/api/funcionarios/{id}` | Atualizar dados do funcionário | Qualquer |
| GET | `/api/lancamentos/funcionario/{id}` | Listar lançamentos (paginado) | Qualquer |
| GET | `/api/lancamentos/{id}` | Buscar lançamento por ID | Qualquer |
| POST | `/api/lancamentos` | Criar lançamento | Qualquer |
| PUT | `/api/lancamentos/{id}` | Atualizar lançamento | Qualquer |
| DELETE | `/api/lancamentos/{id}` | Remover lançamento | **ADMIN** |

### Documentação

| Método | Path | Descrição |
|--------|------|-----------|
| GET | `/swagger-ui/index.html` | UI interativa (SpringDoc) |
| GET | `/v3/api-docs` | OpenAPI 3 JSON |

---

## 5. Fluxo de Autenticação

```
1. POST /auth {email, senha}
2. AuthenticationManager valida via BCrypt
3. JwtTokenUtil gera token HS512 (expira em 7 dias)
4. Response: {token: "eyJ..."}

Requests seguintes:
  Header: Authorization: Bearer eyJ...
  → JwtAuthenticationTokenFilter extrai e valida
  → SecurityContext populado
  → Controller executa
```

---

## 6. Estrutura de Diretórios

```
src/main/java/com/samuelTI/smartpoint/api/
├── SmartPointApplication.java
├── config/                     # Configurações (Swagger, etc)
├── controllers/                # REST Controllers (5)
├── dtos/                       # Data Transfer Objects (5)
├── entities/                   # Entidades de domínio (3)
├── enums/                      # PerfilEnum, TipoEnum
├── repository/                 # Spring Data repositories (3)
├── responses/                  # Response wrapper genérico
├── security/
│   ├── config/                 # WebSecurityConfig
│   ├── controllers/            # AuthenticationController
│   ├── dto/                    # JwtAuthenticationDto, TokenDto
│   ├── filters/                # JwtAuthenticationTokenFilter
│   ├── services/               # JwtUserDetailsServiceImpl
│   └── utils/                  # JwtTokenUtil
├── services/                   # Service interfaces
│   └── impl/                   # Service implementations
└── utils/                      # PasswordUtils

src/main/resources/
├── application.properties      # Config principal (profile dev)
├── application-test.properties # Config de testes (H2)
├── application-prod.properties # Config de produção
├── db/migration/mysql/         # Flyway migrations
└── ehcache.xml                 # Cache config (será removido)

doc/                            # Documentação do projeto
```

---

## 7. Configuração por Ambiente

| Propriedade | dev | test | prod |
|-------------|-----|------|------|
| Banco | MySQL localhost | H2 in-memory | MySQL (env var) |
| DDL | Flyway | create (sem Flyway) | Flyway |
| JWT secret | env var (default dev) | env var | env var (obrigatório) |
| SQL logs | true | false | false |
| Porta | 8090 | 8090 | $PORT |
| Cache | Caffeine | desabilitado | Caffeine |
