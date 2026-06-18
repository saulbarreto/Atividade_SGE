package biblioteca;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TelaPrincipal extends JFrame {

    private final Biblioteca biblioteca = Biblioteca.getInstance();
    private JLabel lblTotalLivros;
    private JLabel lblTotalUsuarios;
    private JLabel lblEmprestimosAtivos;

    public TelaPrincipal() {
        setTitle("Sistema Gerencial de Biblioteca");
        setSize(700, 400);
        setMinimumSize(new Dimension(700, 400));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                biblioteca.salvarDados("biblioteca.dat");
                dispose();
            }
        });

        setLayout(new BorderLayout());

        // Painel norte - título
        JPanel painelNorte = new JPanel(new GridBagLayout());
        JLabel lblTitulo = new JLabel("Sistema Gerencial de Biblioteca");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        painelNorte.add(lblTitulo);
        add(painelNorte, BorderLayout.NORTH);

        // Painel centro - previews
        JPanel painelCentro = new JPanel(new GridLayout(3, 1, 10, 10));
        painelCentro.setLayout(new GridBagLayout());

        lblTotalLivros = new JLabel("Total de livros cadastrados: " + biblioteca.getLivros().size());
        lblTotalLivros.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblTotalUsuarios = new JLabel("Total de usuários cadastrados: " + biblioteca.getUsuarios().size());
        lblTotalUsuarios.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblEmprestimosAtivos = new JLabel("Livros emprestados no momento: " + biblioteca.getEmprestimosAtivos().size());
        lblEmprestimosAtivos.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel painelPreviews = new JPanel(new GridLayout(3, 1, 10, 10));
        painelPreviews.add(lblTotalLivros);
        painelPreviews.add(lblTotalUsuarios);
        painelPreviews.add(lblEmprestimosAtivos);
        painelCentro.add(painelPreviews);
        add(painelCentro, BorderLayout.CENTER);

        // Painel sul - botões
        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnLivros = new JButton("Gerenciar Livros");
        btnLivros.setPreferredSize(new Dimension(200, 40));
        btnLivros.addActionListener(e -> {
            TelaLivros dialog = new TelaLivros(this);
            dialog.setVisible(true);
            atualizarPreview();
        });

        JButton btnUsuarios = new JButton("Gerenciar Usuários");
        btnUsuarios.setPreferredSize(new Dimension(200, 40));
        btnUsuarios.addActionListener(e -> {
            TelaUsuarios dialog = new TelaUsuarios(this);
            dialog.setVisible(true);
            atualizarPreview();
        });

        JButton btnEmprestimos = new JButton("Gerenciar Empréstimos");
        btnEmprestimos.setPreferredSize(new Dimension(200, 40));
        btnEmprestimos.addActionListener(e -> {
            TelaEmprestimos dialog = new TelaEmprestimos(this);
            dialog.setVisible(true);
            atualizarPreview();
        });

        painelSul.add(btnLivros);
        painelSul.add(btnUsuarios);
        painelSul.add(btnEmprestimos);
        add(painelSul, BorderLayout.SOUTH);
    }

    private void atualizarPreview() {
        lblTotalLivros.setText("Total de livros cadastrados: " + biblioteca.getLivros().size());
        lblTotalUsuarios.setText("Total de usuários cadastrados: " + biblioteca.getUsuarios().size());
        lblEmprestimosAtivos.setText("Livros emprestados no momento: " + biblioteca.getEmprestimosAtivos().size());
    }

    public static void main(String[] args) {
        Biblioteca.carregarDados("biblioteca.dat");
        SwingUtilities.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }
}
