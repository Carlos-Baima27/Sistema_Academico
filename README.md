# Sistema Acadêmico

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
3. Envie as alterações para `main`. O Render atualiza o backend e o workflow [deploy-pages.yml](./.github/workflows/deploy-pages.yml) testa o projeto e publica a interface. O workflow também pode ser executado manualmente pela aba **Actions**.

O site ficará disponível em `https://carlos-baima27.github.io/Sistema_Academico/`. A interface está em `site/`; ela consome as rotas REST `/api/alunos` do backend. Sem o backend publicado e acessível por HTTPS, a página pode abrir, mas as operações de alunos e notas não funcionarão.

Para executar a interface localmente, sirva a pasta `site/` por um servidor HTTP e defina `apiBaseUrl` em `site/config.js` para a URL local da API. O navegador também precisa que o backend permita a origem local via `APP_CORS_ALLOWED_ORIGIN`.
