# PRD — Melhorias do Sistema Acadêmico

## 1. Visão geral

Este documento define os requisitos de produto para melhorar a experiência de consulta e gerenciamento de alunos no Sistema Acadêmico. O produto deve facilitar a localização de alunos, permitir administrar seus dados pela interface web e tornar o acompanhamento do desempenho mais claro, sem alterar regras acadêmicas existentes.

Este PRD foi elaborado com base no [plano de melhorias](./plan.md). Os requisitos descrevem o comportamento esperado; não significam que as funcionalidades já estejam implementadas.

## 2. Contexto e problema

Atualmente, a interface permite cadastrar alunos e notas, consultar a lista de alunos e abrir os detalhes de cada aluno. Entretanto, a busca, a edição e a exclusão disponíveis na API não estão acessíveis pelas páginas web. A listagem também não oferece recursos de ordenação ou paginação, e os formulários precisam comunicar validações e resultados de forma consistente.

Como consequência, tarefas comuns de gestão podem exigir navegação desnecessária ou o uso direto da API, e fica mais difícil localizar rapidamente alunos e interpretar seus dados acadêmicos.

## 3. Usuários

- **Pessoa responsável pela gestão acadêmica:** cadastra e atualiza os dados dos alunos, consulta informações e mantém os registros organizados.
- **Pessoa docente ou consultora acadêmica:** consulta dados, notas, médias e situações para acompanhar o desempenho dos alunos.

## 4. Objetivos

1. Permitir localizar alunos com rapidez pela interface.
2. Disponibilizar operações de edição e exclusão com retorno claro e confirmação adequada.
3. Melhorar a clareza e a consistência das validações e mensagens dos formulários.
4. Facilitar a interpretação de médias e situações acadêmicas.
5. Manter a interface utilizável em telas pequenas e por teclado.
6. Oferecer uma visão inicial com indicadores baseados nos dados reais, sem duplicar regras de negócio.

## 5. Fora de escopo

- Alterar as regras de cálculo da média ou de determinação da situação acadêmica.
- Criar novos papéis, autenticação ou permissões de usuário.
- Introduzir novos tipos de registros acadêmicos além dos descritos no plano.
- Definir uma tecnologia ou serviço externo para gráficos e indicadores.
- Tornar obrigatória a paginação antes que o volume de dados a justifique.

## 6. Requisitos funcionais

### RF-01 — Buscar alunos

Na página de listagem, a pessoa usuária deve poder pesquisar alunos pelo nome. A pesquisa deve exibir os resultados correspondentes, preservar o texto consultado e oferecer uma forma de limpar a pesquisa e voltar à lista completa.

**Critérios de aceite**

- Uma pesquisa por nome mostra os alunos correspondentes.
- Uma pesquisa sem correspondências exibe uma mensagem informativa, distinta do estado de lista vazia sem pesquisa.
- O texto consultado permanece visível após a busca.
- Limpar a busca restaura a listagem completa.

### RF-02 — Filtrar por outros campos

Filtros por e-mail e matrícula podem ser incluídos se a camada de serviço der suporte a eles de forma adequada. Caso contrário, permanecem fora da primeira entrega e devem ser tratados como evolução futura.

**Critérios de aceite, se incluídos**

- Os filtros indicam claramente quais campos estão sendo pesquisados.
- É possível remover os filtros e retornar à listagem completa.
- A combinação dos filtros tem comportamento consistente e documentado na interface.

### RF-03 — Editar aluno

A pessoa usuária deve poder iniciar a edição de um aluno a partir da listagem ou de seus detalhes. O formulário deve apresentar os dados atuais e, após salvar com sucesso, a listagem e os detalhes devem refletir as alterações.

**Critérios de aceite**

- O formulário de edição é preenchido com os dados atuais do aluno.
- Dados inválidos não são salvos e são apresentados com mensagens compreensíveis.
- Após a edição bem-sucedida, a pessoa usuária recebe confirmação e vê os dados atualizados.
- A tentativa de editar um aluno inexistente apresenta um resultado apropriado, sem simular sucesso.

### RF-04 — Excluir aluno

A pessoa usuária deve poder solicitar a exclusão de um aluno. Antes de concluir a ação, a interface deve pedir confirmação. Após sucesso, o aluno deve deixar de aparecer na listagem.

**Critérios de aceite**

- A exclusão não ocorre antes da confirmação.
- Cancelar a confirmação mantém o registro.
- Após a exclusão bem-sucedida, a interface atualiza a listagem e informa o resultado.
- Falhas ou registros inexistentes são comunicados sem apresentar uma confirmação falsa de sucesso.

### RF-05 — Ordenar e paginar a listagem

A listagem deve permitir ordenar por nome, matrícula, média e situação. Paginação deve ser adicionada quando o volume de alunos justificar a necessidade. Quando houver paginação, a pesquisa e a ordenação selecionadas devem ser preservadas entre páginas.

**Critérios de aceite**

- A pessoa usuária consegue identificar e alterar o campo de ordenação.
- A ordenação funciona em ambas as direções quando aplicável.
- Se a paginação for implementada, a interface informa a quantidade de resultados e a página atual.
- Navegar entre páginas não remove os filtros ou a ordenação ativos.

### RF-06 — Apresentar desempenho acadêmico

A listagem e os detalhes devem tornar a média e a situação mais fáceis de identificar. A apresentação deve usar rótulos e estilos consistentes e não depender exclusivamente de cores. Os valores devem respeitar as regras acadêmicas existentes e permanecer consistentes com a API.

**Critérios de aceite**

- Média e situação estão visíveis na listagem e nos detalhes.
- A situação é compreensível sem depender da percepção de cor.
- A interface não calcula regras acadêmicas divergentes das regras existentes no serviço/API.

### RF-07 — Validar formulários e comunicar resultados

Os formulários de aluno e nota devem validar campos obrigatórios, formatos e limites definidos pelas regras existentes. Mensagens devem ser apresentadas junto aos campos quando possível. Em caso de erro, os valores informados devem ser preservados quando isso for seguro e viável.

**Critérios de aceite**

- Campos obrigatórios, formato de e-mail, idade e valor da nota são validados conforme as regras existentes.
- Erros identificam o campo e explicam como corrigir o problema.
- A interface não apresenta mensagem de sucesso quando o envio falha.
- Uma operação bem-sucedida recebe confirmação visível.

### RF-08 — Exibir indicadores na página inicial

A página inicial deve apresentar o total real de alunos cadastrados. Indicadores adicionais, como distribuição por situação acadêmica e média geral, podem ser incluídos desde que usem dados reais e as regras existentes. A página deve manter atalhos para consultar e cadastrar alunos.

**Critérios de aceite**

- O total apresentado corresponde aos registros disponíveis.
- Indicadores adicionais, se incluídos, são calculados com dados reais.
- Em caso de ausência de dados, a interface apresenta um estado vazio claro.
- Os atalhos para a listagem e o cadastro continuam disponíveis.

## 7. Requisitos de experiência e qualidade

### UX-01 — Acessibilidade

- Todos os controles devem ter rótulos compreensíveis.
- As ações devem ser acessíveis por teclado e apresentar foco visível.
- Títulos, mensagens e regiões da página devem manter hierarquia semântica.
- Cor não deve ser o único meio de transmitir estado ou desempenho.

### UX-02 — Responsividade

- As páginas devem permanecer utilizáveis em telas estreitas.
- A tabela pode permitir rolagem horizontal, mas ações e informações essenciais devem continuar acessíveis.
- Formulários, botões e mensagens devem manter legibilidade e interação adequada em dispositivos móveis.

### QL-01 — Consistência das regras

Regras de negócio, especialmente cálculo de média, situação acadêmica e limites de notas, devem continuar centralizadas na lógica existente. A interface não deve introduzir uma segunda implementação que possa divergir do serviço ou da API.

### QL-02 — Testabilidade

Cada novo fluxo deve ter testes apropriados. A validação deve incluir os testes existentes e o build configurado para o projeto.

## 8. Métricas de sucesso

- A pessoa usuária consegue localizar um aluno pela busca sem recorrer à API.
- Edição e exclusão podem ser concluídas pela interface com confirmação e retorno visível.
- Nenhum fluxo de erro apresenta estado de sucesso.
- Média e situação são exibidas de forma consistente entre listagem, detalhes e API.
- As funcionalidades principais podem ser usadas por teclado e em telas estreitas.

As métricas podem ser verificadas por testes funcionais e revisão manual; não se presume a existência de telemetria no sistema.

## 9. Dependências e premissas

- A camada de serviço deve ser a fonte de verdade para busca e regras acadêmicas.
- A API já oferece busca por nome, atualização e exclusão; a implementação web deve reutilizar a lógica existente sempre que possível.
- A busca por e-mail/matrícula depende de suporte apropriado na camada de serviço.
- A decisão de introduzir paginação deve considerar o volume esperado de alunos e as capacidades atuais de persistência.
- Indicadores adicionais na página inicial dependem da disponibilidade dos dados necessários e da lógica de cálculo existente.

## 10. Priorização sugerida

1. Busca por nome e estados de resultado da listagem.
2. Edição e exclusão com confirmação e mensagens de retorno.
3. Validação e mensagens nos formulários de aluno e nota.
4. Apresentação acessível da média e situação acadêmica.
5. Ordenação e avaliação da necessidade de paginação.
6. Indicadores reais na página inicial.
7. Revisão geral de acessibilidade, responsividade e consistência visual.

## 11. Questões para validação antes da implementação

- Quais são os limites válidos de idade e de nota, conforme as regras acadêmicas do projeto?
- A exclusão de aluno também deve remover suas notas associadas, ou essa operação deve ser bloqueada quando houver notas?
- O volume esperado de alunos justifica paginação desde a primeira versão?
- Quais indicadores, além do total de alunos, são prioritários para a página inicial?
