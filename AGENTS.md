# Ponto Inteligente API — Convenções do Projeto

> Este arquivo é lido automaticamente por AI assistants (HubAI Nitro, Cursor, Copilot, etc).
> Ele define as regras do projeto. Seguir à risca.

## Stack Alvo (pós-modernização)

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Java 21 (LTS) |
| Framework | Spring Boot 3.4.x |
| Build | Maven (wrapper) |
| Persistência | **Spring Data JDBC** (NÃO JPA/Hibernate) |
| Banco | MySQL 8+ (dev/prod), H2 (testes) |
| Migração DB | Flyway |
| Cache | Caffeine (via spring-boot-starter-cache) |
| Segurança | Spring Security 6 + JWT (jjwt 0.12.x) |
| Docs API | SpringDoc OpenAPI 3 |
| CI/CD | GitHub Actions |
| Container | Docker (multi-stage) |

## Regras de Código

### Geral

- **Sem JPA. Sem Hibernate. Sem `@Entity`.** Persistência é Spring Data JDBC puro.
- Entidades são POJOs com `@Table` e `@Id` (de `org.springframework.data.relational`).
- Referências entre aggregates são **por ID** (`Long`), nunca por objeto.
- Sem lazy loading, sem proxies, sem dirty checking.
- Queries derivadas ou `@Query` com **SQL nativo** (não JPQL, não HQL).
- Repositories estendem `CrudRepository` ou `ListCrudRepository`.

### Java

- Java 21 — usar Records pra DTOs, pattern matching, text blocks onde fizer sentido.
- `java.time.LocalDateTime` pra datas. Nunca `java.util.Date`.
- Constructor injection (sem `@Autowired` em campos). Pode usar `@RequiredArgsConstructor` (Lombok).
- Campos `final` onde possível.
- Lombok permitido: `@Getter`, `@Setter`, `@RequiredArgsConstructor`, `@ToString`, `@Builder`.
  Evitar `@Data` (equals/hashCode automático causa problemas com entidades).

### DTOs

- Records quando possível: `public record TokenDto(String token) {}`
- Validação com Bean Validation direto nos components do record.
- Sem `Optional` como campo de DTO. Campos opcionais são nullable.

### Services

- Interface + implementação (manter padrão atual).
- `@Transactional` explícito quando necessário.
- Datas de auditoria (criação/atualização) setadas no Service, não via callback.

### Controllers

- Sem `@CrossOrigin` nos controllers. CORS é centralizado no SecurityFilterChain.
- Response wrapper: `Response<T>` com `data` e `errors`.
- Validação via `@Valid` + `BindingResult`.

### Segurança

- JWT secret via env var `JWT_SECRET` (mínimo 32 caracteres).
- Credenciais do banco via env vars (`DB_URL`, `DB_USER`, `DB_PASSWORD`).
- Nenhuma credencial hardcoded nos properties.
- CORS configurável via env var `CORS_ORIGINS`.

### Testes

- `@DataJdbcTest` pra testes de repository (NÃO `@DataJpaTest`).
- `@SpringBootTest` + `@AutoConfigureMockMvc` pra testes de controller.
- Profile `test` usa H2 in-memory com Flyway desabilitado.
- Nomes descritivos em inglês (padrão atual do projeto).

### Flyway

- Migrations em `src/main/resources/db/migration/mysql/`.
- Formato: `V{n}__{descricao}.sql`.
- Nunca alterar uma migration já aplicada. Criar nova.

### Git

- Conventional Commits: `feat:`, `fix:`, `chore:`, `refactor:`, `docs:`, `test:`
- Mensagens em português.
- Uma feature/fix por commit.
- Não commitar `.env`, secrets, ou credenciais.

### Documentação

- Documentação do projeto na pasta `doc/`.
- README.md com instruções de setup e endpoints.
- OpenAPI 3 gerado automaticamente via SpringDoc.

## Estrutura de Pacotes

```
com.samuelTI.smartpoint.api/
├── config/          # Beans de configuração
├── controllers/     # REST Controllers
├── dtos/            # DTOs (Records)
├── entities/        # Entidades Spring Data JDBC
├── enums/           # Enumerações
├── repository/      # Spring Data JDBC Repositories
├── responses/       # Response wrapper
├── security/        # JWT, Security Config, Filters
├── services/        # Service interfaces
│   └── impl/        # Implementações
└── utils/           # Utilitários
```

## O que NÃO fazer

- Não adicionar JPA/Hibernate de volta.
- Não usar `@Entity`, `@ManyToOne`, `@OneToMany`, `@JoinColumn`.
- Não usar `java.util.Date`.
- Não hardcodar secrets nos properties.
- Não usar `@CrossOrigin` nos controllers.
- Não usar Springfox/Swagger 2.
- Não usar EhCache.
- Não usar `@Autowired` em campos (usar constructor injection).
