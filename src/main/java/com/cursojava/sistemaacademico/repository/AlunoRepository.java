package com.cursojava.sistemaacademico.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cursojava.sistemaacademico.model.Aluno;

public interface AlunoRepository 
        extends JpaRepository<Aluno, Long> {}
