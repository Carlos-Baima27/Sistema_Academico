package com.cursojava.sistemaacademico.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cursojava.sistemaacademico.model.Aluno;
import com.cursojava.sistemaacademico.service.AlunoService;

class AlunoControllerTest {

    private final Aluno ana = new Aluno(1L, "Ana Silva", "ana@example.com", "A001", 20);
    private final Aluno bruno = new Aluno(2L, "Bruno Costa", "bruno@example.com", "A002", 21);
    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        List<Aluno> alunos = List.of(ana, bruno);
        AlunoService alunoService = new AlunoService(null, null) {
            @Override
            public List<Aluno> listarTodos() {
                return alunos;
            }
        };
        mockMvc = MockMvcBuilders.standaloneSetup(new AlunoController(alunoService))
                .setViewResolvers((viewName, locale) -> (model, request, response) -> {
                })
                .build();
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
}
