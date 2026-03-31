# Guia de Migração: JPA → Spring Data JDBC

## Por que trocar?

| JPA / Hibernate | Spring Data JDBC |
|-----------------|-----------------|
| Lazy loading implícito → N+1 surpresa | Sem lazy loading — carrega o que pedir, explicitamente |
| Proxies em runtime → stack traces confusos | POJOs puros, sem proxy |
| Cache L1 automático → estado stale | Sem cache implícito — previsível |
| `@Entity`, `@ManyToOne`, `@OneToMany`, cascade | `@Table`, `@Id`, aggregate roots |
| Dirty checking → updates implícitos | Save explícito — muda quando vc manda |
| Hibernate session → complexidade | Connection direta → simples |
| DDL gerado → imprevisível | DDL sempre via Flyway |
| ~50 annotations | ~5 annotations |

**Em resumo:** Spring Data JDBC é mais simples, mais explícito e mais previsível.
Pra um projeto desse tamanho (3 entidades), não tem motivo pra carregar Hibernate.

---

## Conceitos-chave do Spring Data JDBC

### Aggregate Roots

Spring Data JDBC implementa DDD aggregates:
- Cada repository gerencia um **aggregate root**
- Entidades filhas dentro do aggregate são gerenciadas pelo root
- Referências entre aggregates são por **ID** (não por objeto)

No nosso modelo:
```
Empresa (aggregate root)
  └── não tem filhos no aggregate (Funcionario é outro aggregate)

Funcionario (aggregate root)
  └── não tem filhos no aggregate (Lancamento é outro aggregate)

Lancamento (aggregate root)
  └── referencia Funcionario por empresaId / funcionarioId (Long)
```

**Decisão:** cada entidade é seu próprio aggregate root com referências por ID.
Isso é mais simples que tentar embutir (o JPA original já fazia eager em tudo).

### Sem anotações de relacionamento

Nada de `@ManyToOne`, `@OneToMany`, `@JoinColumn`.
Referências entre aggregates são `Long` (FK).

---

## Migração Passo a Passo

### 1. Dependência no pom.xml

```xml
<!-- REMOVER -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- ADICIONAR -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jdbc</artifactId>
</dependency>
```

### 2. Entidade Empresa

**Antes (JPA):**
```java
@Entity
@Table(name = "empresa")
public class Empresa implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "razao_social", nullable = false)
    private String razaoSocial;
    @Column(name = "cnpj", nullable = false)
    private String cnpj;
    private Date dataCriacao;
    private Date dataAtualizacao;
    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Funcionario> funcionarios;
    // ... getters, setters, PrePersist, PreUpdate
}
```

**Depois (Spring Data JDBC):**
```java
@Table("empresa")
public class Empresa {
    @Id
    private Long id;
    private String razaoSocial;
    private String cnpj;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    // SEM lista de funcionarios — referência por ID, não por objeto
    // SEM Serializable — não é necessário
}
```

Notas:
- `@Table` vem de `org.springframework.data.relational.core.mapping.Table`
- `@Id` vem de `org.springframework.data.annotation.Id`
- Sem `@Column` — Spring Data JDBC mapeia por naming strategy (snake_case por default)
- Sem `@GeneratedValue` — Spring Data JDBC detecta auto-increment pela coluna `id`
- Sem `@PrePersist`/`@PreUpdate` — usar callbacks via `BeforeSaveCallback` ou setar manualmente
- `Date` → `LocalDateTime`

### 3. Entidade Funcionario

**Depois (Spring Data JDBC):**
```java
@Table("funcionario")
public class Funcionario {
    @Id
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String cpf;
    private BigDecimal valorHora;
    private Float qtdHorasTrabalhadaDia;  // renomear coluna ou usar @Column
    private Float qtdHorasAlmoco;
    private String perfil;  // ou PerfilEnum (JDBC converte automaticamente)
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private Long empresaId;  // FK como Long, não objeto Empresa
    // SEM lista de lancamentos
}
```

Notas:
- `empresa_id` no banco → `empresaId` no Java (naming strategy cuida)
- Enum pode ser guardado como String se o banco guardar String
- Sem `@ManyToOne`, sem `@Enumerated` — JDBC converte automaticamente se o tipo bater

### 4. Entidade Lancamento

**Depois (Spring Data JDBC):**
```java
@Table("lancamento")
public class Lancamento {
    @Id
    private Long id;
    private LocalDateTime data;
    private String descricao;
    private String localizacao;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String tipo;  // ou TipoEnum
    private Long funcionarioId;  // FK como Long
}
```

### 5. Repositories

**Antes (JPA):**
```java
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Empresa findByCnpj(String cnpj);
}
```

**Depois (JDBC):**
```java
public interface EmpresaRepository extends CrudRepository<Empresa, Long> {
    Optional<Empresa> findByCnpj(String cnpj);
}
```

Notas:
- `JpaRepository` → `CrudRepository` (ou `ListCrudRepository` se quiser `List` em vez de `Iterable`)
- Paginação: extender `PagingAndSortingRepository` também
- Queries derivadas funcionam igual (findByXxx)
- Pra queries complexas: `@Query("SELECT ... FROM ...")` com SQL nativo (não JPQL!)

```java
public interface LancamentoRepository extends CrudRepository<Lancamento, Long>,
        PagingAndSortingRepository<Lancamento, Long> {

    List<Lancamento> findByFuncionarioId(Long funcionarioId);

    Page<Lancamento> findByFuncionarioId(Long funcionarioId, Pageable pageable);

    // Se query derivada não funcionar, usar SQL nativo:
    // @Query("SELECT * FROM lancamento WHERE funcionario_id = :funcionarioId")
    // Page<Lancamento> findByFuncionarioId(@Param("funcionarioId") Long funcionarioId, Pageable pageable);
}
```

### 6. Callbacks (substituir @PrePersist / @PreUpdate)

Criar um `BeforeSaveCallback` genérico ou setar datas no Service:

**Opção A — Callback global (recomendado):**
```java
@Component
public class AuditingCallback implements BeforeSaveCallback<Object> {
    @Override
    public Object onBeforeSave(Object entity, MutableAggregateChange<?> change) {
        // usar reflection ou interface comum pra setar datas
        return entity;
    }
}
```

**Opção B — Setar no Service (mais explícito):**
```java
public Empresa persistir(Empresa empresa) {
    LocalDateTime agora = LocalDateTime.now();
    if (empresa.getId() == null) {
        empresa.setDataCriacao(agora);
    }
    empresa.setDataAtualizacao(agora);
    return empresaRepository.save(empresa);
}
```

Opção B é mais simples e mais explícita — recomendada pro escopo desse projeto.

### 7. Cache (@Cacheable / @CachePut)

Não muda nada. Spring Cache funciona independente de JPA/JDBC.
Só trocar EhCache por Caffeine na config.

### 8. Flyway

Não muda nada. As migrations continuam as mesmas.
Spring Data JDBC usa o schema que já existe.

### 9. Testes

- `@DataJpaTest` → `@DataJdbcTest` (ou `@SpringBootTest` pra integration)
- Remover imports de `javax.persistence`
- H2 continua funcionando

### 10. application.properties

```properties
# REMOVER (JPA-specific)
spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.type=trace
spring.jpa.properties.hibernate.id.new_generator_mappings=false

# MANTER
spring.datasource.url=...
spring.datasource.username=...
spring.datasource.password=...

# ADICIONAR (opcional — log SQL)
logging.level.org.springframework.jdbc.core=DEBUG
```

---

## Checklist de Migração

- [ ] Trocar dependência no pom.xml (data-jpa → data-jdbc)
- [ ] Migrar Empresa (remover @Entity, @OneToMany, Serializable, @PrePersist)
- [ ] Migrar Funcionario (remover @Entity, @ManyToOne, @Enumerated, etc)
- [ ] Migrar Lancamento (remover @Entity, @ManyToOne, @Temporal, etc)
- [ ] Migrar EmpresaRepository (JpaRepository → CrudRepository)
- [ ] Migrar FuncionarioRepository
- [ ] Migrar LancamentoRepository (NamedQuery → @Query SQL nativo)
- [ ] Ajustar Services pra setar datas manualmente (substituir @PrePersist)
- [ ] Ajustar Controllers que usavam navegação de objetos (ex: `lancamento.getFuncionario().getId()`)
- [ ] Ajustar PessoaFisicaController e PessoaJuridicaController (setar empresaId em vez de objeto)
- [ ] Remover propriedades JPA do application.properties
- [ ] Migrar testes (@DataJpaTest → @DataJdbcTest)
- [ ] Rodar testes — tudo verde
- [ ] Testar manualmente os endpoints

---

## Pontos de Atenção

1. **Controllers navegam objetos:** `lancamento.getFuncionario().getId()` não funciona mais.
   Agora é `lancamento.getFuncionarioId()`. Ajustar conversão DTO↔Entity.

2. **PessoaJuridicaController:** cria Empresa e Funcionario juntos. Com JDBC,
   precisa salvar a Empresa primeiro, pegar o ID, e setar no Funcionario.
   (O JPA fazia isso via cascade — agora é explícito.)

3. **PessoaFisicaController:** busca Empresa por CNPJ e seta no Funcionario.
   Agora: busca Empresa, pega `empresa.getId()`, seta `funcionario.setEmpresaId(id)`.

4. **EmpresaController:** retornava Empresa com lista de funcionarios.
   Agora: Empresa não tem lista. Se precisar, fazer query separada.
   (No código atual, o controller NÃO usa a lista — então não impacta.)

5. **Enum storage:** JPA usava `@Enumerated(EnumType.STRING)`.
   Spring Data JDBC pode converter automaticamente se o enum tiver o mesmo nome.
   Se não funcionar, usar `ReadingConverter`/`WritingConverter`.
