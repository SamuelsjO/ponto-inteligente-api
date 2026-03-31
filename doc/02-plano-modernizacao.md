# Plano de Modernização — Ponto Inteligente API

## Resumo das Decisões

| Decisão | De | Para | Motivo |
|---------|----|------|--------|
| Linguagem | Java 9 | Java 21 | Records, pattern matching, virtual threads, LTS |
| Framework | Spring Boot 2.1.5 | Spring Boot 3.4 | javax→jakarta, Security 6, atualizações |
| Persistência | Spring Data JPA / Hibernate | **Spring Data JDBC** | Simplicidade, sem lazy loading, sem proxies, sem cache L1/L2 implícito, SQL explícito |
| Cache | EhCache 2 | Caffeine | Mais leve, sem XML, mantém @Cacheable |
| JWT | jjwt 0.9.0 | jjwt 0.12.6 | API moderna, segurança |
| Docs API | Springfox (Swagger 2) | SpringDoc (OpenAPI 3) | Springfox abandonado |
| CI/CD | Travis CI / Heroku | GitHub Actions / Docker | Travis e Heroku free morreram |
| Deps mortas | MongoDB, web-services | removidas | Nunca usadas |

---

## Fase 0 — Baseline e Cleanup

**Objetivo:** Partir de um estado limpo e funcional.

### Tarefas

- [ ] Corrigir permissão do Maven Wrapper (`chmod +x mvnw`)
- [ ] Rodar testes e documentar estado atual
- [ ] Remover dependências mortas (`spring-boot-starter-data-mongodb`, `spring-boot-starter-web-services`)
- [ ] Corrigir bugs:
  - Typo `getFuncioarios()` → `getFuncionarios()` em `Empresa.java`
  - Bug `setCnpj(funcionario.getCpf())` em `PessoaFisicaController`
  - Null safety: checar `isPresent()` antes do `.get()` em `FuncionarioController`
  - Remover import `com.sun.xml.messaging.saaj` em `LancamentoController`
  - Trocar `new PageRequest(...)` por `PageRequest.of(...)`
- [ ] Rodar testes novamente — tudo verde

**Commit:** `fix: corrigir bugs e remover dependências mortas`

---

## Fase 1 — Migração JPA → Spring Data JDBC

> **PRIORIDADE MÁXIMA** — executar antes da migração de Spring Boot.
> Motivo: migrar a persistência com as dependências atuais é mais seguro.
> Depois basta atualizar versões.

**Objetivo:** Eliminar Hibernate/JPA, usar Spring Data JDBC puro.

### O que muda

| Aspecto | JPA (antes) | JDBC (depois) |
|---------|------------|---------------|
| Annotations | `@Entity`, `@Table`, `@ManyToOne`, `@OneToMany`, `@GeneratedValue` | `@Table`, `@Id`, `@MappedCollection` |
| Queries | Derivadas + NamedQuery + proxy | Derivadas + `@Query` SQL nativo |
| Lazy loading | Sim (armadilha N+1) | Não existe (carrega o que pedir) |
| Session/Cache L1 | Automático | Não existe |
| Cascade | `CascadeType.ALL` | Aggregate roots controlam ciclo de vida |
| Proxies | Sim | Não |
| Transactions | Automáticas via proxy | Explícitas via `@Transactional` |

### Tarefas

Detalhes no arquivo `doc/03-migracao-jpa-jdbc.md`.

**Commit:** `feat: migrar persistência de JPA para Spring Data JDBC`

---

## Fase 2 — Migração de Build (Java 21 + Spring Boot 3.4)

**Objetivo:** Atualizar o motor.

### Tarefas

- [ ] Atualizar `pom.xml`:
  - `spring-boot-starter-parent` → 3.4.4
  - `java.version` → 21
  - `spring-boot-starter-data-jpa` → `spring-boot-starter-data-jdbc` (já feito na Fase 1)
  - `mysql-connector-java` → `com.mysql:mysql-connector-j`
  - `flyway-core` + adicionar `flyway-mysql`
  - Remover `net.sf.ehcache:ehcache`
  - Remover Springfox (springfox-swagger2, springfox-swagger-ui)
  - `jjwt` → `jjwt-api` 0.12.6 + `jjwt-impl` + `jjwt-jackson`
  - Adicionar `springdoc-openapi-starter-webmvc-ui` 2.8.x
  - Adicionar `spring-boot-starter-cache` + `caffeine`
- [ ] Migrar `javax` → `jakarta` (persistence, validation, servlet)
- [ ] Migrar Spring Security 6:
  - `WebSecurityConfigurerAdapter` → `SecurityFilterChain` @Bean
  - `authorizeRequests()` → `authorizeHttpRequests()`
  - `antMatchers()` → `requestMatchers()`
- [ ] Migrar JwtTokenUtil pra jjwt 0.12 API
- [ ] Substituir SwaggerConfig por SpringDoc config
- [ ] Substituir EhCache por Caffeine
- [ ] Adaptar testes

**Commit:** `feat: migrar para Java 21 + Spring Boot 3.4`

---

## Fase 3 — Segurança

**Objetivo:** Externalizar secrets, corrigir vulnerabilidades.

### Tarefas

- [ ] JWT secret via env var (`${JWT_SECRET}`) + validação de tamanho mínimo
- [ ] Credenciais DB via env vars (`${DB_URL}`, `${DB_USER}`, `${DB_PASSWORD}`)
- [ ] CORS centralizado no SecurityFilterChain (remover `@CrossOrigin` dos controllers)
- [ ] Corrigir bug: `tokenExpirado()` retorna `false` quando parse falha → deve retornar `true`
- [ ] Limpar `application-prod.properties` (sem credenciais)
- [ ] Criar `.env.example`
- [ ] Validação de config no startup (fail-fast)
- [ ] Garantir que logs não expõem secrets

**Commit:** `feat: externalizar secrets e corrigir vulnerabilidades`

---

## Fase 4 — Modernização do Código

**Objetivo:** Java 21 idiomático.

### Tarefas

- [ ] Entidades: `Date` → `LocalDateTime`, Lombok nos campos
- [ ] DTOs → Java Records
- [ ] Constructor injection (eliminar `@Autowired` field injection)
- [ ] Adaptar testes

**Commit:** `refactor: modernizar código com Java 21`

---

## Fase 5 — Infraestrutura

**Objetivo:** Containerizar e CI/CD moderno.

### Tarefas

- [ ] Dockerfile multi-stage (build + runtime)
- [ ] docker-compose.yml (MySQL + app)
- [ ] GitHub Actions CI workflow
- [ ] Remover .travis.yml e Procfile
- [ ] Atualizar README.md

**Commit:** `chore: containerizar e migrar CI para GitHub Actions`

---

## Ordem de Execução

```
Fase 0 (Cleanup)
    │
    ▼
Fase 1 (JPA → JDBC) ← NOVA PRIORIDADE
    │
    ▼
Fase 2 (Java 21 + Spring Boot 3.4)
    │
    ▼
Fase 3 (Segurança) ─┐
    │                 ├─ podem ser paralelas
Fase 4 (Código)    ──┘
    │
    ▼
Fase 5 (Infra)
```
