import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Window extends JFrame {
    private JTextField userField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private Login loginController;

    public Window() {
        loginController = new Login();

        // Configuration de la fenêtre
        setTitle("Connexion");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centre la fenêtre
        setResizable(false);

        // Création du panneau principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        // Création des composants de l'interface
        JLabel userLabel = new JLabel("Nom d'utilisateur:");
        userField = new JTextField();
        JLabel passwordLabel = new JLabel("Mot de passe:");
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Connexion");
        messageLabel = new JLabel("", JLabel.CENTER); // Label pour afficher les messages
        messageLabel.setForeground(Color.RED);

        // Ajout des composants au panneau
        panel.add(userLabel);
        panel.add(userField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // Espace vide
        panel.add(loginButton);

        // Ajout du panneau et du label des messages à la fenêtre
        add(panel, BorderLayout.CENTER);
        add(messageLabel, BorderLayout.SOUTH);

        // Gestionnaire d'événements pour le bouton "Connexion"
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    handleLogin();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    // Méthode pour gérer la connexion
    private void handleLogin() throws SQLException {
        // Récupérer les données saisies par l'utilisateur
        String username = userField.getText();
        String password = new String(passwordField.getPassword());

        String message = loginController.verifyLogin(username, password);

        // Affichage du message (succès ou erreur)
        if (message.equals("Connexion réussie !")) {
            messageLabel.setForeground(Color.GREEN);
        } else {
            messageLabel.setForeground(Color.RED);
        }
        messageLabel.setText(message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Window window = new Window();
            window.setVisible(true);
        });
    }
}