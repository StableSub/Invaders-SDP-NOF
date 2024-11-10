package engine;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Find_ID {
    private UserDatabase db;

    public Find_ID(UserDatabase db) {
        this.db = db;
        createFindIDUI();
    }

    // ID 찾기 UI 생성 메서드
    private void createFindIDUI() {
        JFrame frame = new JFrame("Find ID");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);// 가운데 출력.
        frame.setResizable(false); // 사용자가 임의로 프레임 조절 못하게 하기.
        frame.getContentPane().setBackground(Color.black); // 검은색으로 설정
        Font font = new Font("Arial", Font.BOLD, 16); // Arial 폰트 설정


        JLabel labelEmail = new JLabel("Email:");
        labelEmail.setForeground(new Color(101, 255, 94));
        labelEmail.setBounds(50, 50, 80, 25);
        labelEmail.setFont(font);
        frame.add(labelEmail);

        JTextField textEmail = new JTextField(20);
        textEmail.setBounds(150, 50, 160, 25);
        frame.add(textEmail);

        JButton findButton = new JButton("Find ID");
        findButton.setBounds(150, 100, 100, 25);
        findButton.setBackground(Color.DARK_GRAY); // 버튼 배경색
        findButton.setForeground(Color.lightGray); // 버튼 텍스트 색상
        frame.add(findButton);

        // Find ID 버튼의 액션 리스너
        findButton.addActionListener(e -> {
            String email = textEmail.getText();
            String userId = findIDByEmail(email);
            if (userId != null) {
                JOptionPane.showMessageDialog(frame, "Your ID is: " + userId);
            } else {
                JOptionPane.showMessageDialog(frame, "Email not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    // 이메일로 ID 찾기 메서드
    public String findIDByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";

        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("id"); // 이메일이 일치할 경우 ID 반환
            } else {
                return null; // 일치하는 이메일이 없을 경우 null 반환
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}