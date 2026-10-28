# Tarefas — Melhorias do Sistema Acadêmico

Lista de implementação derivada do [PRD](./prd.md). Marque uma tarefa como concluída trocando `[ ]` por `[x]`.

## Etapa 0 — Preparação e regras existentes

- [x] T001 [PRD:RF-03, RF-04, RF-07, QL-01] Revisar e registrar nos testes as regras já existentes de aluno e nota em `src/main/java/com/cursojava/sistemaacademico/service/AlunoService.java` (idade maior que zero e nota entre 0 e 10).
- [x] T002 [PRD:RF-04] Verificar em `src/main/java/com/cursojava/sistemaacademico/model/Aluno.java` e nos testes se excluir um aluno remove também suas notas; definir o comportamento esperado antes de integrar a exclusão pela interface.
- [x] T003 [PRD:RF-02, RF-05, RF-08] Confirmar o escopo inicial dos filtros por e-mail/matrícula, da paginação e dos indicadores adicionais; manter como evolução os itens não priorizados.

## Etapa 1 — Busca de alunos (RF-01)

- [ ] T004 [PRD:RF-01] Atualizar a rota `GET /alunos` em `src/main/java/com/cursojava/sistemaacademico/controller/AlunoController.java` para aceitar uma pesquisa opcional por nome e enviar a consulta e os resultados à página.
- [ ] T005 [PRD:RF-01] Reutilizar `AlunoService.buscarPorNome` em `src/main/java/com/cursojava/sistemaacademico/service/AlunoService.java`, tratando pesquisa vazia conforme o comportamento definido para a listagem completa.
- [ ] T006 [PRD:RF-01] Adicionar campo de pesquisa, botão para pesquisar e ação para limpar a consulta em `src/main/resources/templates/alunos.html`.
- [ ] T007 [PRD:RF-01] Diferenciar em `src/main/resources/templates/alunos.html` a lista sem alunos cadastrados da pesquisa sem resultados, preservando o texto pesquisado.
- [ ] T008 [PRD:RF-01, QL-02] Criar ou atualizar testes do controlador para pesquisa com resultados, sem resultados, consulta vazia e preservação do termo.

## Etapa 2 — Edição de alunos (RF-03)

- [ ] T009 [PRD:RF-03] Criar a rota `GET /alunos/{id}/editar` em `src/main/java/com/cursojava/sistemaacademico/controller/AlunoController.java`, carregando o aluno e tratando ID inexistente sem apresentar sucesso.
- [ ] T010 [PRD:RF-03] Adaptar `src/main/resources/templates/aluno-form.html` para funcionar como formulário de cadastro e edição, preenchendo os valores atuais e usando título, ação e botão adequados a cada modo.
- [ ] T011 [PRD:RF-03, RF-07] Criar a rota `POST /alunos/{id}` em `src/main/java/com/cursojava/sistemaacademico/controller/AlunoController.java` para validar, atualizar e redirecionar após sucesso.
- [ ] T012 [PRD:RF-03] Adicionar links para editar na listagem `src/main/resources/templates/alunos.html` e nos detalhes `src/main/resources/templates/aluno-detalhes.html`.
- [ ] T013 [PRD:RF-03, QL-02] Testar edição bem-sucedida, dados inválidos e tentativa de editar aluno inexistente.

## Etapa 3 — Exclusão de alunos (RF-04)

- [ ] T014 [PRD:RF-04] Criar uma rota web `POST` para excluir aluno em `src/main/java/com/cursojava/sistemaacademico/controller/AlunoController.java`, reutilizando `AlunoService.excluirAluno` e tratando registros inexistentes.
- [ ] T015 [PRD:RF-04] Adicionar ação de exclusão à listagem em `src/main/resources/templates/alunos.html`, usando envio `POST` e solicitando confirmação antes de enviar.
- [ ] T016 [PRD:RF-04] Exibir confirmação de sucesso ou erro após exclusão e assegurar que a listagem seja atualizada.
- [ ] T017 [PRD:RF-04, QL-02] Testar exclusão confirmada, cancelamento no navegador, ID inexistente e comportamento das notas associadas.

## Etapa 4 — Validação e retorno dos formulários (RF-07)

- [ ] T018 [PRD:RF-07, QL-01] Aplicar ao cadastro web de aluno as validações de `AlunoService.dadosInvalidos` em `src/main/java/com/cursojava/sistemaacademico/controller/AlunoController.java`.
- [ ] T019 [PRD:RF-07, QL-01] Aplicar ao cadastro web de nota as validações de `AlunoService.notaInvalida` em `src/main/java/com/cursojava/sistemaacademico/controller/AlunoController.java`.
- [ ] T020 [PRD:RF-07] Ajustar os atributos HTML de idade, e-mail e nota em `src/main/resources/templates/aluno-form.html` e `src/main/resources/templates/nota-form.html` para refletir os limites definidos pelo serviço.
- [ ] T021 [PRD:RF-07] Exibir mensagens junto aos campos inválidos e preservar os valores informados ao retornar aos formulários `src/main/resources/templates/aluno-form.html` e `src/main/resources/templates/nota-form.html`.
- [ ] T022 [PRD:RF-03, RF-07] Apresentar mensagens de sucesso e erro nos fluxos de cadastro, edição e nota sem informar sucesso quando houver falha.
- [ ] T023 [PRD:RF-07, QL-02] Testar validações, mensagens e ausência de falsos retornos de sucesso nos formulários de aluno e nota.

## Etapa 5 — Destaque do desempenho acadêmico (RF-06)

- [ ] T024 [PRD:RF-06, QL-01] Aplicar estilos consistentes à média e à situação na listagem `src/main/resources/templates/alunos.html` e nos detalhes `src/main/resources/templates/aluno-detalhes.html`, usando os valores calculados pelo modelo.
- [ ] T025 [PRD:RF-06, UX-01] Ajustar `src/main/resources/static/css/style.css` para diferenciar visualmente as situações sem depender exclusivamente de cores.
- [ ] T026 [PRD:RF-06, QL-02] Verificar em testes ou revisão funcional que a apresentação de média e situação corresponde aos valores existentes na API e no modelo.

## Etapa 6 — Ordenação e paginação (RF-05)

- [ ] T027 [PRD:RF-05] Definir, com base no volume esperado e na persistência disponível, se a paginação faz parte da primeira entrega.
- [ ] T028 [PRD:RF-05] Implementar ordenação por nome, matrícula, média e situação na rota `GET /alunos` em `src/main/java/com/cursojava/sistemaacademico/controller/AlunoController.java`, preservando a pesquisa ativa.
- [ ] T029 [PRD:RF-05] Adicionar controles de ordenação e indicação do critério atual em `src/main/resources/templates/alunos.html`.
- [ ] T030 [PRD:RF-05] Se a paginação for aprovada em T027, implementar navegação e quantidade de resultados na listagem, preservando pesquisa e ordenação.
- [ ] T031 [PRD:RF-05, QL-02] Testar ordenação, navegação entre páginas quando aplicável e preservação dos parâmetros de consulta.

## Etapa 7 — Indicadores na página inicial (RF-08)

- [ ] T032 [PRD:RF-08] Disponibilizar à rota inicial em `src/main/java/com/cursojava/sistemaacademico/controller/AlunoController.java` a quantidade real de alunos usando a camada de serviço.
- [ ] T033 [PRD:RF-08] Exibir o total de alunos e um estado apropriado para lista vazia em `src/main/resources/templates/index.html`, mantendo os atalhos para consulta e cadastro.
- [ ] T034 [PRD:RF-08] Se indicadores adicionais forem priorizados em T003, disponibilizar seus dados pela camada de serviço e apresentá-los em `src/main/resources/templates/index.html` sem duplicar regras acadêmicas.
- [ ] T035 [PRD:RF-08, QL-02] Testar o total e os estados da página inicial, incluindo ausência de alunos e indicadores adicionais quando implementados.

## Etapa 8 — Acessibilidade, responsividade e validação final

- [ ] T036 [PRD:UX-01] Revisar rótulos, hierarquia de títulos, mensagens semânticas e foco visível nas páginas `src/main/resources/templates/index.html`, `src/main/resources/templates/alunos.html`, `src/main/resources/templates/aluno-detalhes.html`, `src/main/resources/templates/aluno-form.html` e `src/main/resources/templates/nota-form.html`.
- [ ] T037 [PRD:UX-02] Verificar em `src/main/resources/static/css/style.css` e nas páginas alteradas a legibilidade em telas estreitas e o acesso às ações da tabela.
- [ ] T038 [PRD:UX-01, UX-02, QL-02] Validar busca, edição, exclusão, formulários, desempenho acadêmico e página inicial por teclado e em viewport móvel.
- [ ] T039 [PRD:QL-02] Executar os testes automatizados e o build configurados no projeto; corrigir falhas introduzidas pelas melhorias.

## Mapeamento de requisitos

| Requisito | Tarefas |
|---|---|
| RF-01 — Buscar alunos | T004–T008 |
| RF-02 — Filtrar por outros campos | T003 |
| RF-03 — Editar aluno | T001, T009–T013, T018, T022 |
| RF-04 — Excluir aluno | T002, T014–T017 |
| RF-05 — Ordenar e paginar | T003, T027–T031 |
| RF-06 — Apresentar desempenho acadêmico | T024–T026 |
| RF-07 — Validar formulários e comunicar resultados | T001, T018–T023 |
| RF-08 — Exibir indicadores na página inicial | T003, T032–T035 |
| UX-01 — Acessibilidade | T025, T036, T038 |
| UX-02 — Responsividade | T037–T038 |
| QL-01 — Consistência das regras | T001, T005, T018–T020, T024, T034 |
| QL-02 — Testabilidade | T008, T013, T017, T023, T026, T031, T035, T038–T039 |

## Observações

- Decisões da Etapa 0: a primeira entrega inclui busca por nome, mas não filtros por e-mail/matrícula, que ficam como evolução; paginação será adiada até que o volume de alunos a justifique; a página inicial terá o total real de alunos, enquanto indicadores adicionais ficam como evolução.
- O comportamento esperado ao excluir um aluno é remover também suas notas associadas. `Aluno.notas` configura `CascadeType.ALL`; o teste de integração verifica a exclusão em cascata. O identificador de `Nota` foi ajustado para `Long` gerado, compatível com `NotaRepository`.
- Os testes registram os limites atuais sem alterá-los: idade maior que zero e nota de 0 a 10, inclusive.
- As tarefas condicionais dependem das decisões indicadas em T003 e T027.
- Os limites citados devem ser confirmados na implementação existente antes de serem alterados; este backlog não propõe mudar regras acadêmicas.
- Marcar uma tarefa como concluída somente após implementar e verificar seu resultado.
