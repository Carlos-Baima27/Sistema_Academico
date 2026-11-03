package com.cursojava.sistemaacademico.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MatriculaInitializer implements ApplicationRunner {

    private final AlunoService alunoService;

    public MatriculaInitializer(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @Override
    public void run(ApplicationArguments args) {
        alunoService.inicializarMatriculas();
    }
}
