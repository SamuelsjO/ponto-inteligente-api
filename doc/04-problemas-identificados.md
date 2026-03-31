# Problemas Identificados — Ponto Inteligente API

> Levantamento feito em 30/03/2026. Base: código atual (Java 9, Spring Boot 2.1.5).

---

## Segurança (Críticos)

### SEC-01 | CRITICA — JWT secret hardcoded e commitado

**Arquivo:** `application.properties:27`, `application-prod.properties:14`
**Problema:** Secret `_@HRL&L3tF?Z7ccj4z&L5!nU2B!Rjs3_` em texto plano no repositório.
Qualquer pessoa com acesso ao repo pode forjar tokens JWT válidos.
**CWE:** CWE-798 (Use of Hard-coded Credentials)
**Correção:** Externalizar para env var `JWT_SECRET`. Fase 3 do plano.

### SEC-02 | CRITICA — Credenciais do banco de produção commitadas

**Arquivo:** `application-prod.properties:3`
**Problema:** URL ClearDB com user:password em texto plano: `mysql://be1869cfffde85:33bd91cc@...`
**CWE:** CWE-798
**Correção:** Externalizar para env vars `DB_URL`, `DB_USER`, `DB_PASSWORD`. Fase 3.

### SEC-03 | CRITICA — CORS wildcard em todos os controllers

**Arquivos:** Todos os 5 controllers + AuthenticationController
**Problema:** `@CrossOrigin(origins = "*")` permite qualquer origem acessar a API com o token JWT do usuário.
**CWE:** CWE-942 (Permissive Cross-domain Policy)
**Correção:** Centralizar CORS no SecurityFilterChain, configurável via env var. Fase 3.

### SEC-04 | ALTA — Bug no tokenExpirado() considera token inválido como válido

**Arquivo:** `JwtTokenUtil.java:96`
**Problema:**
```java
private boolean tokenExpirado(String token) {
    Date dataExpiracao = this.getExpirationDateFromToken(token);
    if (dataExpiracao == null) {
        return false;  // ← BUG: token sem data de expiração é considerado VÁLIDO
    }
    return dataExpiracao.before(new Date());
}
```
Se o parsing do token falha (token malformado, manipulado, etc), `getExpirationDateFromToken()`
retorna `null`, e o método diz "não expirou" → token é aceito.
**CWE:** CWE-287 (Improper Authentication)
**Correção:** `dataExpiracao == null` → retornar `true` (token inválido). Fase 3.

### SEC-05 | ALTA — Spring Boot 2.1.5 e jjwt 0.9.0 (EOL)

**Arquivo:** `pom.xml`
**Problema:** Spring Boot 2.1.5 (maio/2019) — sem patches de segurança há 6+ anos.
jjwt 0.9.0 (2017) — vulnerabilidades conhecidas, API deprecated.
**Correção:** Atualizar na Fase 2.

### SEC-06 | MEDIA — Senha admin previsível no seed

**Arquivo:** `V2__admin_padrao.sql`
**Problema:** Hash BCrypt commitado. Email `faculdade@gmail.com` público.
**Correção:** Remover seed de produção ou torná-lo configurável.

### SEC-07 | MEDIA — Swagger gera token automático pra user fixo

**Arquivo:** `SwaggerConfig.java:36-43`
**Problema:** Em profile dev, gera token JWT real para `faculdade@gmail.com` e injeta no Swagger UI.
**Correção:** Será removido com a migração pra SpringDoc. Fase 2.

---

## Bugs Funcionais

### BUG-01 | ALTA — Typo no getter getFuncioarios()

**Arquivo:** `Empresa.java:63`
**Problema:** Getter `getFuncioarios()` e setter `setFuncioarios()` — faltou o "n" de "funcionarios".
Hibernate pode não conseguir mapear o `@OneToMany(mappedBy = "empresa")` corretamente.
**Correção:** Fase 0.

### BUG-02 | ALTA — Mapeamento JPA inconsistente (field vs method level)

**Arquivo:** `Empresa.java` (annotations nos getters) vs `Funcionario.java` (annotations nos campos)
**Problema:** JPA Access Type inconsistente no mesmo projeto. Pode causar mapeamento incorreto.
**Correção:** Será eliminado com migração pra Spring Data JDBC. Fase 1.

### BUG-03 | MEDIA — PessoaFisicaController seta CNPJ com CPF

**Arquivo:** `PessoaFisicaController.java:88`
**Código:** `cadastroPFDto.setCnpj(funcionario.getCpf())`
**Problema:** Copia CPF no campo CNPJ do DTO de resposta. Bug de copiar/colar.
**Correção:** Trocar para `funcionario.getEmpresa().getCnpj()` (ou `empresaId` pós-JDBC). Fase 0.

### BUG-04 | MEDIA — FuncionarioController.atualizar() sem null check

**Arquivo:** `FuncionarioController.java:56`
**Problema:** Faz `funcionario.get()` logo após `if (!funcionario.isPresent())` que só adiciona erro no result,
mas NÃO retorna. A execução continua e chama `.get()` que lança NoSuchElementException.
**Correção:** Adicionar `return` após o `result.addError(...)`. Fase 0.

### BUG-05 | MEDIA — Import com.sun.xml não portável

**Arquivo:** `LancamentoController.java:19`
**Import:** `com.sun.xml.messaging.saaj.packaging.mime.internet.ParseException`
**Problema:** Classe interna do JDK, não faz parte da API pública. Pode não existir em outros JDKs.
Deveria usar `java.text.ParseException`.
**Correção:** Fase 0.

### BUG-06 | BAIXA — PageRequest deprecated

**Arquivo:** `LancamentoController.java:82`
**Código:** `new PageRequest(pag, this.qtdPorPagina, Direction.valueOf(dir), ord)`
**Problema:** Construtor deprecated desde Spring Data 2.0.
**Correção:** `PageRequest.of(pag, qtdPorPagina, Direction.valueOf(dir), ord)`. Fase 0.

### BUG-07 | BAIXA — Lombok importado mas não usado

**Arquivo:** `pom.xml` + todas as entidades
**Problema:** Lombok no classpath mas entidades são 100% verbosas.
**Correção:** Usar de verdade ou remover. Fase 4.

---

## Dependências Mortas

### DEP-01 — spring-boot-starter-data-mongodb

**Arquivo:** `pom.xml`
**Problema:** Importado mas nenhuma classe usa MongoDB. Nenhum repository Mongo. Nenhuma entity Mongo.
Adiciona dependências transitivas desnecessárias (driver MongoDB, etc).
**Correção:** Remover. Fase 0.

### DEP-02 — spring-boot-starter-web-services

**Arquivo:** `pom.xml`
**Problema:** Starter para SOAP/WS. O projeto é REST puro.
**Correção:** Remover. Fase 0.
