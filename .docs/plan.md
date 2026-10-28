# Plano de melhorias da página do Sistema Acadêmico

## Objetivo

Melhorar a experiência de uso do Sistema Acadêmico, tornando mais simples consultar e administrar alunos, acompanhar o desempenho acadêmico e identificar informações importantes. As melhorias devem aproveitar as funções já disponíveis no projeto sempre que possível e manter a interface responsiva e acessível.

## Situação atual

- A página inicial apresenta o sistema e oferece acesso à listagem e ao cadastro de alunos.
- A listagem apresenta dados dos alunos e um link para consultar detalhes.
- A página de detalhes exibe os dados do aluno e suas notas.
- Existem formulários para cadastrar alunos e notas.
- A API já oferece busca de alunos por nome, atualização e exclusão, mas essas ações ainda não estão integradas à interface web.

## Melhorias propostas

### 1. Busca e filtros de alunos

- Adicionar à listagem um campo de busca para localizar alunos por nome.
- Considerar filtros por e-mail e matrícula, caso sejam suportados adequadamente pela camada de serviço.
- Apresentar uma mensagem clara quando nenhum aluno corresponder à pesquisa.
- Permitir limpar a pesquisa e voltar à listagem completa.
- Preservar o valor pesquisado no campo após a consulta.

### 2. Ações de gerenciamento na listagem

- Adicionar uma ação para editar os dados do aluno.
- Criar ou adaptar um formulário de edição com os dados atuais preenchidos.
- Integrar o formulário web à função de atualização existente na API ou à camada de serviço.
- Adicionar uma ação para excluir o aluno.
- Solicitar confirmação antes da exclusão e apresentar o resultado da operação de forma compreensível.
- Atualizar a listagem após uma edição ou exclusão bem-sucedida.

### 3. Paginação e ordenação

- Permitir ordenar a listagem por nome, matrícula, média e situação.
- Introduzir paginação quando o volume de alunos justificar essa necessidade.
- Manter os filtros e a ordenação ao navegar entre páginas.
- Exibir a quantidade de resultados e a página atual.

### 4. Destaque do desempenho acadêmico

- Tornar a média e a situação mais fáceis de identificar visualmente na listagem e nos detalhes.
- Usar textos e estilos consistentes para representar cada situação acadêmica.
- Não depender somente de cores: combinar cor com texto ou outro indicador acessível.
- Manter as regras de cálculo da média e de determinação da situação centralizadas na lógica existente, evitando divergência entre páginas e API.

### 5. Validação e retorno nos formulários

- Validar campos obrigatórios e formatos antes do envio, incluindo e-mail, idade e valores de notas.
- Exibir mensagens junto aos campos com erro e preservar os valores informados quando possível.
- Apresentar confirmação visual após salvar ou atualizar dados.
- Usar mensagens claras para falhas, como aluno inexistente ou dados inválidos.
- Manter os limites permitidos para notas e demais regras de negócio consistentes com o serviço e a API.

### 6. Painel inicial com indicadores

- Exibir a quantidade total de alunos cadastrados.
- Avaliar a inclusão de indicadores de desempenho, como alunos por situação acadêmica e média geral.
- Oferecer atalhos para as ações mais utilizadas, como consultar alunos e cadastrar um aluno.
- Garantir que os indicadores sejam calculados a partir dos dados reais, sem valores fixos na interface.

### 7. Acessibilidade e responsividade

- Verificar a navegação por teclado, o foco visível e os rótulos dos campos.
- Manter tabelas utilizáveis em telas estreitas, preservando o acesso às ações.
- Usar títulos, mensagens e regiões semânticas com hierarquia adequada.
- Verificar contraste e não transmitir informações apenas por cor.

## Ordem sugerida de implementação

1. Implementar busca por nome e estados de resultado vazio na listagem.
2. Integrar edição e exclusão de alunos, incluindo formulário, confirmação e mensagens de retorno.
3. Melhorar validações e mensagens dos formulários de aluno e nota.
4. Destacar média e situação acadêmica com indicadores acessíveis.
5. Adicionar ordenação e, se necessária para o volume esperado, paginação.
6. Evoluir a página inicial com indicadores reais.
7. Revisar responsividade, acessibilidade e consistência visual em todas as páginas.

## Critérios de aceite

- É possível localizar alunos pela interface e entender quando a busca não retorna resultados.
- É possível editar os dados de um aluno e ver as alterações refletidas na listagem e nos detalhes.
- A exclusão exige confirmação e, após sucesso, o aluno deixa de aparecer na listagem.
- Formulários indicam campos inválidos com mensagens claras e não apresentam sucesso quando a operação falha.
- A média e a situação são apresentadas de forma consistente entre listagem, detalhes e API.
- Ordenação e paginação, quando implementadas, preservam a busca aplicada.
- A interface permanece utilizável em telas pequenas e por navegação via teclado.
- As melhorias não alteram as regras acadêmicas existentes sem validação explícita.

## Observações

- Este documento descreve o plano; não implica que as funcionalidades acima já estejam implementadas.
- Antes de cada etapa, verificar os serviços, modelos e testes existentes para reutilizar regras e evitar duplicação.
- A implementação deve incluir testes para os novos fluxos e executar as verificações de build e testes já configuradas no projeto.
