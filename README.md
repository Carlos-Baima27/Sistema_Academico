# Sistema Acadêmico

Aplicação web para gerenciamento de alunos e acompanhamento do desempenho acadêmico. A interface permite consultar e pesquisar alunos, organizar a listagem e administrar cadastros e notas.

**Acesse a aplicação:** [Sistema Acadêmico](https://carlos-baima27.github.io/Sistema_Academico/)

## Funcionalidades

- Cadastro, consulta, edição e exclusão de alunos.
- Geração automática de matrículas anuais no formato `AAAA-0001`.
- Pesquisa de alunos por nome e ordenação por nome, matrícula, média ou situação acadêmica.
- Cadastro e consulta de notas por aluno.
- Cálculo e apresentação da média e da situação acadêmica.
- Validação de dados nos formulários e na API REST.

## Arquitetura

O projeto é composto por uma interface estática em `site/` e um backend em Java com Spring Boot. A interface é publicada no GitHub Pages; o backend disponibiliza a API REST e utiliza PostgreSQL na hospedagem. Em ambiente local, a configuração padrão utiliza H2.

O workflow do GitHub Actions executa os testes do backend e publica a interface no GitHub Pages após alterações na branch `main`. O backend e a persistência são definidos no Blueprint do Render (`render.yaml`).

As matrículas são geradas pelo backend no formato `AAAA-0001`, com sequência reiniciada a cada ano. Na primeira inicialização após a atualização, as matrículas já existentes são renumeradas em ordem de ID; os IDs dos alunos e suas notas são preservados.

## Tecnologias

- Java 25 e Spring Boot
- Spring MVC, Spring Data JPA e Thymeleaf
- H2 para desenvolvimento local e PostgreSQL para hospedagem
- HTML, CSS e JavaScript
- Maven e GitHub Actions

## Executar localmente

### Requisitos

- JDK 25
- Git

### Backend

Na raiz do projeto, inicie a aplicação:

```powershell
.\mvnw.cmd spring-boot:run
```

O backend ficará disponível em `http://localhost:8080`. Com a configuração padrão, o banco H2 é criado localmente em `data/`.

### Interface estática

A interface requer um servidor HTTP estático e uma API acessível pelo navegador. Para desenvolvimento local, configure `site/config.js` com a URL da API local:

```js
window.APP_CONFIG = { apiBaseUrl: "http://localhost:8080" };
```

Sirva os arquivos da pasta `site/` por um servidor HTTP. O backend precisa permitir a origem usada pelo servidor da interface; a propriedade `APP_CORS_ALLOWED_ORIGIN` configura essa origem.

## Testes

Execute a suíte automatizada e a verificação do projeto:

```powershell
.\mvnw.cmd --batch-mode clean verify
```

## API REST

A API está sob o prefixo `/api/alunos`. Entre os recursos disponíveis estão:

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/alunos` | Lista os alunos |
| `GET` | `/api/alunos/{id}` | Consulta um aluno |
| `GET` | `/api/alunos/busca?nome={nome}` | Pesquisa alunos por nome |
| `POST` | `/api/alunos` | Cadastra um aluno |
| `PUT` | `/api/alunos/{id}` | Atualiza um aluno |
| `DELETE` | `/api/alunos/{id}` | Exclui um aluno |
| `GET` | `/api/alunos/{id}/notas` | Lista as notas do aluno |
| `POST` | `/api/alunos/{id}/notas` | Cadastra uma nota |
| `GET` | `/api/alunos/{id}/media` | Consulta média e situação acadêmica |

## Publicação

O site público está hospedado no GitHub Pages. A interface depende da API hospedada separadamente; portanto, as operações de consulta e manutenção de dados requerem que o backend e o banco estejam disponíveis. A variável `API_BASE_URL` do workflow define o endereço base da API usado na publicação da interface.
