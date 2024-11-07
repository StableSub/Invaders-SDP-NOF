package engine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ResetPassword_ID {

    private UserDatabase db;

    public ResetPassword_ID(UserDatabase db) {
        this.db = db;
        createPasswordResetUI();
    }

    // 비밀번호 재설정 UI 생성 메서드
    private void createPasswordResetUI() {
        JFrame frame = new JFrame("Find and Reset Password");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);

        JLabel labelID = new JLabel("ID:");
        labelID.setBounds(50, 30, 80, 25);
        frame.add(labelID);

        JTextField textID = new JTextField(20);
        textID.setBounds(150, 30, 160, 25);
        frame.add(textID);

        JLabel labelEmail = new JLabel("Email:");
        labelEmail.setBounds(50, 70, 80, 25);
        frame.add(labelEmail);

        JTextField textEmail = new JTextField(20);
        textEmail.setBounds(150, 70, 160, 25);
        frame.add(textEmail);

        JLabel labelNewPassword = new JLabel("New Password:");
        labelNewPassword.setBounds(50, 110, 100, 25);
        frame.add(labelNewPassword);

        JPasswordField textNewPassword = new JPasswordField(20);
        textNewPassword.setBounds(150, 110, 160, 25);
        frame.add(textNewPassword);

        JButton resetButton = new JButton("Reset Password");
        resetButton.setBounds(150, 160, 140, 30);
        frame.add(resetButton);

        // 비밀번호 재설정 버튼 동작 정의
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = textID.getText();
                String email = textEmail.getText();
                String newPassword = new String(textNewPassword.getPassword());

                if (finder_PASSWORD(id, email, newPassword)) {
                    JOptionPane.showMessageDialog(frame, "Password has been reset successfully.");
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "ID or Email not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }

    // 비밀번호 찾기 및 재설정 메서드
    public boolean finder_PASSWORD(String id, String email, String newPassword) {
        String selectSql = "SELECT id FROM users WHERE id = ? AND email = ?";
        String updateSql = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection conn = db.connect();
             PreparedStatement pstmtSelect = conn.prepareStatement(selectSql);
             PreparedStatement pstmtUpdate = conn.prepareStatement(updateSql)) {

            pstmtSelect.setString(1, id);
            pstmtSelect.setString(2, email);
            ResultSet rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                String encryptedPassword = db.hashPassword(newPassword);
                pstmtUpdate.setString(1, encryptedPassword);
                pstmtUpdate.setString(2, id);
                pstmtUpdate.executeUpdate();

                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}