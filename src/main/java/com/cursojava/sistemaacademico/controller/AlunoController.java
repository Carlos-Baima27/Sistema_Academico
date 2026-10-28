package com.cursojava.sistemaacademico.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cursojava.sistemaacademico.model.Aluno;
import com.cursojava.sistemaacademico.service.AlunoService;

@Controller
public class AlunoController {
    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/alunos")
    public String listar(@RequestParam(required = false) String nome,
            Model model) {
        List<Aluno> alunos = alunoService.buscarPorNome(nome);

        model.addAttribute("alunos", alunos);
        model.addAttribute("nomePesquisado", nome == null ? "" : nome);
        model.addAttribute("pesquisando", nome != null && !nome.isBlank());

        return "alunos";
    }

    @GetMapping("/alunos/novo")
    public String formularioAluno(Model model) {
        model.addAttribute("aluno", new Aluno(null, "", "", "", 0));
        model.addAttribute("edicao", false);
        model.addAttribute("idadeCampo", "");
        return "aluno-form";
    }

    @PostMapping("/alunos")
    public String cadastarAluno(@RequestParam String nome,
            @RequestParam String email,
            @RequestParam String matricula,
            @RequestParam int idade) {
        alunoService.cadastrarAluno(nome, email, matricula, idade);

        return "redirect:/alunos";
    }

    @GetMapping("/alunos/{id}/editar")
    public String formularioEdicaoAluno(@PathVariable Long id, Model model) {
        Aluno aluno = alunoService.buscarPorId(id);

        if (aluno == null) {
            return "redirect:/alunos";
        }

        model.addAttribute("aluno", aluno);
        model.addAttribute("edicao", true);
        model.addAttribute("idadeCampo", aluno.getIdade());
        return "aluno-form";
    }

    @PostMapping("/alunos/{id}")
    public String atualizarAluno(@PathVariable Long id,
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String matricula,
            @RequestParam int idade,
            Model model) {
        if (alunoService.buscarPorId(id) == null) {
            return "redirect:/alunos";
        }

        Aluno dados = new Aluno(id, nome, email, matricula, idade);

        if (alunoService.dadosInvalidos(dados)) {
            model.addAttribute("aluno", dados);
            model.addAttribute("edicao", true);
            model.addAttribute("idadeCampo", idade);
            model.addAttribute("erro",
                    "Informe nome, e-mail e matrícula e uma idade maior que zero.");
            return "aluno-form";
        }

        Aluno alunoAtualizado = alunoService.atualizarAluno(id, dados);

        if (alunoAtualizado == null) {
            return "redirect:/alunos";
        }

        return "redirect:/alunos";
    }

    @PostMapping("/alunos/{id}/excluir")
    public String excluirAluno(@PathVariable Long id,
            @RequestParam(required = false) String nome,
            RedirectAttributes redirectAttributes) {
        if (alunoService.excluirAluno(id)) {
            redirectAttributes.addFlashAttribute("mensagemSucesso",
                    "Aluno excluído com sucesso.");
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não foi possível excluir: aluno não encontrado.");
        }

        if (nome != null && !nome.isBlank()) {
            redirectAttributes.addAttribute("nome", nome);
        }

        return "redirect:/alunos";
    }

    @GetMapping("/alunos/{id}")
    public String detalhes(@PathVariable Long id, Model model) {
        Aluno aluno = alunoService.buscarPorId(id);

        if (aluno == null) {
            return "redirect:/alunos";
        }

        model.addAttribute("aluno", aluno);
        return "aluno-detalhes";
    }

    @GetMapping("/alunos/{id}/notas/nova")
    public String formularioNota(@PathVariable Long id,
            Model model) {
        Aluno aluno = alunoService.buscarPorId(id);

        if(aluno == null) {
            return "redirect:/alunos";
        }

        model.addAttribute("aluno", aluno);
        return "nota-form";
    }

    @PostMapping("/alunos/{id}/notas")
    public String cadastrarNota(@PathVariable Long id,
                                @RequestParam String descricao,
                                @RequestParam double valor
    ) {
        alunoService.cadastrarNota(id, descricao, valor);

        return "redirect:/alunos/" + id;
    }
}
