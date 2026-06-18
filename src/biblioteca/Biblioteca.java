package biblioteca;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Biblioteca implements Serializable {

    private static final long serialVersionUID = 1L;

    private static volatile Biblioteca instancia;

    private List<Livro> livros;
    private List<Usuario> usuarios;
    private List<Emprestimo> emprestimos;

    private Biblioteca() {
        livros = new ArrayList<>();
        usuarios = new ArrayList<>();
        emprestimos = new ArrayList<>();
    }

    public static Biblioteca getInstance() {
        if (instancia == null) {
            synchronized (Biblioteca.class) {
                if (instancia == null) {
                    instancia = new Biblioteca();
                }
            }
        }
        return instancia;
    }

    private Object readResolve() {
        instancia = this;
        return instancia;
    }

    // ========== LIVROS ==========

    public boolean adicionarLivro(Livro livro) {
        if (livro == null) return false;
        if (buscarLivroPorCodigo(livro.getCodigo()) != null) return false;
        return livros.add(livro);
    }

    public boolean removerLivro(int codigo) {
        Livro livro = buscarLivroPorCodigo(codigo);
        if (livro == null) return false;
        if (isLivroEmprestado(livro)) return false;
        return livros.remove(livro);
    }

    public List<Livro> getLivros() {
        return Collections.unmodifiableList(livros);
    }

    public Livro buscarLivroPorCodigo(int codigo) {
        return livros.stream()
                .filter(l -> l.getCodigo() == codigo)
                .findFirst()
                .orElse(null);
    }

    public List<Livro> buscarLivroPorTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) return new ArrayList<>(livros);
        return livros.stream()
                .filter(l -> l.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Livro> getLivrosDisponiveis() {
        return livros.stream()
                .filter(l -> !isLivroEmprestado(l))
                .collect(Collectors.toList());
    }

    // ========== USUARIOS ==========

    public boolean adicionarUsuario(Usuario usuario) {
        if (usuario == null) return false;
        if (buscarUsuarioPorId(usuario.getId()) != null) return false;
        return usuarios.add(usuario);
    }

    public boolean removerUsuario(int id) {
        Usuario usuario = buscarUsuarioPorId(id);
        if (usuario == null) return false;
        if (temEmprestimoAtivo(usuario)) return false;
        return usuarios.remove(usuario);
    }

    public List<Usuario> getUsuarios() {
        return Collections.unmodifiableList(usuarios);
    }

    public Usuario buscarUsuarioPorId(int id) {
        return usuarios.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // ========== EMPRESTIMOS ==========

    public boolean realizarEmprestimo(Livro livro, Usuario usuario) {
        if (livro == null || usuario == null) return false;
        if (isLivroEmprestado(livro)) return false;
        return emprestimos.add(new Emprestimo(livro, usuario));
    }

    public boolean realizarDevolucao(Livro livro) {
        Emprestimo emprestimo = emprestimos.stream()
                .filter(e -> e.isAtivo() && e.getLivro().equals(livro))
                .findFirst()
                .orElse(null);
        if (emprestimo == null) return false;
        emprestimo.finalizar();
        return true;
    }

    public List<Emprestimo> getEmprestimos() {
        return Collections.unmodifiableList(emprestimos);
    }

    public List<Emprestimo> getEmprestimosAtivos() {
        return emprestimos.stream()
                .filter(Emprestimo::isAtivo)
                .collect(Collectors.toList());
    }

    public List<Livro> getLivrosEmprestados() {
        return emprestimos.stream()
                .filter(Emprestimo::isAtivo)
                .map(Emprestimo::getLivro)
                .collect(Collectors.toList());
    }

    public boolean isLivroEmprestado(Livro livro) {
        return emprestimos.stream()
                .anyMatch(e -> e.isAtivo() && e.getLivro().equals(livro));
    }

    private boolean temEmprestimoAtivo(Usuario usuario) {
        return emprestimos.stream()
                .anyMatch(e -> e.isAtivo() && e.getUsuario().equals(usuario));
    }

    // ========== PERSISTENCIA ==========

    public void salvarDados(String caminho) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminho))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void carregarDados(String caminho) {
        File arquivo = new File(caminho);
        if (!arquivo.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminho))) {
            instancia = (Biblioteca) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
