import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class Login {
    private int userId; // Stocker l'ID de l'utilisateur connecté

    public int getUserId() {
        return userId;
    }

    public String verifyLogin(String username, String password) throws SQLException {
        String query = "SELECT id, password FROM user WHERE email = ?";

        try (Connection connection = DbConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedHashedPassword = resultSet.getString("password"); // Récupère le mot de passe haché
                this.userId = resultSet.getInt("id"); // Récupère l'ID de l'utilisateur connecté

                // Vérifie que le mot de passe en base de données a été généré par Symfony (format $2y$)
                if (storedHashedPassword != null && storedHashedPassword.startsWith("$2y$")) {
                    // Remplace $2y$ par $2a$ pour garantir compatibilité avec Java BCrypt
                    storedHashedPassword = storedHashedPassword.replace("$2y$", "$2a$");
                }

                // Vérifie le mot de passe fourni avec celui stocké
                if (BCrypt.checkpw(password, storedHashedPassword)) {
                    return "Connexion Réussie";
                } else {
                    return "Identifiant ou mot de passe incorrect";
                }
            } else {
                return "Identifiant ou mot de passe incorrect";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erreur lors de la connexion à la BDD";
        }
    }
}