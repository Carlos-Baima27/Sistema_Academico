package com.cursojava.sistemaacademico.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    public String index(Model model) {
        model.addAttribute("totalAlunos", alunoService.contarAlunos());
        return "index";
    }

    @GetMapping("/alunos")
    public String listar(@RequestParam(required = false) String nome,
            @RequestParam(required = false) String ordenarPor,
            @RequestParam(required = false) String direcao,
            Model model) {
        List<Aluno> alunos = new ArrayList<>(alunoService.buscarPorNome(nome));
        String criterio = normalizarCriterio(ordenarPor);
        String sentido = "desc".equalsIgnoreCase(direcao) ? "desc" : "asc";
        alunos.sort((alunoA, alunoB) ->
                compararAlunos(alunoA, alunoB, criterio, sentido));

        model.addAttribute("alunos", alunos);
        model.addAttribute("nomePesquisado", nome == null ? "" : nome);
        model.addAttribute("pesquisando", nome != null && !nome.isBlank());
        model.addAttribute("ordenarPor", criterio);
        model.addAttribute("direcao", sentido);
        model.addAttribute("ordenacaoAtual", descricaoOrdenacao(criterio, sentido));

        return "alunos";
    }

    private String normalizarCriterio(String ordenarPor) {
        if (ordenarPor == null) {
            return "nome";
        }

        return switch (ordenarPor.toLowerCase(Locale.ROOT)) {
            case "matricula", "media", "situacao" -> ordenarPor.toLowerCase(Locale.ROOT);
            default -> "nome";
        };
    }

    private int compararAlunos(Aluno alunoA, Aluno alunoB, String criterio, String sentido) {
        boolean decrescente = "desc".equals(sentido);
        int resultado;
        if ("matricula".equals(criterio)) {
            resultado = compararTexto(
                    alunoA.getMatricula(), alunoB.getMatricula(), decrescente);
        } else if ("media".equals(criterio)) {
            resultado = Double.compare(alunoA.getMedia(), alunoB.getMedia());
            if (decrescente) {
                resultado = -resultado;
            }
        } else if ("situacao".equals(criterio)) {
            resultado = compararTexto(
                    alunoA.getSituacao(), alunoB.getSituacao(), decrescente);
        } else {
            resultado = compararTexto(
                    alunoA.getNome(), alunoB.getNome(), decrescente);
        }

        if (resultado != 0) {
            return resultado;
        }
        return compararIds(alunoA.getId(), alunoB.getId(), decrescente);
    }

    private int compararTexto(String textoA, String textoB, boolean decrescente) {
        if (textoA == null) {
            return textoB == null ? 0 : 1;
        }
        if (textoB == null) {
            return -1;
        }

        int resultado = String.CASE_INSENSITIVE_ORDER.compare(textoA, textoB);
        return decrescente ? -resultado : resultado;
    }

    private int compararIds(Long idA, Long idB, boolean decrescente) {
        if (idA == null) {
            return idB == null ? 0 : 1;
        }
        if (idB == null) {
            return -1;
        }

        int resultado = Long.compare(idA, idB);
        return decrescente ? -resultado : resultado;
    }

    private String descricaoOrdenacao(String criterio, String sentido) {
        String campo = switch (criterio) {
            case "matricula" -> "matrícula";
            case "media" -> "média";
            case "situacao" -> "situação";
            default -> "nome";
        };
        String ordem = "desc".equals(sentido) ? "decrescente" : "crescente";
        return campo + " (" + ordem + ")";
    }

    @GetMapping("/alunos/novo")
    public String formularioAluno(Model model) {
        model.addAttribute("aluno", new Aluno(null, "", "", null, 0));
        model.addAttribute("edicao", false);
        model.addAttribute("idadeCampo", "");
        return "aluno-form";
    }

    @PostMapping("/alunos")
    public String cadastrarAluno(@RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String idade,
            Model model,
            RedirectAttributes redirectAttributes) {
        Integer idadeConvertida = converterIdade(idade);
        Aluno dados = new Aluno(null, nome, email, null,
                idadeConvertida == null ? 0 : idadeConvertida);

        if (idadeConvertida == null || idadeConvertida <= 0
                || alunoService.dadosInvalidos(dados)) {
            model.addAttribute("aluno", dados);
            model.addAttribute("edicao", false);
            model.addAttribute("idadeCampo", idade == null ? "" : idade);
            model.addAttribute("erros", errosAluno(dados, idadeConvertida));
            return "aluno-form";
        }

        Aluno alunoCadastrado = alunoService.cadastrarAluno(
                nome, email, idadeConvertida.intValue());

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
            @RequestParam(required = false) String idade,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (alunoService.buscarPorId(id) == null) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não foi possível atualizar: aluno não encontrado.");
            return "redirect:/alunos";
        }

        Integer idadeConvertida = converterIdade(idade);
        Aluno dados = new Aluno(id, nome, email, null,
                idadeConvertida == null ? 0 : idadeConvertida);

        if (idadeConvertida == null || idadeConvertida <= 0
                || alunoService.dadosInvalidos(dados)) {
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
            @RequestParam(required = false) String ordenarPor,
            @RequestParam(required = false) String direcao,
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
        if (ordenarPor != null && !ordenarPor.isBlank()) {
            redirectAttributes.addAttribute("ordenarPor", ordenarPor);
        }
        if (direcao != null && !direcao.isBlank()) {
            redirectAttributes.addAttribute("direcao", direcao);
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

        if (valorConvertido == null || alunoService.notaInvalida(dados)) {
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

        Nota notaCadastrada = alunoService.cadastrarNota(
                id, descricao, valorConvertido.doubleValue());

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
        if (idadeConvertida == null || idadeConvertida <= 0) {
            erros.put("idade", "Informe uma idade válida maior que zero.");
        }
        return erros;
    }
}
