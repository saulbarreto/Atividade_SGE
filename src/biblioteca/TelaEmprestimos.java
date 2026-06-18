package biblioteca;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TelaEmprestimos extends JDialog {

    private final Biblioteca biblioteca = Biblioteca.getInstance();

    private JComboBox<Livro> cmbLivros;
    private JComboBox<Usuario> cmbUsuarios;
    private JComboBox<Livro> cmbDevolver;
    private JTextArea txtLogEmprestar;
    private JTextArea txtLogDevolver;
    private JTable tabelaRelatorio;
    private EmprestimoTableModel tableModel;

    public TelaEmprestimos(JFrame owner) {
        super(owner, "Gerenciar Empr\u00e9stimos", true);
        setSize(700, 500);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Emprestar", criarPainelEmprestar());
        tabbedPane.addTab("Devolver", criarPainelDevolver());
        tabbedPane.addTab("Relat\u00f3rio", criarPainelRelatorio());

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 2) {
                atualizarRelatorio();
            }
        });

        add(tabbedPane);
        carregarCombos();
    }

    private JPanel criarPainelEmprestar() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.add(new JLabel("Livro:"));
        cmbLivros = new JComboBox<>();
        form.add(cmbLivros);
        form.add(new JLabel("Usu\u00e1rio:"));
        cmbUsuarios = new JComboBox<>();
        form.add(cmbUsuarios);
        form.add(new JLabel());
        JButton btnEmprestar = new JButton("Realizar Empr\u00e9stimo");
        btnEmprestar.addActionListener(e -> realizarEmprestimo());
        form.add(btnEmprestar);

        panel.add(form, BorderLayout.NORTH);

        txtLogEmprestar = new JTextArea(20, 40);
        txtLogEmprestar.setEditable(false);
        panel.add(new JScrollPane(txtLogEmprestar), BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarPainelDevolver() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.add(new JLabel("Livro:"));
        cmbDevolver = new JComboBox<>();
        form.add(cmbDevolver);
        form.add(new JLabel());
        JButton btnDevolver = new JButton("Realizar Devolu\u00e7\u00e3o");
        btnDevolver.addActionListener(e -> realizarDevolucao());
        form.add(btnDevolver);

        panel.add(form, BorderLayout.NORTH);

        txtLogDevolver = new JTextArea(20, 40);
        txtLogDevolver.setEditable(false);
        panel.add(new JScrollPane(txtLogDevolver), BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarPainelRelatorio() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        tableModel = new EmprestimoTableModel();
        tabelaRelatorio = new JTable(tableModel);
        panel.add(new JScrollPane(tabelaRelatorio), BorderLayout.CENTER);

        JButton btnAtualizar = new JButton("Atualizar Relat\u00f3rio");
        btnAtualizar.addActionListener(e -> atualizarRelatorio());
        panel.add(btnAtualizar, BorderLayout.SOUTH);

        return panel;
    }

    private void realizarEmprestimo() {
        Livro livro = (Livro) cmbLivros.getSelectedItem();
        Usuario usuario = (Usuario) cmbUsuarios.getSelectedItem();
        if (livro == null || usuario == null) {
            log(txtLogEmprestar, "Selecione um livro e um usu\u00e1rio");
            return;
        }
        if (biblioteca.realizarEmprestimo(livro, usuario)) {
            log(txtLogEmprestar, "Empr\u00e9stimo realizado: " + livro.getTitulo() + " -> " + usuario.getNome());
        } else {
            log(txtLogEmprestar, "Este livro j\u00e1 est\u00e1 emprestado.");
        }
        carregarCombos();
    }

    private void realizarDevolucao() {
        Livro livro = (Livro) cmbDevolver.getSelectedItem();
        if (livro == null) {
            log(txtLogDevolver, "Selecione um livro");
            return;
        }
        if (biblioteca.realizarDevolucao(livro)) {
            log(txtLogDevolver, "Devolu\u00e7\u00e3o realizada: " + livro.getTitulo());
        } else {
            log(txtLogDevolver, "Este livro n\u00e3o consta como emprestado.");
        }
        carregarCombos();
    }

    private void carregarCombos() {
        cmbLivros.removeAllItems();
        for (Livro livro : biblioteca.getLivrosDisponiveis()) {
            cmbLivros.addItem(livro);
        }

        cmbUsuarios.removeAllItems();
        for (Usuario usuario : biblioteca.getUsuarios()) {
            cmbUsuarios.addItem(usuario);
        }

        cmbDevolver.removeAllItems();
        for (Livro livro : biblioteca.getLivrosEmprestados()) {
            cmbDevolver.addItem(livro);
        }
    }

    private void atualizarRelatorio() {
        tableModel.setEmprestimos(biblioteca.getEmprestimos());
    }

    private void log(JTextArea area, String msg) {
        area.append(msg + "\n");
    }

    private static class EmprestimoTableModel extends AbstractTableModel {
        private final String[] colunas = {"Livro", "Usu\u00e1rio", "Data Empr\u00e9stimo", "Data Devolu\u00e7\u00e3o", "Status"};
        private List<Emprestimo> emprestimos = List.of();

        public void setEmprestimos(List<Emprestimo> emprestimos) {
            this.emprestimos = emprestimos;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return emprestimos.size();
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
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Emprestimo e = emprestimos.get(rowIndex);
            switch (columnIndex) {
                case 0: return e.getLivro().getTitulo();
                case 1: return e.getUsuario().getNome();
                case 2: return e.getDataEmprestimo().toString();
                case 3: {
                    LocalDate data = e.getDataDevolucao();
                    return data != null ? data.toString() : "-";
                }
                case 4: return e.isAtivo() ? "Ativo" : "Devolvido";
                default: return null;
            }
        }
    }
}
