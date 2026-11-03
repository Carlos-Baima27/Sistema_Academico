const app = document.querySelector("#app");
const apiBaseUrl = (window.APP_CONFIG?.apiBaseUrl || "").replace(/\/+$/, "");

const escapeHtml = (value) => String(value ?? "").replace(/[&<>"']/g, (character) => ({
  "&": "&amp;",
  "<": "&lt;",
  ">": "&gt;",
  '"': "&quot;",
  "'": "&#39;",
})[character]);

const formatNumber = (value) => Number(value || 0).toLocaleString("pt-BR", {
  minimumFractionDigits: 1,
  maximumFractionDigits: 2,
});

async function request(path, options = {}) {
  if (!apiBaseUrl) {
    throw new Error("A URL da API ainda não foi configurada. Defina a variável API_BASE_URL nas variáveis do GitHub Actions e execute o workflow novamente.");
  }

  let response;
  try {
    response = await fetch(`${apiBaseUrl}${path}`, {
      ...options,
      headers: {
        ...(options.body ? { "Content-Type": "application/json" } : {}),
        ...options.headers,
      },
    });
  } catch {
    throw new Error("Não foi possível conectar à API. Verifique se o backend está no ar e se o CORS permite este site.");
  }

  if (!response.ok) {
    if (response.status === 404) {
      throw new Error("O registro solicitado não foi encontrado.");
    }
    if (response.status === 400) {
      throw new Error("Os dados informados são inválidos. Confira os campos e tente novamente.");
    }
    throw new Error(`A API retornou um erro (${response.status}). Tente novamente mais tarde.`);
  }

  return response.status === 204 ? null : response.json();
}

function feedback(message, type = "error") {
  return `<div class="feedback feedback-${type}" role="${type === "error" ? "alert" : "status"}">${escapeHtml(message)}</div>`;
}

function heading(eyebrow, title, description = "") {
  return `<section class="page-heading"><div class="page-heading-copy">
    <p class="eyebrow">${escapeHtml(eyebrow)}</p>
    <h1>${escapeHtml(title)}</h1>
    ${description ? `<p class="page-description">${escapeHtml(description)}</p>` : ""}
  </div></section>`;
}

function statusBadge(situacao) {
  const styles = {
    Aprovado: ["status-approved", "✓"],
    Reprovado: ["status-failed", "×"],
    "Sem notas": ["status-no-grades", "—"],
  };
  const [className, icon] = styles[situacao] || styles["Sem notas"];
  return `<span class="status-badge ${className}"><span class="status-icon" aria-hidden="true">${icon}</span>
    <span>${escapeHtml(situacao || "Sem notas")}</span></span>`;
}

function studentRows(alunos) {
  return alunos.map((aluno) => `<tr>
    <td>${escapeHtml(aluno.id)}</td>
    <td>${escapeHtml(aluno.nome)}</td>
    <td>${escapeHtml(aluno.email)}</td>
    <td>${escapeHtml(aluno.matricula)}</td>
    <td>${escapeHtml(aluno.idade)}</td>
    <td><span class="academic-average">${formatNumber(aluno.media)}</span></td>
    <td>${statusBadge(aluno.situacao)}</td>
    <td class="actions-cell">
      <a href="#/alunos/${encodeURIComponent(aluno.id)}">Detalhes</a>
      <a href="#/alunos/${encodeURIComponent(aluno.id)}/editar">Editar</a>
      <button class="button button-danger" type="button" data-delete="${escapeHtml(aluno.id)}"
        data-name="${escapeHtml(aluno.nome)}" aria-label="Excluir aluno ${escapeHtml(aluno.nome)}">Excluir</button>
    </td>
  </tr>`).join("");
}

function sortStudents(alunos, field, direction) {
  const comparators = {
    nome: (a, b) => (a.nome || "").localeCompare(b.nome || "", "pt-BR", { sensitivity: "base" }),
    matricula: (a, b) => (a.matricula || "").localeCompare(b.matricula || "", "pt-BR", { sensitivity: "base" }),
    media: (a, b) => Number(a.media || 0) - Number(b.media || 0),
    situacao: (a, b) => (a.situacao || "").localeCompare(b.situacao || "", "pt-BR", { sensitivity: "base" }),
  };
  const compare = comparators[field] || comparators.nome;
  return [...alunos].sort((a, b) => {
    const result = compare(a, b) || Number(a.id) - Number(b.id);
    return direction === "desc" ? -result : result;
  });
}

async function renderHome() {
  const alunos = await request("/api/alunos");
  return `<section class="hero">
    <div class="hero-copy">
      <p class="eyebrow">Gestão acadêmica</p>
      <h1>Acompanhe cada etapa da jornada dos seus alunos.</h1>
      <p class="page-description">Organize cadastros, consulte informações e mantenha as notas em um só lugar.</p>
      <div class="page-actions">
        <a class="button" href="#/alunos">Ver alunos</a>
        <a class="button button-secondary" href="#/alunos/novo">Cadastrar aluno</a>
      </div>
    </div>
    <aside class="hero-card" aria-label="Visão geral do sistema">
      <div class="hero-card-mark" aria-hidden="true">SA</div>
      <p class="hero-card-label">Tudo em ordem</p>
      <h2>Informação clara para decisões melhores.</h2>
      <div class="stats-grid"><div class="stat-card">
        <span class="stat-label">Total de alunos</span><strong class="stat-value">${alunos.length}</strong>
      </div></div>
      ${alunos.length === 0 ? '<div class="empty-state empty-state-inline"><p>Nenhum aluno cadastrado ainda.</p></div>' : ""}
    </aside>
  </section>`;
}

async function renderStudents(searchParams) {
  const alunos = await request("/api/alunos");
  const search = searchParams.get("nome") || "";
  const field = searchParams.get("ordenarPor") || "nome";
  const direction = searchParams.get("direcao") === "desc" ? "desc" : "asc";
  const filtered = alunos.filter((aluno) => (aluno.nome || "").toLocaleLowerCase("pt-BR")
    .includes(search.trim().toLocaleLowerCase("pt-BR")));
  const sorted = sortStudents(filtered, field, direction);
  const fieldNames = { nome: "nome", matricula: "matrícula", media: "média", situacao: "situação" };
  const orderName = direction === "desc" ? "decrescente" : "crescente";

  return `${heading("Gestão acadêmica", "Alunos cadastrados", "Consulte os dados e o desempenho dos alunos.")}
    <section class="surface" aria-label="Lista de alunos">
      <form class="search-form" id="search-form">
        <div class="form-field form-field-full">
          <label for="pesquisa-nome">Pesquisar por nome</label>
          <input id="pesquisa-nome" name="nome" type="search" value="${escapeHtml(search)}" />
        </div>
        <div class="form-grid">
          <div class="form-field"><label for="ordenar-por">Ordenar por</label>
            <select id="ordenar-por" name="ordenarPor">
              ${["nome", "matricula", "media", "situacao"].map((item) => `<option value="${item}" ${field === item ? "selected" : ""}>${{ nome: "Nome", matricula: "Matrícula", media: "Média", situacao: "Situação" }[item]}</option>`).join("")}
            </select>
          </div>
          <div class="form-field"><label for="direcao">Direção</label>
            <select id="direcao" name="direcao">
              <option value="asc" ${direction === "asc" ? "selected" : ""}>Crescente</option>
              <option value="desc" ${direction === "desc" ? "selected" : ""}>Decrescente</option>
            </select>
          </div>
        </div>
        <div class="form-actions"><button class="button" type="submit">Aplicar</button>
          ${search ? '<a class="button button-secondary" href="#/alunos">Limpar pesquisa</a>' : ""}
          <a class="button" href="#/alunos/novo">Cadastrar novo aluno</a>
        </div>
      </form>
      <p class="page-description sort-description" aria-live="polite">Ordenação atual:
        <strong>${escapeHtml(fieldNames[field] || "nome")} (${orderName})</strong></p>
      ${sorted.length === 0 ? `<div class="empty-state"><h2>${search ? "Nenhum aluno encontrado" : "Nenhum aluno cadastrado"}</h2>
        <p>${search ? `Não encontramos alunos com o nome <strong>${escapeHtml(search)}</strong>.` : "Cadastre o primeiro aluno para começar a acompanhar o desempenho acadêmico."}</p></div>` : `
        <div class="table-wrapper" role="region" aria-label="Tabela de alunos" tabindex="0"><table>
          <caption>Alunos cadastrados</caption><thead><tr>
            <th scope="col">ID</th><th scope="col">Nome</th><th scope="col">E-mail</th>
            <th scope="col">Matrícula</th><th scope="col">Idade</th><th scope="col">Média</th>
            <th scope="col">Situação</th><th scope="col">Ações</th>
          </tr></thead><tbody>${studentRows(sorted)}</tbody>
        </table></div>`}
    </section>
    <div class="page-actions"><a class="button button-secondary" href="#/">Voltar ao início</a></div>`;
}

async function renderStudentForm(id) {
  const editing = Boolean(id);
  const aluno = editing ? await request(`/api/alunos/${encodeURIComponent(id)}`) : {
    nome: "", email: "", matricula: "", idade: "",
  };
  return `${heading(editing ? "Edição" : "Cadastro", editing ? "Editar aluno" : "Novo aluno",
    editing ? "Atualize os dados do aluno." : "Preencha os dados para adicionar um aluno ao sistema.")}
    <form class="surface form-card" id="student-form" data-id="${editing ? escapeHtml(id) : ""}">
      <div class="form-grid">
        <div class="form-field form-field-full"><label for="nome">Nome completo</label>
          <input id="nome" name="nome" type="text" autocomplete="name" value="${escapeHtml(aluno.nome)}" required /></div>
        <div class="form-field form-field-full"><label for="email">E-mail</label>
          <input id="email" name="email" type="email" autocomplete="email" value="${escapeHtml(aluno.email)}" required /></div>
        <div class="form-field"><label for="matricula">Matrícula</label>
          <input id="matricula" type="text" value="${editing ? escapeHtml(aluno.matricula) : "Gerada após o cadastro"}" readonly />
          <p class="field-hint">${editing ? "A matrícula é gerada automaticamente e não pode ser alterada." : "Será gerada automaticamente no formato AAAA-0001."}</p></div>
        <div class="form-field"><label for="idade">Idade</label>
          <input id="idade" name="idade" type="number" min="1" value="${escapeHtml(aluno.idade)}" required /></div>
      </div>
      <div class="form-actions"><button type="submit">${editing ? "Salvar alterações" : "Cadastrar aluno"}</button>
        <a class="button button-secondary" href="#/alunos">Cancelar</a></div>
    </form>`;
}

async function renderStudent(id) {
  const aluno = await request(`/api/alunos/${encodeURIComponent(id)}`);
  const notas = aluno.notas || [];
  return `${heading("Perfil acadêmico", "Detalhes do aluno", aluno.nome)}
    <div class="page-heading"><span></span><a class="button" href="#/alunos/${encodeURIComponent(id)}/editar">Editar aluno</a></div>
    <section class="surface student-summary" aria-labelledby="student-data-title"><h2 id="student-data-title">Dados do aluno</h2>
      <dl class="detail-grid">
        <div class="detail-item"><dt>ID</dt><dd>${escapeHtml(aluno.id)}</dd></div>
        <div class="detail-item"><dt>Nome</dt><dd>${escapeHtml(aluno.nome)}</dd></div>
        <div class="detail-item"><dt>E-mail</dt><dd>${escapeHtml(aluno.email)}</dd></div>
        <div class="detail-item"><dt>Matrícula</dt><dd>${escapeHtml(aluno.matricula)}</dd></div>
        <div class="detail-item"><dt>Idade</dt><dd>${escapeHtml(aluno.idade)}</dd></div>
        <div class="detail-item"><dt>Média</dt><dd><span class="academic-average">${formatNumber(aluno.media)}</span></dd></div>
        <div class="detail-item"><dt>Situação</dt><dd>${statusBadge(aluno.situacao)}</dd></div>
      </dl>
    </section>
    <section class="page-heading"><div class="page-heading-copy"><p class="eyebrow">Desempenho</p>
      <h2>Notas</h2><p class="page-description">Acompanhe as avaliações cadastradas para este aluno.</p></div>
      <a class="button" href="#/alunos/${encodeURIComponent(id)}/notas/nova">Cadastrar nota</a></section>
    <section class="surface" aria-label="Notas do aluno">
      ${notas.length === 0 ? '<div class="empty-state"><h2>Nenhuma nota cadastrada</h2><p>As avaliações deste aluno aparecerão aqui.</p></div>' : `
      <div class="table-wrapper" role="region" aria-label="Tabela de notas do aluno" tabindex="0"><table>
        <caption>Notas do aluno</caption><thead><tr><th scope="col">Descrição</th><th scope="col">Valor</th></tr></thead>
        <tbody>${notas.map((nota) => `<tr><td>${escapeHtml(nota.descricao)}</td><td>${formatNumber(nota.valor)}</td></tr>`).join("")}</tbody>
      </table></div>`}
    </section>
    <div class="page-actions"><a class="button button-secondary" href="#/alunos">Voltar para alunos</a></div>`;
}

async function renderGradeForm(id) {
  const aluno = await request(`/api/alunos/${encodeURIComponent(id)}`);
  return `${heading("Desempenho acadêmico", "Cadastrar nota", `Aluno: ${aluno.nome}`)}
    <form class="surface form-card" id="grade-form" data-id="${escapeHtml(id)}">
      <div class="form-grid">
        <div class="form-field form-field-full"><label for="descricao">Descrição da avaliação</label>
          <input id="descricao" name="descricao" type="text" required /></div>
        <div class="form-field"><label for="valor">Nota (de 0 a 10)</label>
          <input id="valor" name="valor" type="number" step="any" min="0" max="10" required /></div>
      </div>
      <div class="form-actions"><button type="submit">Cadastrar nota</button>
        <a class="button button-secondary" href="#/alunos/${encodeURIComponent(id)}">Cancelar</a></div>
    </form>`;
}

async function render() {
  const route = window.location.hash.slice(1) || "/";
  const [path, query = ""] = route.split("?", 2);
  const segments = path.split("/").filter(Boolean);
  const params = new URLSearchParams(query);
  app.innerHTML = '<p class="surface empty-state">Carregando...</p>';

  try {
    if (segments.length === 0) app.innerHTML = await renderHome();
    else if (segments[0] === "alunos" && segments.length === 1) app.innerHTML = await renderStudents(params);
    else if (segments[0] === "alunos" && segments[1] === "novo") app.innerHTML = await renderStudentForm();
    else if (segments[0] === "alunos" && segments.length === 3 && segments[2] === "editar") {
      app.innerHTML = await renderStudentForm(decodeURIComponent(segments[1]));
    } else if (segments[0] === "alunos" && segments.length === 4 && segments[2] === "notas" && segments[3] === "nova") {
      app.innerHTML = await renderGradeForm(decodeURIComponent(segments[1]));
    } else if (segments[0] === "alunos" && segments.length === 2) {
      app.innerHTML = await renderStudent(decodeURIComponent(segments[1]));
    } else {
      app.innerHTML = `${heading("Página", "Página não encontrada")}<a class="button" href="#/">Voltar ao início</a>`;
    }
  } catch (error) {
    app.innerHTML = `${feedback(error.message)}<div class="page-actions"><a class="button button-secondary" href="#/">Voltar ao início</a></div>`;
  }
}

app.addEventListener("submit", async (event) => {
  const form = event.target;
  if (!(form instanceof HTMLFormElement)) return;

  if (form.id === "search-form") {
    event.preventDefault();
    const params = new URLSearchParams(new FormData(form));
    window.location.hash = `/alunos?${params}`;
    return;
  }

  if (form.id !== "student-form" && form.id !== "grade-form") return;
  event.preventDefault();
  const submitButton = form.querySelector('button[type="submit"]');
  submitButton.disabled = true;

  try {
    const values = Object.fromEntries(new FormData(form));
    if (form.id === "student-form") {
      const payload = { ...values, idade: Number(values.idade) };
      const id = form.dataset.id;
      await request(id ? `/api/alunos/${encodeURIComponent(id)}` : "/api/alunos", {
        method: id ? "PUT" : "POST",
        body: JSON.stringify(payload),
      });
      window.location.hash = "#/alunos";
    } else {
      const id = form.dataset.id;
      await request(`/api/alunos/${encodeURIComponent(id)}/notas`, {
        method: "POST",
        body: JSON.stringify({ descricao: values.descricao, valor: Number(values.valor) }),
      });
      window.location.hash = `#/alunos/${encodeURIComponent(id)}`;
    }
  } catch (error) {
    app.insertAdjacentHTML("afterbegin", feedback(error.message));
    submitButton.disabled = false;
  }
});

app.addEventListener("click", async (event) => {
  const button = event.target.closest("button[data-delete]");
  if (!button) return;
  if (!window.confirm(`Tem certeza de que deseja excluir ${button.dataset.name}? Esta ação também removerá as notas associadas.`)) return;

  button.disabled = true;
  try {
    await request(`/api/alunos/${encodeURIComponent(button.dataset.delete)}`, { method: "DELETE" });
    await render();
  } catch (error) {
    app.insertAdjacentHTML("afterbegin", feedback(error.message));
    button.disabled = false;
  }
});

window.addEventListener("hashchange", render);
render();
