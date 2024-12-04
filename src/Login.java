import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {
    public String verifyLogin (String username, String password) throws SQLException {    //méthode pour vérifier les identifiant et mot de passe
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DbConnect.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)){
            //Remplacement de la requete avec les infos de l'utilisateur
            statement.setString(1, username);
            statement.setString(2, password);


            //Execution de la requete
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()){
                return "Connexion Réussi";
            } else {
                return "Identifiant incorrect";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erreur lors de la connexion à la bdd";
        }
    }
}
