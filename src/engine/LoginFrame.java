package engine;

import auth.*;
import database.*;
import javax.swing.*; // GUI 요소 가져오기
import java.awt.*; // Color, Font 등 가져오기
import java.awt.event.ActionEvent; // 이벤트 처리
import java.awt.event.ActionListener; // 이벤트 리스너

public class LoginFrame extends JFrame {
    private JTextField textID; // ID 입력 필드
    private JPasswordField textPassword; // 비밀번호 입력 필드
    private UserDatabase db; // UserDatabase 객체 (데이터베이스 작업 담당)
    private FindID findID;
    private ResetPassword resetPassword;

    public LoginFrame(UserDatabase db) {
        this.db = db; // 전달받은 UserDatabase 객체를 인스턴스 변수에 저장

        // GUI 구성
        setTitle("Login"); // 창 제목 설정
        setSize(400, 350); // 창 크기 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫을 때 프로그램 종료 설정
        setLocationRelativeTo(null); // 창 위치를 화면 중앙으로 설정
        setLayout(null); // 절대 레이아웃 설정 (위치와 크기를 수동으로 설정)
        setResizable(false); // 사용자가 임의로 프레임 조절 못하게 하기.
        getContentPane().setBackground(Color.black); // 연한 회색으로 설정

        // 폰트 설정 및 색상 설정
        Font font = new Font("Arial", Font.BOLD, 16); // Arial 폰트 설정
        Font titleFont = new Font("Arial", Font.BOLD, 35); // Arial 폰트 설정
        Color textColor = new Color(101, 255, 94); // 텍스트 색상 설정 (어두운 회색)
        Color buttonBorderColor = (Color.BLACK); // 버튼 테두리 색상

        JLabel welcome_string = new JLabel("SPACE INVADERS"); // 환영 메시지 레이블 생성
        welcome_string.setBounds(40, 27, 400, 25); // 레이블 위치 및 크기 설정
        welcome_string.setFont(titleFont); // 폰트 크기 조정
        welcome_string.setForeground(textColor);
        add(welcome_string); // 레이블 추가
        JLabel InstructionsString= new JLabel(" Login and start INVADERS !!"); // 환영 메시지 레이블 생성
        InstructionsString.setBounds(95, 58, 400, 25); // 레이블 위치 및 크기 설정
        InstructionsString.setFont(new Font("Arial", Font.ITALIC, 15)); // 폰트 크기 조정
        InstructionsString.setForeground(Color.GRAY);
        add(InstructionsString); // 레이블 추가

        //Log in and start exploring the world of [게임 이름]!


        JLabel labelID = new JLabel("ID:"); // ID 레이블 생성
        labelID.setBounds(60, 90, 80, 25); // 레이블 위치 및 크기 설정
        labelID.setFont(font); // 폰트 설정
        labelID.setForeground(textColor);
        add(labelID); // 레이블 추가

        textID = new JTextField(20); // ID 입력 필드 생성 (최대 20자)
        textID.setBounds(160, 90, 160, 25); // 위치 및 크기 설정
        textID.setFont(font); // 폰트 설정
        add(textID); // 필드 추가

        JLabel labelPassword = new JLabel("Password:"); // 비밀번호 레이블 생성
        labelPassword.setBounds(60, 130, 80, 25); // 위치 및 크기 설정
        labelPassword.setFont(font); // 폰트 설정
        labelPassword.setForeground(textColor);

        add(labelPassword); // 레이블 추가

        textPassword = new JPasswordField(20); // 비밀번호 입력 필드 생성 (최대 20자)
        textPassword.setBounds(160, 130, 160, 25); // 위치 및 크기 설정
        textPassword.setFont(font); // 폰트 설정


        add(textPassword); // 필드 추가

        JButton loginButton = new JButton("Login"); // 로그인 버튼 생성
        loginButton.setBounds(60, 180, 100, 25); // 위치 및 크기 설정
        loginButton.setForeground(Color.WHITE); // 버튼 텍스트 색상
        loginButton.setBackground(new Color(0, 216, 251)); // 버튼 배경색

        loginButton.setFont(font); // 폰트 설정
        add(loginButton); // 버튼 추가

        JButton registerButton = new JButton("Register"); // 회원가입 버튼 생성
        registerButton.setBounds(210, 180, 110, 25); // 위치 및 크기 설정
        registerButton.setBackground(new Color(0, 216, 251)); // 버튼 배경색

        registerButton.setForeground(Color.WHITE); // 버튼 텍스트 색상
        registerButton.setFont(font); // 폰트 설정
        add(registerButton); // 버튼 추가

        JLabel find_user = new JLabel("Did you forget your ID, Password?"); // 비밀번호 찾기 메시지 레이블 생성
        find_user.setBounds(65, 225, 300, 25); // 레이블 위치 및 크기 설정
        find_user.setFont(font); // 폰트 설정
        find_user.setForeground(Color.GRAY);

        add(find_user); // 레이블 추가
        JButton findIDButton = new JButton("Find ID"); // 아이디 찾기 버튼 생성
        findIDButton.setBounds(60, 265, 100, 25); // 위치 및 크기 설정
        findIDButton.setBackground(Color.DARK_GRAY); // 버튼 배경색
        findIDButton.setForeground(Color.lightGray); // 버튼 텍스트 색상
        add(findIDButton); // 버튼 추가

        JButton resetPasswordButton = new JButton("Reset Password"); // 비밀번호 재설정 버튼 생성
        resetPasswordButton.setBounds(190, 265, 140, 25); // 위치 및 크기 설정
        resetPasswordButton.setBackground(Color.DARK_GRAY); // 버튼 배경색
        resetPasswordButton.setForeground(Color.lightGray); // 버튼 텍스트 색상

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

                    WelcomeFrame aa = new WelcomeFrame(user_name);
                    dispose(); // 로그인 창 닫기
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

        // 아이디 찾기 버튼 동작 정의
        findIDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FindID(db); // 아이디 찾기 창 생성 및 표시
            }
        });

        // 비밀번호 재설정 버튼 동작 정의
        resetPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ResetPassword(db); // 비밀번호 재설정 창 생성 및 표시
            }
        });



        setVisible(true); // 로그인 창 표시
    }
}
