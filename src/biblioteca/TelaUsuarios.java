package biblioteca;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

public class TelaUsuarios extends JDialog {

    private final Biblioteca biblioteca = Biblioteca.getInstance();

    private JTextField txtId;
    private JTextField txtNome;
    private JTextField txtEmail;
    private JTable tabela;
    private UsuarioTableModel tableModel;
    private JLabel lblContador;

    public TelaUsuarios(JFrame owner) {
        super(owner, "Gerenciar Usu\u00e1rios", true);
        setSize(600, 400);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel painelForm = new JPanel(new GridLayout(4, 2, 10, 10));
        painelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        painelForm.add(new JLabel("ID:"));
        txtId = new JTextField();
        painelForm.add(txtId);

        painelForm.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        painelForm.add(txtNome);

        painelForm.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        painelForm.add(txtEmail);

        JPanel painelBotoesForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnLimpar = new JButton("Limpar");
        painelBotoesForm.add(btnCadastrar);
        painelBotoesForm.add(btnLimpar);
        painelForm.add(new JLabel());
        painelForm.add(painelBotoesForm);

        add(painelForm, BorderLayout.NORTH);

        tableModel = new UsuarioTableModel();
        tabela = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelSul = new JPanel(new FlowLayout());
        JButton btnListarTodos = new JButton("Listar Todos");
        JButton btnRemoverPorId = new JButton("Remover por ID");
        lblContador = new JLabel("Total: 0 usu\u00e1rios");
        painelSul.add(btnListarTodos);
        painelSul.add(btnRemoverPorId);
        painelSul.add(lblContador);
        add(painelSul, BorderLayout.SOUTH);

        btnCadastrar.addActionListener(e -> cadastrar());
        btnLimpar.addActionListener(e -> limparCampos());
        btnListarTodos.addActionListener(e -> atualizarTabela(biblioteca.getUsuarios()));
        btnRemoverPorId.addActionListener(e -> removerPorId());

        atualizarContador();
    }

    private void cadastrar() {
        if (!validarCampos()) return;

        int id = Integer.parseInt(txtId.getText().trim());
        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();

        Usuario usuario = new Usuario(id, nome, email);

        if (biblioteca.adicionarUsuario(usuario)) {
            JOptionPane.showMessageDialog(this, "Usu\u00e1rio cadastrado com sucesso!");
            limparCampos();
            atualizarTabela(biblioteca.getUsuarios());
        } else {
            JOptionPane.showMessageDialog(this, "ID j\u00e1 cadastrado.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removerPorId() {
        String idStr = JOptionPane.showInputDialog(this, "Digite o ID do usu\u00e1rio a remover:");
        if (idStr == null || idStr.isBlank()) return;

        try {
            int id = Integer.parseInt(idStr.trim());

            Usuario usuario = biblioteca.buscarUsuarioPorId(id);
            if (usuario == null) {
                JOptionPane.showMessageDialog(this, "Usu\u00e1rio n\u00e3o encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja remover o usu\u00e1rio \"" + usuario.getNome() + "\"?",
                    "Confirmar Remo\u00e7\u00e3o", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            if (biblioteca.removerUsuario(id)) {
                JOptionPane.showMessageDialog(this, "Usu\u00e1rio removido com sucesso!");
                atualizarTabela(biblioteca.getUsuarios());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Usu\u00e1rio possui empr\u00e9stimos ativos. N\u00e3o \u00e9 poss\u00edvel remover.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inv\u00e1lido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        String id = txtId.getText().trim();
        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();

        if (id.isBlank() || nome.isBlank() || email.isBlank()) {
            JOptionPane.showMessageDialog(this, "Todos os campos s\u00e3o obrigat\u00f3rios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Integer.parseInt(id);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID deve ser um n\u00famero inteiro.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!email.contains("@") || email.indexOf('@') == email.length() - 1) {
            JOptionPane.showMessageDialog(this, "Email inv\u00e1lido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void limparCampos() {
        txtId.setText("");
        txtNome.setText("");
        txtEmail.setText("");
        tableModel.atualizarTabela(List.of());
        atualizarContador();
    }

    private void atualizarTabela(List<Usuario> usuarios) {
        tableModel.atualizarTabela(usuarios);
        atualizarContador();
    }

    private void atualizarContador() {
        int total = biblioteca.getUsuarios().size();
        lblContador.setText("Total: " + total + " usu\u00e1rios");
    }

    private class UsuarioTableModel extends AbstractTableModel {
        private final String[] colunas = {"ID", "Nome", "Email"};
        private List<Usuario> dados = List.of();

        @Override
        public int getRowCount() {
            return dados.size();
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
            Usuario u = dados.get(rowIndex);
            switch (columnIndex) {
                case 0: return u.getId();
                case 1: return u.getNome();
                case 2: return u.getEmail();
                default: return null;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        public void atualizarTabela(List<Usuario> novaLista) {
            this.dados = novaLista;
            fireTableDataChanged();
        }
    }
}
