import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CodeVerification extends JFrame {
    private JTextField codeField;
    private JLabel messageLabel;
    private JLabel timerLabel;
    private int userId;
    private int forfaitId;
    private ForfaitManager forfaitManager;

    public CodeVerification(int userId) {
        this.userId = userId;
        this.forfaitManager = new ForfaitManager();

        setTitle("Vérification du Code");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));

        codeField = new JTextField();
        JButton verifyButton = new JButton("Valider");
        messageLabel = new JLabel("", JLabel.CENTER);
        messageLabel.setForeground(Color.RED);
        timerLabel = new JLabel("", JLabel.CENTER);

        panel.add(codeField);
        panel.add(verifyButton);
        panel.add(timerLabel);

        add(panel, BorderLayout.CENTER);
        add(messageLabel, BorderLayout.SOUTH);

        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    verifyCode();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                forfaitManager.saveRemainingTime(userId, forfaitId);
            }
        });
    }

    private void verifyCode() throws SQLException {
        String enteredCode = codeField.getText();
        String query = "SELECT access_code, forfait_id FROM reservation WHERE user_id = ?";

        try (Connection connection = DbConnect.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedCode = resultSet.getString("access_code");
                this.forfaitId = resultSet.getInt("forfait_id");

                if (enteredCode.equals(storedCode)) {
                    messageLabel.setForeground(Color.GREEN);
                    messageLabel.setText("Code valide, accès autorisé!");
                    forfaitManager.startForfaitTimer(userId, forfaitId, timerLabel);
                } else {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Code incorrect!");
                }
            }
        }
    }
}
