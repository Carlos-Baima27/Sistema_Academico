package com.cursojava.sistemaacademico.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Year;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.cursojava.sistemaacademico.model.Aluno;
import com.cursojava.sistemaacademico.model.Nota;
import com.cursojava.sistemaacademico.repository.AlunoRepository;
import com.cursojava.sistemaacademico.repository.MatriculaSequenceRepository;
import com.cursojava.sistemaacademico.repository.NotaRepository;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:aluno-cascade-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class AlunoCascadeIntegrationTest {

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private MatriculaSequenceRepository matriculaSequenceRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void excluirAlunoTambemRemoveSuasNotas() {
        Aluno aluno = new Aluno(null, "Ana", "ana@example.com", "A001", 20);
        aluno.adicionarNota(new Nota("Prova", 8.0));
        Aluno salvo = alunoRepository.save(aluno);
        Long alunoId = salvo.getId();

        assertNotNull(alunoId);
        assertFalse(notaRepository.findByAlunoId(alunoId).isEmpty());

        assertTrue(alunoService.excluirAluno(alunoId));

        assertFalse(alunoRepository.existsById(alunoId));
        assertTrue(notaRepository.findByAlunoId(alunoId).isEmpty());
    }

    @Test
    void cadastrarNotaPersisteAssociadaAoAluno() {
        Aluno salvo = alunoRepository.save(
                new Aluno(null, "Bruno", "bruno@example.com", "A002", 21));

        Nota nota = alunoService.cadastrarNota(salvo.getId(), "Prova", 8.5);

        assertNotNull(nota);
        assertFalse(notaRepository.findByAlunoId(salvo.getId()).isEmpty());
        assertTrue(notaRepository.findByAlunoId(salvo.getId()).contains(nota));
    }

    @Test
    void cadastroGeraMatriculaSequencialEIgnoraValorEnviadoPelaApi() throws Exception {
        String matriculaEsperada = formatarMatricula(1);

        mockMvc.perform(post("/api/alunos")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Ana",
                                  "email": "ana@example.com",
                                  "matricula": "MATRICULA-ESCOLHIDA",
                                  "idade": 20
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricula").value(matriculaEsperada));

        Aluno segundo = alunoService.cadastrarAluno("Bruno", "bruno@example.com", 21);
        assertTrue(segundo.getMatricula().endsWith("-0002"));
    }

    @Test
    void atualizacaoDaApiNaoAlteraMatricula() throws Exception {
        Aluno salvo = alunoService.cadastrarAluno("Ana", "ana@example.com", 20);
        String matriculaOriginal = salvo.getMatricula();

        mockMvc.perform(put("/api/alunos/" + salvo.getId())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Ana Souza",
                                  "email": "ana@example.com",
                                  "matricula": "MATRICULA-ESCOLHIDA",
                                  "idade": 21
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value(matriculaOriginal));
    }

    @Test
    void migracaoRenumeraAlunosExistentesUmaUnicaVezEPreservaNotas() {
        matriculaSequenceRepository.deleteById(1L);
        Aluno primeiro = new Aluno(null, "Ana", "ana@example.com", "A001", 20);
        primeiro.adicionarNota(new Nota("Prova", 8.0));
        Aluno segundo = new Aluno(null, "Bruno", "bruno@example.com", "A002", 21);
        alunoRepository.saveAll(List.of(primeiro, segundo));
        alunoRepository.flush();

        alunoService.inicializarMatriculas();

        assertEquals(formatarMatricula(1), primeiro.getMatricula());
        assertEquals(formatarMatricula(2), segundo.getMatricula());
        assertEquals(1, notaRepository.findByAlunoId(primeiro.getId()).size());

        primeiro.setMatricula("MATRICULA-MANUAL");
        alunoRepository.saveAndFlush(primeiro);
        alunoService.inicializarMatriculas();

        assertEquals("MATRICULA-MANUAL", primeiro.getMatricula());
        Aluno novo = alunoService.cadastrarAluno("Carla", "carla@example.com", 19);
        assertEquals(formatarMatricula(3), novo.getMatricula());
    }

    @Test
    void formularioDeAlunoRenderizaErrosDeValidacao() throws Exception {
        String conteudo = mockMvc.perform(post("/alunos")
                                .param("nome", "")
                                .param("email", "")
                                .param("matricula", "")
                                .param("idade", "0"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(conteudo.contains("Informe o nome do aluno."));
        assertTrue(conteudo.contains("Informe uma idade válida maior que zero."));
        assertTrue(conteudo.contains("aria-invalid=\"true\""));
    }

    @Test
    void formularioDeNovoAlunoInformaGeracaoAutomaticaDaMatricula() throws Exception {
        String conteudo = mockMvc.perform(get("/alunos/novo"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(conteudo.contains("Será gerada automaticamente no formato AAAA-0001."));
        assertFalse(conteudo.contains("name=\"matricula\""));
    }

    @Test
    void formularioDeNotaRenderizaErrosEValoresPreservados() throws Exception {
        Aluno salvo = alunoRepository.save(
                new Aluno(null, "Carla", "carla@example.com", "A003", 19));

        String conteudo = mockMvc.perform(post("/alunos/" + salvo.getId() + "/notas")
                                .param("descricao", "")
                                .param("valor", "10.5"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(conteudo.contains("Informe a descrição da avaliação."));
        assertTrue(conteudo.contains("Informe uma nota válida entre 0 e 10."));
        assertTrue(conteudo.contains("value=\"10.5\""));
        assertTrue(notaRepository.findByAlunoId(salvo.getId()).isEmpty());
    }

    private String formatarMatricula(long numero) {
        return String.format(Locale.ROOT, "%d-%04d", Year.now().getValue(), numero);
    }
}
