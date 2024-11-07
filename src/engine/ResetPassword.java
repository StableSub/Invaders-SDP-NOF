package engine;

import javax.swing.*;

public class ResetPassword extends JFrame {
    private JTextField emailField; // 이메일 입력 필드
    private UserDatabase db; // UserDatabase 객체 (데이터베이스 작업 담당)

    public ResetPassword(UserDatabase db) {
        this.db = db; // 전달받은 UserDatabase 객체를 인스턴스 변수에 저장
        createPasswordResetUI();
    }

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

        frame.setVisible(true);
    }

    }

