package biblioteca;

import java.io.Serializable;
import java.util.Objects;

public class Livro implements Serializable {

    private static final long serialVersionUID = 1L;

    private int codigo;
    private String titulo;
    private String autor;
    private int ano;

    public Livro(int codigo, String titulo, String autor, int ano) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.ano = ano;
    }

    public int getCodigo() { return codigo; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getAno() { return ano; }

    @Override
    public String toString() {
        return codigo + " - " + titulo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Livro livro = (Livro) o;
        return codigo == livro.codigo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
