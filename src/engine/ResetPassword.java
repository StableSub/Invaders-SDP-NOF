package engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ResetPassword {

    private DatabaseManager db;

    public ResetPassword(DatabaseManager db) {
        this.db = db;
        createPasswordResetUI();
    }

    // 비밀번호 재설정 UI 생성 메서드
    private void createPasswordResetUI() {
        JFrame frame = new JFrame("Find and Reset Password");
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);// 가운데 출력.
        frame.getContentPane().setBackground(Color.black); // 검은색으로 설정
        Color textColor = new Color(101, 255, 94); // 텍스트 색상 설정 (어두운 회색)
        Font font = new Font("Arial", Font.BOLD, 13); // Arial 폰트 설정



        JLabel labelID = new JLabel("ID:");
        labelID.setBounds(50, 30, 80, 25);
        labelID.setForeground(textColor);
        labelID.setFont(font);
        frame.add(labelID);

        JTextField textID = new JTextField(20);
        textID.setBounds(170, 30, 160, 25);
        frame.add(textID);

        JLabel labelEmail = new JLabel("Email:");
        labelEmail.setBounds(50, 70, 80, 25);
        labelEmail.setForeground(textColor);
        labelEmail.setFont(font);
        frame.add(labelEmail);

        JTextField textEmail = new JTextField(20);
        textEmail.setBounds(170, 70, 160, 25);
        frame.add(textEmail);

        JLabel labelNewPassword = new JLabel("New Password:");
        labelNewPassword.setBounds(50, 110, 100, 25);
        labelNewPassword.setForeground(textColor);
        labelNewPassword.setFont(font);
        frame.add(labelNewPassword);

        JPasswordField textNewPassword = new JPasswordField(20);
        textNewPassword.setBounds(170, 110, 160, 25);
        frame.add(textNewPassword);

        JButton resetButton = new JButton("Reset Password");
        resetButton.setBounds(130, 160, 140, 30);
        resetButton.setBackground(Color.DARK_GRAY); // 버튼 배경색
        resetButton.setForeground(Color.lightGray); // 버튼 텍스트 색상
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
        String selectSql = "SELECT id FROM user_account WHERE id = ? AND email = ?";
        String updateSql = "UPDATE user_account SET password = ? WHERE id = ?";

        try (Connection conn = db.connect();
             PreparedStatement pstmtSelect = conn.prepareStatement(selectSql);
             PreparedStatement pstmtUpdate = conn.prepareStatement(updateSql)) {

            pstmtSelect.setString(1, id);
            pstmtSelect.setString(2, db.hashValue(email));
            ResultSet rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                String encryptedPassword = db.hashValue(newPassword);
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