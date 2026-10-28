package com.cursojava.sistemaacademico.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.cursojava.sistemaacademico.model.Aluno;
import com.cursojava.sistemaacademico.model.Nota;

class AlunoServiceTest {

    private final AlunoService alunoService = new AlunoService(null, null);

    @Test
    void idadeDeveSerMaiorQueZero() {
        assertFalse(alunoService.dadosInvalidos(alunoComIdade(1)));
        assertTrue(alunoService.dadosInvalidos(alunoComIdade(0)));
        assertTrue(alunoService.dadosInvalidos(alunoComIdade(-1)));
    }

    @Test
    void notaDeveEstarEntreZeroEDezInclusive() {
        assertFalse(alunoService.notaInvalida(new Nota("Prova", 0.0)));
        assertFalse(alunoService.notaInvalida(new Nota("Prova", 10.0)));
        assertTrue(alunoService.notaInvalida(new Nota("Prova", -0.01)));
        assertTrue(alunoService.notaInvalida(new Nota("Prova", 10.01)));
    }

    private Aluno alunoComIdade(int idade) {
        return new Aluno(null, "Ana", "ana@example.com", "A001", idade);
    }
}
