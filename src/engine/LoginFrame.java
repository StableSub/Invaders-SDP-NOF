package engine;

import javax.swing.*; // GUI 요소 가져오기
import java.awt.event.ActionEvent; // 이벤트 처리
import java.awt.event.ActionListener; // 이벤트 리스너

public class LoginFrame extends JFrame {
    private JTextField textID; // ID 입력 필드
    private JPasswordField textPassword; // 비밀번호 입력 필드
    private UserDatabase db; // UserDatabase 객체 (데이터베이스 작업 담당)

    public LoginFrame(UserDatabase db) {
        this.db = db; // 전달받은 UserDatabase 객체를 인스턴스 변수에 저장

        // GUI 구성
        setTitle("Login"); // 창 제목 설정
        setSize(400, 250); // 창 크기 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫을 때 프로그램 종료 설정
        setLocationRelativeTo(null); // 창 위치를 화면 중앙으로 설정
        setLayout(null); // 절대 레이아웃 설정 (위치와 크기를 수동으로 설정)

        JLabel labelID = new JLabel("ID:"); // ID 레이블 생성
        labelID.setBounds(50, 50, 80, 25); // 레이블 위치 및 크기 설정
        add(labelID); // 레이블 추가

        textID = new JTextField(20); // ID 입력 필드 생성 (최대 20자)
        textID.setBounds(150, 50, 160, 25); // 위치 및 크기 설정
        add(textID); // 필드 추가

        JLabel labelPassword = new JLabel("Password:"); // 비밀번호 레이블 생성
        labelPassword.setBounds(50, 90, 80, 25); // 위치 및 크기 설정
        add(labelPassword); // 레이블 추가

        textPassword = new JPasswordField(20); // 비밀번호 입력 필드 생성 (최대 20자)
        textPassword.setBounds(150, 90, 160, 25); // 위치 및 크기 설정
        add(textPassword); // 필드 추가

        JButton loginButton = new JButton("Login"); // 로그인 버튼 생성
        loginButton.setBounds(50, 150, 100, 25); // 위치 및 크기 설정
        add(loginButton); // 버튼 추가

        JButton registerButton = new JButton("Register"); // 회원가입 버튼 생성
        registerButton.setBounds(200, 150, 100, 25); // 위치 및 크기 설정
        add(registerButton); // 버튼 추가

        JButton findIDButton = new JButton("Find ID"); // 아이디 찾기 버튼 생성
        findIDButton.setBounds(150, 200, 100, 25); // 위치 및 크기 설정
        add(findIDButton); // 버튼 추가

        JButton resetPasswordButton = new JButton("Reset Password"); // 비밀번호 재설정 버튼 설정
        resetPasswordButton.setBounds(250, 200, 150, 25); // 위치 및 크기 설정
        add(resetPasswordButton); // 버튼 추가


        // 로그인 버튼 동작 정의
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = textID.getText(); // 입력된 ID 가져오기
                String password = new String(textPassword.getPassword()); // 입력된 비밀번호 가져오기

                if (db.loginUser(id, password)) { // UserDatabase에서 로그인 성공 여부 확인
                    JOptionPane.showMessageDialog(null, "Login successful!"); // 로그인 성공 메시지 출력
                    String user_name = id;

                    welcome_frame aa = new welcome_frame(user_name);


                    dispose(); // 로그인 창 닫기
                    // 로그인 후 게임 시작 로직 추가 가능
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid ID or Password", "Error", JOptionPane.ERROR_MESSAGE); // 로그인 실패 메시지 출력
                }
            }
        });

        // 회원가입 버튼 동작 정의
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterFrame(db); // 회원가입 창 생성 및 표시
            }
        });
        // 비밀번호 재설정 버튼 동작 정의
        resetPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ResetPassword_ID(db); // 비밀번호 재설정 창 생성 및 표시
            }
        });

        setVisible(true); // 로그인 창 표시
    }
}
