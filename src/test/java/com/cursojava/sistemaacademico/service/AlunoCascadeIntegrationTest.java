package com.cursojava.sistemaacademico.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.cursojava.sistemaacademico.model.Aluno;
import com.cursojava.sistemaacademico.model.Nota;
import com.cursojava.sistemaacademico.repository.AlunoRepository;
import com.cursojava.sistemaacademico.repository.NotaRepository;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:aluno-cascade-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class AlunoCascadeIntegrationTest {

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private NotaRepository notaRepository;

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
}
