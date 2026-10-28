package com.cursojava.sistemaacademico.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.cursojava.sistemaacademico.model.Aluno;
import com.cursojava.sistemaacademico.model.Nota;
import com.cursojava.sistemaacademico.repository.AlunoRepository;
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
}
