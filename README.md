# 🐾 Clyvo Vet — Backend Java (Spring Boot)

Backend do projeto **Clyvo Vet**, desenvolvido para o Challenge FIAP 2026. Substitui a API
de desenvolvimento que o app mobile (React Native / Expo) já consumia, mantendo o **contrato
de API imutável** definido junto com a squad de Mobile.

## Integrantes

| RM | Nome |
|---|---|
| 562156 | Pedro Henrique dos Santos Costa |
| 565269 | Eduardo Augusto de Oliveira Souza |
| 564673 | Fellipe Costa de Oliveira |
| 563009 | Felype Ferreira Maschio |
| 563304 | Gustavo Vieira de Matos |

## O que a solução resolve

O app mobile Clyvo Vet ajuda tutores a cadastrar seus pets, agendar consultas veterinárias e
acompanhar a carteira de vacinação. Este backend:

- **Substitui a API de testes do app** por uma API própria, em Java/Spring, sem exigir
  nenhuma reescrita do app mobile (o contrato de `/api/v1/**` foi mantido 100% compatível).
- **Adiciona um painel web para tutores e veterinários** (Thymeleaf), com dashboard,
  gestão de pets/consultas e uma agenda dedicada para o veterinário.
- **Automatiza o atendimento veterinário**: ao concluir uma consulta, o prontuário é gravado,
  o peso do pet é atualizado e — se houver retorno — a próxima consulta é criada sozinha.
- **Cria alertas de vacinação**: cada dose é classificada como em dia, próxima ou atrasada,
  com o total de pendências visível direto no dashboard.

## Stack

- Java 21, Spring Boot 3.3, Maven
- Spring Web, Spring Data JPA, Bean Validation, Spring Security, Thymeleaf
- Flyway (migrations versionadas) + PostgreSQL
- Firebase Admin SDK (validação do token do app mobile)
- Lombok

## Arquitetura de segurança

Duas cadeias de segurança independentes (`SecurityFilterChain` com `@Order`):

| Cadeia | Rotas | Autenticação | Consumida por |
|---|---|---|---|
| API | `/api/v1/**` | Bearer token do Firebase, stateless | App mobile |
| Web | demais rotas | Form login + sessão, `ROLE_TUTOR` / `ROLE_VETERINARIO` | Frontend Thymeleaf |

O `tutorId` do corpo das requisições da API **nunca** é usado — o backend sempre extrai o
`uid` do token Firebase e o usa como fonte da verdade.

## Como rodar localmente

1. Suba um banco PostgreSQL (local ou em nuvem) e um projeto Firebase (para o Admin SDK).
2. Copie `src/main/resources/application-example.properties` e ajuste os valores (ou exporte as
   variáveis de ambiente abaixo direto no seu terminal/IDE):

   ```bash
   export DB_URL=jdbc:postgresql://localhost:5432/clyvovet
   export DB_USER=postgres
   export DB_PASSWORD=postgres
   export FIREBASE_CREDENTIALS=/caminho/para/serviceAccountKey.json
   ```

   > **Local vs. produção**: `FIREBASE_CREDENTIALS` aceita **dois formatos**, e o
   > `FirebaseConfig` detecta qual foi usado sozinho — primeiro tenta abrir o valor como
   > caminho de arquivo; se não existir, trata o próprio valor como o JSON.
   > - **Local**: use um caminho de arquivo, como no exemplo acima.
   > - **Render (ou qualquer PaaS sem upload de arquivo)**: cole o **conteúdo inteiro**
   >   do `.json` da service account direto no valor da variável — não um caminho, já
   >   que o arquivo não existe dentro do container.

3. Rode a aplicação:

   ```bash
   ./mvnw spring-boot:run
   ```

4. O Flyway cria o schema e a carga inicial automaticamente na primeira subida
   (`ddl-auto: validate` — o schema é sempre responsabilidade das migrations).
5. Acesse `http://localhost:8080` para o painel web, ou aponte o app mobile para
   `http://localhost:8080/api/v1`.

### Usuários de teste (carga inicial do Flyway)

| E-mail | Perfil |
|---|---|
| ana.lima@example.com | Tutor |
| marina.alves@clyvovet.com | Veterinário |

> A senha da carga inicial é um hash de exemplo — crie sua própria conta pela tela de
> cadastro (`/cadastro`) para testar o login web de ponta a ponta.

## Endpoints da API (`/api/v1`)

Todas exigem `Authorization: Bearer <firebase-id-token>`.

| Método | Rota | Retorno |
|---|---|---|
| GET | `/api/v1/pets?tutorId={uid}` | `200` + array (nunca 404) |
| GET | `/api/v1/pets/{id}` | `200` + objeto, ou `404` |
| POST | `/api/v1/pets` | `201` + objeto criado |
| PUT | `/api/v1/pets/{id}` | `200` + objeto atualizado |
| DELETE | `/api/v1/pets/{id}` | `204` sem corpo |
| GET/POST/PUT/DELETE | `/api/v1/consultas...` | mesma semântica de `/pets` |

Veja a coleção completa em [`testes/clyvovet.http`](testes/clyvovet.http).

## Estrutura do projeto

```
src/main/java/br/com/fiap/clyvovet/
├── config/          SecurityConfig, FirebaseConfig, CorsConfig, WebConfig
├── control/api    PetApiController, ConsultaApiController (REST — app mobile)
├── control/web    DashboardController, PetWebController, ConsultaWebController,
│                     VetController, AuthController (Thymeleaf)
├── model              Tutor, Pet, Consulta, Prontuario, Vacina, Especie, Sexo,
│                       StatusConsulta, Perfil, StatusVacina (entidades + enums, sem Lombok)
├── dto/request | response
├── mapper
├── repository
├── service            TutorService, PetService, ConsultaService,
│                       AtendimentoService (fluxo A), VacinacaoService (fluxo B)
├── security            FirebaseTokenFilter, FirebaseAuthenticationToken,
│                        UsuarioDetailsService, TutorAtual
└── exception           GlobalExceptionHandler (API), WebExceptionHandler (web)
```

## Fluxos de negócio implementados

**Fluxo A — Atendimento veterinário.** O veterinário abre `/vet/agenda`, seleciona uma
consulta `agendada` e preenche o prontuário. Em uma única transação: o prontuário é gravado,
a consulta vira `concluida`, o peso do pet é atualizado e — se houver retorno — uma nova
consulta `agendada` é criada automaticamente.

**Fluxo B — Carteira de vacinação.** Cada dose é classificada em `em_dia` (mais de 30 dias
até a próxima), `proxima` (até 30 dias) ou `atrasada` (data já passada). O dashboard soma as
pendências de todos os pets do tutor. Ao registrar uma nova dose, a próxima é calculada pelo
intervalo do tipo de vacina.

## Vídeo

_(link do vídeo de demonstração aqui)_
