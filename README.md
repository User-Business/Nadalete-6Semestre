# Atlaz — Back-end (Sistema de Controle e Análise de Abastecimento)

API REST em Java/Spring Boot do [Sistema de Controle e Análise de Abastecimento](https://github.com/AtlazDB/Atlaz).
Este repositório isola o back-end do projeto original [AtlazDB/Atlaz-BackEnd](https://github.com/AtlazDB/Atlaz-BackEnd/tree/main/AtlazDB)
(histórico preservado) e acrescenta testes automatizados, análise estática e pipeline de CI.

| Stack | |
| --- | --- |
| Linguagem / Framework | Java 17, Spring Boot 3.5 (Web, Data JPA, Validation), Lombok |
| Banco de dados | PostgreSQL 16 + Flyway |
| Testes de unidade | JUnit 5, Mockito, AssertJ, JaCoCo |
| Testes de sistema (E2E) | REST-assured, JSON Schema Validator, Testcontainers (PostgreSQL) |
| Qualidade / CI | SonarCloud, GitHub Actions |

## Pré-requisitos

- JDK 17+
- Docker (Docker Desktop no Windows) — usado pelo `docker compose` da aplicação e pelos Testcontainers
- Maven 3.x (opcional: o projeto inclui o Maven Wrapper `mvnw`)

## Executando a aplicação

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local   # Windows: .\mvnw.cmd ...
```

O perfil `local` sobe o PostgreSQL do `docker-compose.yml` automaticamente. A API fica em
`http://localhost:8080/api` e a documentação Swagger em `http://localhost:8080/api/docs`.

## Testes

| Tipo | Comando | Onde ficam |
| --- | --- | --- |
| Unidade + relatório de cobertura | `./mvnw verify` | `src/test/java/**/*Test.java` |
| Sistema (E2E) | `./mvnw verify -DskipUnitTests=true -DskipSystemTests=false` | `src/test/java/**/e2e/*IT.java` |

- O relatório JaCoCo (usado pelo SonarCloud) é gerado em `target/site/jacoco/jacoco.xml`.
- Os testes de sistema sobem a aplicação completa em porta aleatória e um PostgreSQL isolado em container
  (Testcontainers); o schema é criado pelas migrations reais do Flyway. Os JSON Schemas das respostas ficam em
  `src/test/resources/schemas`.

## Pipeline de CI (GitHub Actions)

Arquivo: [`.github/workflows/ci.yml`](.github/workflows/ci.yml). Executa em todo push/PR para `main`:

```
unit-test  ──►  static-analysis  ──►  system-test
```

| Stage | O que faz |
| --- | --- |
| `unit-test` | Executa os testes de unidade e gera o relatório de cobertura JaCoCo (publicado como artefato) |
| `static-analysis` | Envia a análise estática ao SonarCloud com cobertura de linhas e branches e aguarda o Quality Gate |
| `system-test` | Executa os testes E2E (REST-assured + Testcontainers) e publica os relatórios |

### Configuração do SonarCloud (uma única vez)

1. Em [sonarcloud.io](https://sonarcloud.io), entre com o GitHub e importe este repositório (projeto público).
2. Em **Administration → Analysis Method**, desative a *Automatic Analysis* (a análise é feita pelo CI).
3. Em **My Account → Security**, gere um token.
4. No GitHub, em **Settings → Secrets and variables → Actions**:
   - *Secret* `SONAR_TOKEN`: o token gerado;
   - *Variable* `SONAR_ORGANIZATION`: a chave da organização no SonarCloud;
   - *Variable* `SONAR_PROJECT_KEY`: a chave do projeto no SonarCloud.

## Quality Gate

| Item | Critério | Resultado |
| --- | --- | --- |
| Code line coverage | > 75% | 99,8% |
| Code branch coverage | == 100% | 100% |
| Bugs | == 0 | 0 |
| Vulnerabilities | == 0 | 0 |
| Duplicidade | < 5% | 0,0% |
| Issues — Blocker | == 0 | 0 |
| Issues — Critical | == 0 | 0 |
| Issues — Major, Minor e Info | < 5 | 0 |

A estratégia evolutiva adotada, a comparação com a análise inicial e a matriz de cenários dos testes E2E estão em
[docs/ESTRATEGIA-QUALIDADE.md](docs/ESTRATEGIA-QUALIDADE.md).
