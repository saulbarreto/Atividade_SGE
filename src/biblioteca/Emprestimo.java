package biblioteca;

import java.io.Serializable;
import java.time.LocalDate;

public class Emprestimo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Livro livro;
    private Usuario usuario;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao;
    private boolean ativo;

    public Emprestimo(Livro livro, Usuario usuario) {
        this.livro = livro;
        this.usuario = usuario;
        this.dataEmprestimo = LocalDate.now();
        this.dataDevolucao = null;
        this.ativo = true;
    }

    public Livro getLivro() { return livro; }
    public Usuario getUsuario() { return usuario; }
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public boolean isAtivo() { return ativo; }

    public void finalizar() {
        this.ativo = false;
        this.dataDevolucao = LocalDate.now();
    }

    @Override
    public String toString() {
        return livro.getTitulo() + " -> " + usuario.getNome()
                + " | " + dataEmprestimo
                + (ativo ? " (ativo)" : " (devolvido em " + dataDevolucao + ")");
    }
}
