package com.cursojava.sistemaacademico.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cursojava.sistemaacademico.model.Aluno;
import com.cursojava.sistemaacademico.model.Nota;
import com.cursojava.sistemaacademico.service.AlunoService;

class AlunoControllerTest {

    private final Aluno ana = new Aluno(1L, "Ana Silva", "ana@example.com", "A001", 20);
    private final Aluno bruno = new Aluno(2L, "Bruno Costa", "bruno@example.com", "A002", 21);
    private final Map<Long, Aluno> alunos = new HashMap<>();
    private boolean falharCadastroAluno;
    private boolean falharAtualizacao;
    private boolean falharCadastroNota;
    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        ana.setNome("Ana Silva");
        ana.setEmail("ana@example.com");
        ana.setMatricula("A001");
        ana.setIdade(20);
        bruno.setNome("Bruno Costa");
        bruno.setEmail("bruno@example.com");
        bruno.setMatricula("A002");
        bruno.setIdade(21);
        alunos.clear();
        alunos.put(ana.getId(), ana);
        alunos.put(bruno.getId(), bruno);
        falharCadastroAluno = false;
        falharAtualizacao = false;
        falharCadastroNota = false;
        List<Aluno> alunos = List.of(ana, bruno);
        AlunoService alunoService = new AlunoService(null, null) {
            @Override
            public List<Aluno> listarTodos() {
                return alunos;
            }

            @Override
            public Aluno buscarPorId(Long id) {
                return AlunoControllerTest.this.alunos.get(id);
            }

            @Override
            public Aluno atualizarAluno(Long id, Aluno dados) {
                Aluno aluno = AlunoControllerTest.this.alunos.get(id);

                if (aluno == null || falharAtualizacao) {
                    return null;
                }

                aluno.setNome(dados.getNome());
                aluno.setEmail(dados.getEmail());
                aluno.setMatricula(dados.getMatricula());
                aluno.setIdade(dados.getIdade());

                return aluno;
            }

            @Override
            public Aluno cadastrarAluno(String nome, String email,
                    String matricula, int idade) {
                if (falharCadastroAluno) {
                    return null;
                }

                Aluno aluno = new Aluno(3L, nome, email, matricula, idade);
                AlunoControllerTest.this.alunos.put(aluno.getId(), aluno);
                return aluno;
            }

            @Override
            public Nota cadastrarNota(Long alunoId, String descricao, double valor) {
                if (falharCadastroNota) {
                    return null;
                }

                Aluno aluno = AlunoControllerTest.this.alunos.get(alunoId);

                if (aluno == null) {
                    return null;
                }

                Nota nota = new Nota(descricao, valor);
                aluno.adicionarNota(nota);
                return nota;
            }

            @Override
            public boolean excluirAluno(Long id) {
                return AlunoControllerTest.this.alunos.remove(id) != null;
            }
        };
        mockMvc = MockMvcBuilders.standaloneSetup(new AlunoController(alunoService))
                .setViewResolvers((viewName, locale) -> (model, request, response) -> {
                })
                .build();
    }

    @Test
    void cadastroAlunoValidoConfirmaSucesso() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/alunos")
                        .param("nome", "Carla Souza")
                        .param("email", "carla@example.com")
                        .param("matricula", "A003")
                        .param("idade", "19"))
                .andExpect(status().isOk())
                .andExpect(flash().attribute("mensagemSucesso",
                        "Aluno cadastrado com sucesso."))
                .andReturn();

        assertEquals("redirect:/alunos", resultado.getModelAndView().getViewName());
        assertEquals("Carla Souza", alunos.get(3L).getNome());
    }

    @Test
    void dadosInvalidosNoCadastroPreservamValoresERetornamErrosDosCampos() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/alunos")
                        .param("nome", "")
                        .param("email", "")
                        .param("matricula", "")
                        .param("idade", "0"))
                .andExpect(status().isOk())
                .andReturn();

        Map<?, ?> erros = (Map<?, ?>) resultado.getModelAndView().getModel().get("erros");
        Aluno dados = (Aluno) resultado.getModelAndView().getModel().get("aluno");

        assertEquals("aluno-form", resultado.getModelAndView().getViewName());
        assertTrue(erros.containsKey("nome"));
        assertTrue(erros.containsKey("email"));
        assertTrue(erros.containsKey("matricula"));
        assertTrue(erros.containsKey("idade"));
        assertEquals("", dados.getNome());
        assertEquals("", dados.getEmail());
        assertEquals("", dados.getMatricula());
        assertEquals("0", resultado.getModelAndView().getModel().get("idadeCampo"));
        assertEquals(2, alunos.size());
    }

    @Test
    void idadeEmFormatoInvalidoRetornaFormularioPreservandoValor() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/alunos")
                        .param("nome", "Carla Souza")
                        .param("email", "carla@example.com")
                        .param("matricula", "A003")
                        .param("idade", "dezenove"))
                .andExpect(status().isOk())
                .andReturn();

        Map<?, ?> erros = (Map<?, ?>) resultado.getModelAndView().getModel().get("erros");

        assertEquals("aluno-form", resultado.getModelAndView().getViewName());
        assertTrue(erros.containsKey("idade"));
        assertEquals("dezenove", resultado.getModelAndView().getModel().get("idadeCampo"));
        assertEquals(2, alunos.size());
    }

    @Test
    void falhaNoCadastroDeAlunoInformaErroSemSucesso() throws Exception {
        falharCadastroAluno = true;

        MvcResult resultado = mockMvc.perform(post("/alunos")
                        .param("nome", "Carla Souza")
                        .param("email", "carla@example.com")
                        .param("matricula", "A003")
                        .param("idade", "19"))
                .andExpect(status().isOk())
                .andExpect(flash().attribute("mensagemErro",
                        "Não foi possível cadastrar o aluno."))
                .andExpect(flash().attributeCount(1))
                .andReturn();

        assertEquals("redirect:/alunos", resultado.getModelAndView().getViewName());
    }

    @Test
    void pesquisaPorNomeExibeResultadosEPreservaOTermo() throws Exception {
        MvcResult resultado = mockMvc.perform(get("/alunos").param("nome", "Ana"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("alunos", resultado.getModelAndView().getViewName());
        assertEquals(List.of(ana), resultado.getModelAndView().getModel().get("alunos"));
        assertEquals("Ana", resultado.getModelAndView().getModel().get("nomePesquisado"));
        assertTrue((Boolean) resultado.getModelAndView().getModel().get("pesquisando"));
    }

    @Test
    void pesquisaSemCorrespondenciasPreservaOTermo() throws Exception {
        MvcResult resultado = mockMvc.perform(get("/alunos").param("nome", "Carla"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(List.of(), resultado.getModelAndView().getModel().get("alunos"));
        assertEquals("Carla", resultado.getModelAndView().getModel().get("nomePesquisado"));
        assertTrue((Boolean) resultado.getModelAndView().getModel().get("pesquisando"));
    }

    @Test
    void consultaVaziaExibeListaCompletaSemEstadoDePesquisa() throws Exception {
        MvcResult resultado = mockMvc.perform(get("/alunos").param("nome", ""))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(List.of(ana, bruno), resultado.getModelAndView().getModel().get("alunos"));
        assertEquals("", resultado.getModelAndView().getModel().get("nomePesquisado"));
        assertFalse((Boolean) resultado.getModelAndView().getModel().get("pesquisando"));
    }

    @Test
    void formularioDeEdicaoCarregaAlunoExistente() throws Exception {
        MvcResult resultado = mockMvc.perform(get("/alunos/1/editar"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("aluno-form", resultado.getModelAndView().getViewName());
        assertEquals(ana, resultado.getModelAndView().getModel().get("aluno"));
        assertTrue((Boolean) resultado.getModelAndView().getModel().get("edicao"));
    }

    @Test
    void formularioDeEdicaoRedirecionaQuandoAlunoNaoExiste() throws Exception {
        MvcResult resultado = mockMvc.perform(get("/alunos/99/editar"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("redirect:/alunos", resultado.getModelAndView().getViewName());
    }

    @Test
    void atualizaAlunoComDadosValidos() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/alunos/1")
                        .param("nome", "Ana Souza")
                        .param("email", "ana.souza@example.com")
                        .param("matricula", "A010")
                        .param("idade", "22"))
                .andExpect(status().isOk())
                .andExpect(flash().attribute("mensagemSucesso",
                        "Aluno atualizado com sucesso."))
                .andReturn();

        assertEquals("redirect:/alunos", resultado.getModelAndView().getViewName());
        assertEquals("Ana Souza", ana.getNome());
        assertEquals("ana.souza@example.com", ana.getEmail());
        assertEquals("A010", ana.getMatricula());
        assertEquals(22, ana.getIdade());
    }

    @Test
    void dadosInvalidosNaoAtualizamAlunoERetornamFormulario() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/alunos/1")
                        .param("nome", "")
                        .param("email", "ana@example.com")
                        .param("matricula", "A001")
                        .param("idade", "0"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("aluno-form", resultado.getModelAndView().getViewName());
        assertTrue((Boolean) resultado.getModelAndView().getModel().get("edicao"));
        assertTrue(((Map<?, ?>) resultado.getModelAndView().getModel().get("erros"))
                .containsKey("nome"));
        assertTrue(((Map<?, ?>) resultado.getModelAndView().getModel().get("erros"))
                .containsKey("idade"));
        assertEquals("0", resultado.getModelAndView().getModel().get("idadeCampo"));
        assertEquals("Ana Silva", ana.getNome());
        assertEquals(20, ana.getIdade());
    }

    @Test
    void falhaNaAtualizacaoInformaErroSemSucesso() throws Exception {
        falharAtualizacao = true;

        MvcResult resultado = mockMvc.perform(post("/alunos/1")
                        .param("nome", "Ana Souza")
                        .param("email", "ana.souza@example.com")
                        .param("matricula", "A010")
                        .param("idade", "22"))
                .andExpect(status().isOk())
                .andExpect(flash().attribute("mensagemErro",
                        "Não foi possível atualizar o aluno."))
                .andExpect(flash().attributeCount(1))
                .andReturn();

        assertEquals("redirect:/alunos", resultado.getModelAndView().getViewName());
        assertEquals("Ana Silva", ana.getNome());
    }

    @Test
    void atualizacaoDeAlunoInexistenteRedirecionaSemSimularSucesso() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/alunos/99")
                        .param("nome", "Carla")
                        .param("email", "carla@example.com")
                        .param("matricula", "A099")
                        .param("idade", "19"))
                .andExpect(status().isOk())
                .andExpect(flash().attribute("mensagemErro",
                        "Não foi possível atualizar: aluno não encontrado."))
                .andExpect(flash().attributeCount(1))
                .andReturn();

        assertEquals("redirect:/alunos", resultado.getModelAndView().getViewName());
    }

    @Test
    void notaInvalidaRetornaFormularioComErrosEValoresPreservados() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/alunos/1/notas")
                        .param("descricao", "")
                        .param("valor", "10.1"))
                .andExpect(status().isOk())
                .andReturn();

        Map<?, ?> erros = (Map<?, ?>) resultado.getModelAndView().getModel().get("erros");

        assertEquals("nota-form", resultado.getModelAndView().getViewName());
        assertTrue(erros.containsKey("descricao"));
        assertTrue(erros.containsKey("valor"));
        assertEquals("", resultado.getModelAndView().getModel().get("descricao"));
        assertEquals("10.1", resultado.getModelAndView().getModel().get("valorCampo"));
        assertTrue(ana.getNotas().isEmpty());
    }

    @Test
    void notaValidaConfirmaSucessoERedirecionaAosDetalhes() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/alunos/1/notas")
                        .param("descricao", "Prova")
                        .param("valor", "8.5"))
                .andExpect(status().isOk())
                .andExpect(flash().attribute("mensagemSucesso",
                        "Nota cadastrada com sucesso."))
                .andReturn();

        assertEquals("redirect:/alunos/1", resultado.getModelAndView().getViewName());
        assertEquals(1, ana.getNotas().size());
        assertEquals("Prova", ana.getNotas().get(0).getDescricao());
        assertEquals(8.5, ana.getNotas().get(0).getValor());
    }

    @Test
    void notaComValorInvalidoRetornaFormularioPreservandoDescricao() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/alunos/1/notas")
                        .param("descricao", "Prova final")
                        .param("valor", "invalido"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("nota-form", resultado.getModelAndView().getViewName());
        assertEquals("Prova final", resultado.getModelAndView().getModel().get("descricao"));
        assertEquals("invalido", resultado.getModelAndView().getModel().get("valorCampo"));
        assertTrue(((Map<?, ?>) resultado.getModelAndView().getModel().get("erros"))
                .containsKey("valor"));
        assertTrue(ana.getNotas().isEmpty());
    }

    @Test
    void falhaNoCadastroDeNotaInformaErroSemSucesso() throws Exception {
        falharCadastroNota = true;

        MvcResult resultado = mockMvc.perform(post("/alunos/1/notas")
                        .param("descricao", "Prova")
                        .param("valor", "8.5"))
                .andExpect(status().isOk())
                .andExpect(flash().attribute("mensagemErro",
                        "Não foi possível cadastrar a nota."))
                .andExpect(flash().attributeCount(1))
                .andReturn();

        assertEquals("redirect:/alunos/1", resultado.getModelAndView().getViewName());
        assertTrue(ana.getNotas().isEmpty());
    }

    @Test
    void notaDeAlunoInexistenteInformaErroSemSimularSucesso() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/alunos/99/notas")
                        .param("descricao", "Prova")
                        .param("valor", "8.5"))
                .andExpect(status().isOk())
                .andExpect(flash().attribute("mensagemErro",
                        "Não foi possível cadastrar a nota: aluno não encontrado."))
                .andExpect(flash().attributeCount(1))
                .andReturn();

        assertEquals("redirect:/alunos", resultado.getModelAndView().getViewName());
        assertTrue(ana.getNotas().isEmpty());
    }

    @Test
    void exclusaoDeAlunoExistenteConfirmaESegueNaPesquisaAtual() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/alunos/1/excluir")
                        .param("nome", "Ana"))
                .andExpect(status().isOk())
                .andExpect(flash().attribute("mensagemSucesso",
                        "Aluno excluído com sucesso."))
                .andReturn();

        assertEquals("redirect:/alunos", resultado.getModelAndView().getViewName());
        assertEquals("Ana", resultado.getModelAndView().getModel().get("nome"));
        assertFalse(alunos.containsKey(ana.getId()));
    }

    @Test
    void exclusaoDeAlunoInexistenteInformaErroSemSimularSucesso() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/alunos/99/excluir"))
                .andExpect(status().isOk())
                .andExpect(flash().attribute("mensagemErro",
                        "Não foi possível excluir: aluno não encontrado."))
                .andReturn();

        assertEquals("redirect:/alunos", resultado.getModelAndView().getViewName());
    }
}
