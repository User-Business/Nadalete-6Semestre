# Estratégia evolutiva de qualidade

Este documento registra como o projeto saiu da análise inicial até atender o Quality Gate definido para o trabalho.

## 1. Análise inicial (baseline)

A primeira análise foi feita sobre o código original, sem nenhuma alteração além do isolamento do back-end e da
inclusão da pipeline (commit `ci: add GitHub Actions pipeline...`).

| Métrica | Inicial | Final |
| --- | --- | --- |
| Cobertura de linhas | 40,8% | 99,8% |
| Cobertura de branches | 15,3% | 100% |
| Bugs / Vulnerabilidades | 0 / 0 | 0 / 0 |
| Code smells | 18 (2 Critical, 5 Major, 11 Minor) | 0 |
| Security hotspots | 0 | 0 |
| Duplicação | 1,0% | 0,0% |
| Testes | 7 (integração com H2) | 119 de unidade + 80 de sistema |

> Valores medidos com o perfil de regras Java mais abrangente do Sonar. Os números exibidos no SonarCloud podem
> variar levemente conforme o perfil de regras ativo na organização.

### Issues encontradas na análise inicial

| Severidade | Regra | Ocorrências | Descrição |
| --- | --- | --- | --- |
| Critical | java:S1452 | 1 | `ResponseEntity<?>` (wildcard) no retorno do login |
| Critical | java:S1192 | 1 | Literal `"Vehicle not found"` duplicado 3 vezes |
| Major | java:S112 | 3 | `throw new RuntimeException(...)` genérica |
| Major | java:S1172 | 1 | Parâmetro `generateCsv` não utilizado no `RefuelingController` |
| Major | java:S125 | 1 | Código comentado em `ServiceOrder` |
| Minor | java:S120 | 9 | Pacote `com.example.AtlazDB` fora da convenção (maiúsculas) |
| Minor | java:S1128 | 2 | Imports duplicados |

## 2. Estratégia adotada

A evolução foi feita em passos pequenos, cada um em um commit, para que o SonarCloud registre a evolução:

1. **Isolamento do back-end** em repositório próprio, preservando o histórico da pasta `AtlazDB` (`git subtree split`).
2. **Pipeline primeiro**: CI com JaCoCo e SonarCloud sobre o código original, para registrar a linha de base.
3. **Convenção de nomes**: pacote renomeado para `com.example.atlazdb` (resolve as 9 issues S120).
4. **Refatoração dirigida pelas issues e pelos fluxos de exceção da API**:
   - exceções específicas (`ResourceNotFoundException`, `BusinessRuleException`, `InvalidCredentialsException`) e
     um `@RestControllerAdvice` que devolve um JSON de erro padronizado (`status`, `error`, `message`):
     404, 422, 401, 409 (violação de integridade), 400 (validação/parâmetros), 405 — antes tudo virava 500;
   - Bean Validation nos DTOs de entrada, alinhada às restrições `NOT NULL` do banco;
   - remoção do wildcard, do parâmetro e imports não usados, do código comentado e de DTOs mortos;
   - lógica de consumo médio (duplicada em `DashboardService` e `VehicleService`) extraída para `ConsumptionCalculator`;
   - entidades com `@Getter/@Setter` do Lombok no lugar de getters/setters manuais redundantes, e `lombok.config`
     marcando código gerado com `@Generated` (ignorado pela cobertura);
   - Dockerfile com imagens de versão fixa, cópia explícita de arquivos e execução com usuário não-root.
5. **Testes de unidade** isolados (Mockito, sem contexto Spring) cobrindo todos os branches.
6. **Testes de sistema** com REST-assured contra a aplicação real e PostgreSQL em Testcontainer.

### Defeitos corrigidos durante a refatoração

| Defeito | Correção |
| --- | --- |
| Senha (`passwordHash`) retornada nas respostas de usuários, ordens de serviço e abastecimentos | `@JsonIgnore` na entidade e endpoints de usuário retornando `UserResponseDTO` |
| `PATCH /vehicles/{id}/km-troca-oleo` bloqueado pelo CORS no navegador | `PATCH` incluído nos métodos permitidos |
| Filtros `totalValue`/`currentKm` comparavam `Double` com coluna `BigDecimal` | Tipos alinhados para `BigDecimal` |
| Possíveis `NullPointerException` (usuário sem CNH cadastrada; última viagem sem km de chegada) | Verificações de nulo |
| Atualização duplicada do km da viatura ao encerrar viagem | Chamada única |
| Intervalos de data ignoravam o último segundo do dia final | Fim do intervalo = início do dia seguinte (exclusivo) |
| Exclusão/consulta de registro inexistente retornava 200 vazio | Retorna 404 |

## 3. Testes de sistema (E2E) — matriz de cenários

Todos os cenários validam **código de resposta**, **JSON Schema** (`src/test/resources/schemas`) e **valores
controlados** criados pelo próprio teste por meio da API. O banco é limpo antes de cada teste.

| API | Fluxos principais | Fluxos alternativos | Fluxos de exceção |
| --- | --- | --- | --- |
| Autenticação `/auth/login` | Login de administrador | Login de técnico (perfil `TECNICO`) | Senha errada (401), e-mail inexistente (401), campos vazios (400), JSON malformado (400) |
| Cidades `/cities` | Criar, listar, buscar, atualizar, excluir | Lista vazia | Id inexistente em GET/PUT/DELETE (404), validação (400), JSON malformado (400), id não numérico (400), cidade em uso (409) |
| Modelos `/models` | Criar, listar, buscar, atualizar, excluir | Lista vazia | Id inexistente (404), validação (400), modelo em uso por viatura (409) |
| Usuários `/users` | Criar (status padrão, sem senha na resposta), listar, buscar, atualizar, excluir, listar status | Criar com status informado, manter status quando omitido, desligar técnico sem viagem | Id inexistente (404), e-mail inválido (400), perfil inválido (400), e-mail duplicado (409), desligar técnico em viagem (422) |
| Viaturas `/vehicles` | Criar (valores padrão), listar, buscar, atualizar, excluir (204), listar status, atualizar km de troca de óleo | Criar com valores informados, viaturas disponíveis compatíveis com a CNH, consumo médio sem e com abastecimentos | Id inexistente em GET/PUT/DELETE/PATCH/consumo (404), modelo inexistente (404), campos obrigatórios (400), prefixo duplicado (409), desativar viatura em viagem (422), usuário inexistente (404) |
| Ordens de serviço `/service-orders` | Abrir viagem (viatura `EM_USO`, técnico `EM_CAMPO`), encerrar viagem (libera e atualiza km), consultas por id/usuário/mês, contagem por intervalo, tipos de ocorrência, excluir, exportar CSV | Registrar viagem já encerrada, CSV filtrado por intervalo, mês e categoria | CNH incompatível (422), km menor que a última viagem (422), km menor que o da viatura (422), referências inexistentes (404), campos obrigatórios (400), mês inválido / parâmetro ausente / data inválida (400) |
| Abastecimentos `/refuelings` | Criar vinculado a viagem (km atual = km de chegada), listar, buscar, atualizar, excluir, consulta por mês, contagem por intervalo | Criar sem ordem de serviço, filtros por valor, km e combustível | Usuário/viatura/cidade/OS inexistentes (404), id inexistente (404), litros inválidos e campos obrigatórios (400), mês inválido, combustível inválido e parâmetro ausente (400) |
| Dashboard `/dashboard` | Métricas e atividades do dia | Dashboard vazio | Método não suportado (405), rota inexistente (404) |

O único serviço externo da aplicação é o banco de dados PostgreSQL, isolado com Testcontainers.
