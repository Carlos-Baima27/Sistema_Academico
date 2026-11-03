# Sistema Acadêmico

Sistema para cadastro e acompanhamento de alunos, com pesquisa, ordenação, edição, exclusão e registro de notas.

**Acesse o sistema:** [Sistema Acadêmico no GitHub Pages](https://carlos-baima27.github.io/Sistema_Academico/)

## Publicação no GitHub Pages + Render

A interface estática é publicada no GitHub Pages e a API Spring Boot/PostgreSQL é publicada no Render. O arquivo [Dockerfile](./Dockerfile) prepara o backend, e [render.yaml](./render.yaml) descreve o serviço e o banco de demonstração.

### Criar o backend no Render

1. Envie este projeto para a branch `main` do GitHub.
2. No Render, escolha **New > Blueprint**, conecte o repositório `Carlos-Baima27/Sistema_Academico` e confirme a localização de `render.yaml`.
3. Revise e aplique o Blueprint. Ele cria o serviço `sistema-academico-api` e um PostgreSQL gratuito para demonstração; não cria recursos até você confirmar no Render.
4. Quando o deploy terminar, abra o serviço e copie sua URL pública HTTPS. O serviço pode levar alguns instantes para responder após ficar inativo.

**Limitações do plano gratuito:** serviços podem entrar em suspensão por inatividade, e o banco gratuito é temporário (expira após 30 dias); não use essa configuração para dados importantes. Para manter dados, escolha um plano de PostgreSQL persistente no Render e confirme os custos antes de aplicar o Blueprint.

### Publicar a interface no GitHub Pages

1. No GitHub, abra **Settings > Secrets and variables > Actions > Variables** e crie a variável `API_BASE_URL` com a URL HTTPS do serviço Render, sem `/api` no final.
2. Em **Settings > Pages**, selecione **GitHub Actions** como fonte de publicação.
3. Envie as alterações para `main`. O workflow [deploy-pages.yml](./.github/workflows/deploy-pages.yml) testa o projeto e publica a interface no Pages. O workflow também pode ser executado manualmente pela aba **Actions**.

O site está disponível em [carlos-baima27.github.io/Sistema_Academico](https://carlos-baima27.github.io/Sistema_Academico/). A interface está em `site/`; ela consome as rotas REST `/api/alunos` do backend. Sem o backend publicado e acessível por HTTPS, a página pode abrir, mas as operações de alunos e notas não funcionarão.

O serviço Render foi criado usando o repositório público. Nesse modo, o deploy do backend não é atualizado automaticamente quando há novos pushes; faça o deploy manualmente no Render ou configure a integração com o GitHub. A publicação do Pages continua automatizada pelo workflow.

Para executar a interface localmente, sirva a pasta `site/` por um servidor HTTP e defina `apiBaseUrl` em `site/config.js` para a URL local da API. O navegador também precisa que o backend permita a origem local via `APP_CORS_ALLOWED_ORIGIN`.
