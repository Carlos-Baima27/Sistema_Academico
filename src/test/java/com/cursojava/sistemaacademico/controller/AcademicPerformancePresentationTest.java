package com.cursojava.sistemaacademico.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import com.cursojava.sistemaacademico.model.Aluno;
import com.cursojava.sistemaacademico.model.Nota;
import com.cursojava.sistemaacademico.service.AlunoService;

class AcademicPerformancePresentationTest {

    private SpringTemplateEngine templateEngine;

    @BeforeEach
    void configurarTemplateEngine() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);

        templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);
    }

    @Test
    void mediaEAprovacaoDoModeloSaoApresentadasNaListaEDetalhes() {
        Aluno aluno = alunoComNotas("Ana", 8.0, 7.0);

        assertEquals(7.5, aluno.getMedia());
        assertEquals("Aprovado", aluno.getSituacao());
        assertPresentesNosTemplates(aluno, "7.5", "Aprovado", "status-approved");
    }

    @Test
    void reprovacaoDoModeloEApresentadaNaListaEDetalhes() {
        Aluno aluno = alunoComNotas("Bruno", 6.0, 7.0);

        assertEquals(6.5, aluno.getMedia());
        assertEquals("Reprovado", aluno.getSituacao());
        assertPresentesNosTemplates(aluno, "6.5", "Reprovado", "status-failed");
    }

    @Test
    void situacaoSemNotasDoModeloEApresentadaNaListaEDetalhes() {
        Aluno aluno = alunoComNotas("Carla");

        assertEquals(0.0, aluno.getMedia());
        assertEquals("Sem notas", aluno.getSituacao());
        assertPresentesNosTemplates(aluno, "0.0", "Sem notas", "status-no-grades");
    }

    private void assertPresentesNosTemplates(
            Aluno aluno, String media, String situacao, String classeSituacao) {
        AlunoRestController api = new AlunoRestController(new AlunoService(null, null) {
            @Override
            public Aluno buscarPorId(Long id) {
                return aluno;
            }
        });
        var respostaApi = api.consultarMedia(aluno.getId());
        assertEquals(200, respostaApi.getStatusCode().value());
        assertEquals(aluno.getMedia(), respostaApi.getBody().get("media"));
        assertEquals(aluno.getSituacao(), respostaApi.getBody().get("situacao"));

        String lista = renderizar("alunos", aluno);
        String detalhes = renderizar("aluno-detalhes", aluno);

        for (String html : List.of(lista, detalhes)) {
            assertTrue(html.contains("academic-average"));
            assertTrue(html.contains(media));
            assertTrue(html.contains(classeSituacao));
            assertTrue(html.contains(situacao));
        }
    }

    private String renderizar(String template, Aluno aluno) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockServletContext servletContext = new MockServletContext();
        JakartaServletWebApplication application =
                JakartaServletWebApplication.buildApplication(servletContext);
        WebContext context = new WebContext(
                application.buildExchange(request, response), Locale.ROOT);
        context.setVariable("aluno", aluno);
        context.setVariable("alunos", List.of(aluno));
        context.setVariable("pesquisando", false);
        context.setVariable("nomePesquisado", "");
        return templateEngine.process(template, context);
    }

    private Aluno alunoComNotas(String nome, double... valores) {
        Aluno aluno = new Aluno(1L, nome, nome.toLowerCase() + "@example.com", "A001", 20);
        for (double valor : valores) {
            aluno.adicionarNota(new Nota("Avaliação", valor));
        }
        return aluno;
    }
}
