package com.cursojava.sistemaacademico.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cursojava.sistemaacademico.model.Aluno;
import com.cursojava.sistemaacademico.model.Nota;
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
    public String cadastrarAluno(@RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false) String idade,
            Model model,
            RedirectAttributes redirectAttributes) {
        Integer idadeConvertida = converterIdade(idade);
        Aluno dados = new Aluno(null, nome, email, matricula,
                idadeConvertida == null ? 0 : idadeConvertida);

        if (alunoService.dadosInvalidos(dados)) {
            model.addAttribute("aluno", dados);
            model.addAttribute("edicao", false);
            model.addAttribute("idadeCampo", idade == null ? "" : idade);
            model.addAttribute("erros", errosAluno(dados, idadeConvertida));
            return "aluno-form";
        }

        Aluno alunoCadastrado = alunoService.cadastrarAluno(nome, email,
                matricula, idadeConvertida);

        if (alunoCadastrado == null) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não foi possível cadastrar o aluno.");
        } else {
            redirectAttributes.addFlashAttribute("mensagemSucesso",
                    "Aluno cadastrado com sucesso.");
        }

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
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false) String idade,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (alunoService.buscarPorId(id) == null) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não foi possível atualizar: aluno não encontrado.");
            return "redirect:/alunos";
        }

        Integer idadeConvertida = converterIdade(idade);
        Aluno dados = new Aluno(id, nome, email, matricula,
                idadeConvertida == null ? 0 : idadeConvertida);

        if (alunoService.dadosInvalidos(dados)) {
            model.addAttribute("aluno", dados);
            model.addAttribute("edicao", true);
            model.addAttribute("idadeCampo", idade == null ? "" : idade);
            model.addAttribute("erros", errosAluno(dados, idadeConvertida));
            return "aluno-form";
        }

        Aluno alunoAtualizado = alunoService.atualizarAluno(id, dados);

        if (alunoAtualizado == null) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não foi possível atualizar o aluno.");
        } else {
            redirectAttributes.addFlashAttribute("mensagemSucesso",
                    "Aluno atualizado com sucesso.");
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

        if (aluno == null) {
            return "redirect:/alunos";
        }

        model.addAttribute("aluno", aluno);
        return "nota-form";
    }

    @PostMapping("/alunos/{id}/notas")
    public String cadastrarNota(@PathVariable Long id,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) String valor,
            Model model,
            RedirectAttributes redirectAttributes) {
        Aluno aluno = alunoService.buscarPorId(id);

        if (aluno == null) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não foi possível cadastrar a nota: aluno não encontrado.");
            return "redirect:/alunos";
        }

        Double valorConvertido = converterValor(valor);
        Nota dados = new Nota(descricao,
                valorConvertido == null ? Double.NaN : valorConvertido);

        if (alunoService.notaInvalida(dados)) {
            Map<String, String> erros = new HashMap<>();
            if (descricao == null || descricao.isBlank()) {
                erros.put("descricao", "Informe a descrição da avaliação.");
            }
            if (valorConvertido == null || alunoService.notaInvalida(
                    new Nota("avaliação", valorConvertido))) {
                erros.put("valor", "Informe uma nota válida entre 0 e 10.");
            }

            model.addAttribute("aluno", aluno);
            model.addAttribute("descricao", descricao == null ? "" : descricao);
            model.addAttribute("valorCampo", valor == null ? "" : valor);
            model.addAttribute("erros", erros);
            return "nota-form";
        }

        Nota notaCadastrada = alunoService.cadastrarNota(id, descricao, valorConvertido);

        if (notaCadastrada == null) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não foi possível cadastrar a nota.");
        } else {
            redirectAttributes.addFlashAttribute("mensagemSucesso",
                    "Nota cadastrada com sucesso.");
        }

        return "redirect:/alunos/" + id;
    }

    private Integer converterIdade(String idade) {
        if (idade == null || idade.isBlank()) {
            return null;
        }

        try {
            return Integer.valueOf(idade);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Double converterValor(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        try {
            return Double.valueOf(valor);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Map<String, String> errosAluno(Aluno aluno, Integer idadeConvertida) {
        Map<String, String> erros = new HashMap<>();
        if (aluno.getNome() == null || aluno.getNome().isBlank()) {
            erros.put("nome", "Informe o nome do aluno.");
        }
        if (aluno.getEmail() == null || aluno.getEmail().isBlank()) {
            erros.put("email", "Informe um e-mail.");
        }
        if (aluno.getMatricula() == null || aluno.getMatricula().isBlank()) {
            erros.put("matricula", "Informe a matrícula.");
        }
        if (idadeConvertida == null || idadeConvertida <= 0) {
            erros.put("idade", "Informe uma idade válida maior que zero.");
        }
        return erros;
    }
}
