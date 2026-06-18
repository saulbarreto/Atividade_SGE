package biblioteca;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TelaLivros extends JDialog {

    private final Biblioteca biblioteca = Biblioteca.getInstance();

    private final JTextField txtCodigo = new JTextField();
    private final JTextField txtTitulo = new JTextField();
    private final JTextField txtAutor = new JTextField();
    private final JTextField txtAno = new JTextField();
    private final JTextField txtPesquisa = new JTextField(20);

    private final JLabel lblContador = new JLabel("Total: 0 livros");

    private final LivroTableModel tableModel = new LivroTableModel();
    private final JTable tabela = new JTable(tableModel);

    public TelaLivros(JFrame owner) {
        super(owner, "Gerenciar Livros", true);
        setSize(700, 500);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(criarPainelNorte(), BorderLayout.NORTH);
        add(criarPainelCentro(), BorderLayout.CENTER);
        add(criarPainelSul(), BorderLayout.SOUTH);

        tabela.setDefaultEditor(Object.class, null);

        atualizarTabela();
        atualizarContador();
    }

    private JPanel criarPainelNorte() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        panel.add(new JLabel("Código"));
        panel.add(txtCodigo);
        panel.add(new JLabel("Título"));
        panel.add(txtTitulo);
        panel.add(new JLabel("Autor"));
        panel.add(txtAutor);
        panel.add(new JLabel("Ano"));
        panel.add(txtAno);

        JButton btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.addActionListener(e -> cadastrar());

        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.addActionListener(e -> limparCampos());

        panel.add(btnCadastrar);
        panel.add(btnLimpar);

        return panel;
    }

    private JScrollPane criarPainelCentro() {
        return new JScrollPane(tabela);
    }

    private JPanel criarPainelSul() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton btnPesquisar = new JButton("Pesquisar");
        btnPesquisar.addActionListener(e -> pesquisar());

        JButton btnListarTodos = new JButton("Listar Todos");
        btnListarTodos.addActionListener(e -> {
            atualizarTabela();
            atualizarContador();
        });

        JButton btnRemover = new JButton("Remover por Código");
        btnRemover.addActionListener(e -> removerPorCodigo());

        panel.add(txtPesquisa);
        panel.add(btnPesquisar);
        panel.add(btnListarTodos);
        panel.add(btnRemover);
        panel.add(lblContador);

        return panel;
    }

    private void cadastrar() {
        if (!validarCampos()) return;

        int codigo = Integer.parseInt(txtCodigo.getText().trim());
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        int ano = Integer.parseInt(txtAno.getText().trim());

        Livro livro = new Livro(codigo, titulo, autor, ano);

        if (biblioteca.adicionarLivro(livro)) {
            JOptionPane.showMessageDialog(this, "Livro cadastrado com sucesso!");
            limparCampos();
            atualizarTabela();
            atualizarContador();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Código já cadastrado.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pesquisar() {
        String texto = txtPesquisa.getText();
        List<Livro> resultado = biblioteca.buscarLivroPorTitulo(texto);
        tableModel.atualizarTabela(resultado);
        atualizarContador();
    }

    private void removerPorCodigo() {
        String codigoStr = txtCodigo.getText().trim();
        if (codigoStr.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Informe o código no campo Código.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int codigo;
        try {
            codigo = Integer.parseInt(codigoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Código inválido.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Deseja remover o livro código " + codigo + "?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao != JOptionPane.YES_OPTION) return;

        Livro livro = biblioteca.buscarLivroPorCodigo(codigo);
        boolean removido = biblioteca.removerLivro(codigo);

        if (removido) {
            JOptionPane.showMessageDialog(this, "Livro removido com sucesso!");
            limparCampos();
            atualizarTabela();
            atualizarContador();
        } else {
            if (livro != null) {
                JOptionPane.showMessageDialog(this,
                        "Livro está emprestado. Devolva primeiro.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Livro não encontrado.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void atualizarTabela() {
        tableModel.atualizarTabela(biblioteca.getLivros());
        atualizarContador();
    }

    private void atualizarContador() {
        int total = tableModel.getRowCount();
        lblContador.setText("Total: " + total + " livros");
    }

    private void limparCampos() {
        txtCodigo.setText("");
        txtTitulo.setText("");
        txtAutor.setText("");
        txtAno.setText("");
        tableModel.atualizarTabela(List.of());
        atualizarContador();
    }

    private boolean validarCampos() {
        String codigo = txtCodigo.getText().trim();
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        String ano = txtAno.getText().trim();

        if (codigo.isBlank() || titulo.isBlank() || autor.isBlank() || ano.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Todos os campos devem ser preenchidos.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int codigoInt;
        try {
            codigoInt = Integer.parseInt(codigo);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Código deve ser um número inteiro.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (codigoInt < 0) {
            JOptionPane.showMessageDialog(this,
                    "Código deve ser um número positivo.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int anoInt;
        try {
            anoInt = Integer.parseInt(ano);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Ano deve ser um número inteiro.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int anoCorrente = LocalDate.now().getYear();
        if (anoInt < 1000 || anoInt > anoCorrente) {
            JOptionPane.showMessageDialog(this,
                    "Ano deve estar entre 1000 e " + anoCorrente + ".",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private static class LivroTableModel extends AbstractTableModel {

        private final String[] colunas = {"Código", "Título", "Autor", "Ano"};
        private List<Livro> livros = List.of();

        public void atualizarTabela(List<Livro> livros) {
            this.livros = livros;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return livros.size();
        }

        @Override
        public int getColumnCount() {
            return colunas.length;
        }

        @Override
        public String getColumnName(int column) {
            return colunas[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Livro livro = livros.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> livro.getCodigo();
                case 1 -> livro.getTitulo();
                case 2 -> livro.getAutor();
                case 3 -> livro.getAno();
                default -> null;
            };
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
