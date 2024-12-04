import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DbConnect {
    private static final String URL = "jdbc:mysql://localhost:3306/gamearras";
    private static final String USER = "root";
    private static final String MOT_DE_PASSE = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, MOT_DE_PASSE);
    }
}