import javax.swing.*;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class ForfaitManager {
    private Timer timer;
    private int remainingTime;

    public void startForfaitTimer(int userId, int forfaitId, JLabel timerLabel) {
        try {
            this.remainingTime = getRemainingTime(userId, forfaitId);
        } catch (SQLException e) {
            timerLabel.setText("Erreur de récupération du forfait");
            return;
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (remainingTime > 0) {
                    remainingTime--;
                    int hours = remainingTime / 3600;
                    int minutes = (remainingTime % 3600) / 60;
                    int seconds = remainingTime % 60;
                    SwingUtilities.invokeLater(() -> timerLabel.setText(String.format("Temps restant: %02d:%02d:%02d", hours, minutes, seconds)));
                } else {
                    timer.cancel();
                    SwingUtilities.invokeLater(() -> timerLabel.setText("Forfait expiré!"));
                }
            }
        }, 0, 1000);
    }

    private int getRemainingTime(int userId, int forfaitId) throws SQLException {
        String query = "SELECT temps_restant FROM reservation WHERE user_id = ? AND forfait_id = ?";

        try (Connection connection = DbConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, forfaitId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String tempsRestantStr = resultSet.getString("temps_restant");
                return convertDurationToSeconds(tempsRestantStr);
            } else {
                return getForfaitDuration(forfaitId);
            }
        }
    }

    private int getForfaitDuration(int forfaitId) throws SQLException {
        String query = "SELECT duration FROM forfait WHERE id = ?";

        try (Connection connection = DbConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, forfaitId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String durationStr = resultSet.getString("duration");
                return convertDurationToSeconds(durationStr);
            } else {
                throw new SQLException("Forfait non trouvé");
            }
        }
    }

    private int convertDurationToSeconds(String duration) {
        if (duration == null || duration.isEmpty()) {
            return 0;
        }

        duration = duration.toLowerCase().trim();
        if (duration.endsWith("h")) {
            return Integer.parseInt(duration.replace("h", "")) * 3600;
        } else if (duration.endsWith("m")) {
            return Integer.parseInt(duration.replace("m", "")) * 60;
        } else {
            return Integer.parseInt(duration) * 60; // Par défaut en minutes
        }
    }

    public void saveRemainingTime(int userId, int forfaitId) {
        String query = "UPDATE reservation SET temps_restant = ? WHERE user_id = ? AND forfait_id = ?";

        try (Connection connection = DbConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            String remainingTimeStr = (remainingTime / 60) + "m";
            statement.setString(1, remainingTimeStr);
            statement.setInt(2, userId);
            statement.setInt(3, forfaitId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
