package engine;
import javax.swing.*; // GUI 요소 가져오기
import java.awt.*;
import java.awt.event.ActionEvent; // 이벤트 처리
import java.awt.event.ActionListener; // 이벤트 리스너

public class RegisterFrame extends JFrame {
    private JTextField textID; // ID 입력 필드
    private JPasswordField textPassword; // 비밀번호 입력 필드
    private JTextField textEmail; // 이메일 입력 필드
    private DatabaseManager db; // UserDatabase 객체 (데이터베이스 작업 담당)

    public RegisterFrame(DatabaseManager db) {
        this.db = db; // 전달받은 UserDatabase 객체를 인스턴스 변수에 저장
        Color textColor = new Color(101, 255, 94); // 텍스트 색상 설정 (어두운 회색)
        Font font = new Font("Arial", Font.BOLD, 16); // Arial 폰트 설정

        // GUI 구성
        setTitle("Register"); // 창 제목 설정
        setSize(400, 280); // 창 크기 설정
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창 닫을 때 현재 창만 종료
        getContentPane().setBackground(Color.black); // 검은색으로 설정
        setLocationRelativeTo(null); // 창 위치를 화면 중앙으로 설정
        setLayout(null); // 절대 레이아웃 설정 (위치와 크기를 수동으로 설정)

        JLabel labelID = new JLabel("ID:"); // ID 레이블 생성
        labelID.setBounds(50, 50, 80, 25); // 위치 및 크기 설정
        labelID.setForeground(textColor);
        labelID.setFont(font);
        add(labelID); // 레이블 추가

        textID = new JTextField(20); // ID 입력 필드 생성 (최대 20자)
        textID.setBounds(160, 50, 160, 25); // 위치 및 크기 설정
        textID.setFont(font);
        add(textID); // 필드 추가

        JLabel labelPassword = new JLabel("Password:"); // 비밀번호 레이블 생성
        labelPassword.setBounds(50, 90, 80, 25); // 위치 및 크기 설정
        labelPassword.setForeground(textColor);
        labelPassword.setFont(font);
        add(labelPassword); // 레이블 추가

        textPassword = new JPasswordField(20); // 비밀번호 입력 필드 생성 (최대 20자)
        textPassword.setBounds(160, 90, 160, 25); // 위치 및 크기 설정
        add(textPassword); // 필드 추가

        JLabel labelEmail = new JLabel("Email:"); // 이메일 레이블 생성
        labelEmail.setBounds(50, 130, 80, 25); // 위치 및 크기 설정
        labelEmail.setForeground(textColor);
        labelEmail.setFont(font);
        add(labelEmail); // 레이블 추가

        textEmail = new JTextField(20); // 이메일 입력 필드 생성 (최대 20자)
        textEmail.setBounds(160, 130, 160, 25); // 위치 및 크기 설정
        add(textEmail); // 필드 추가

        JButton registerButton = new JButton("Register"); // 회원가입 버튼 생성
        registerButton.setBounds(150, 180, 110, 25); // 위치 및 크기 설정
        registerButton.setBackground(new Color(0,198,237)); // 버튼 배경색
        registerButton.setForeground(Color.white); // 버튼 배경색
        registerButton.setFont(font);
        add(registerButton); // 버튼 추가

        // 회원가입 버튼 동작 정의
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = textID.getText(); // 입력된 ID 가져오기
                String password = new String(textPassword.getPassword()); // 입력된 비밀번호 가져오기
                String email = textEmail.getText(); // 입력된 이메일 가져오기
                if (id.isEmpty()){
                    JOptionPane.showMessageDialog(null, "You have to enter your id.", "Error", JOptionPane.ERROR_MESSAGE); // 회원가입 실패 메시지 출력

                }
                else if (password.isEmpty()){
                    JOptionPane.showMessageDialog(null, "You have to enter your password.", "Error", JOptionPane.ERROR_MESSAGE); // 회원가입 실패 메시지 출력

                }
                else if (email.isEmpty()){
                    JOptionPane.showMessageDialog(null, "You have to enter your email.", "Error", JOptionPane.ERROR_MESSAGE); // 회원가입 실패 메시지 출력

                }
                else if (db.registerUser(id, password, email)) { // UserDatabase에 사용자 정보 저장
                    JOptionPane.showMessageDialog(null, "Registration successful!"); // 회원가입 성공 메시지 출력
                    dispose(); // 회원가입 창 닫기
                }
                else {
                    JOptionPane.showMessageDialog(null, "Registration failed. ID may already exist.", "Error", JOptionPane.ERROR_MESSAGE); // 회원가입 실패 메시지 출력
                }
            }
        });

        setVisible(true); // 회원가입 창 표시
    }
}