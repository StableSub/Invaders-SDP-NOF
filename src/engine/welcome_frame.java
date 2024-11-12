package engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class welcome_frame extends JFrame {
    static boolean start = true;




    public void wellcome(String user_name){

        setTitle(" Welcome frame "); // 창 제목 설정
        setSize(500, 400); // 창 크기 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫을 때 프로그램 종료 설정
        setLocationRelativeTo(null); // 창 위치를 화면 중앙으로 설정
        setResizable(false);
        getContentPane().setBackground(Color.black); // 연한 회색으로 설정

        setLayout(null); // 절대 레이아웃 설정 (위치와 크기를 수동으로 설정)
        Font titleFont = new Font("Arial", Font.BOLD, 35); // Arial 폰트 설정
        Color textColor = new Color(101, 255, 94); // 텍스트 색상 설정 (어두운 회색)



        JLabel welcomeLabel = new JLabel("Welcome to Invaders !!");
        welcomeLabel.setBounds(50, 50, 400, 25); // x: 0, y: 50, 너비: 400, 높이: 25
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER); // 수평 정렬 중앙 설정
        welcomeLabel.setFont(titleFont);
        welcomeLabel.setForeground(textColor);
        add(welcomeLabel); // 레이블 추가

        JLabel labelID = new JLabel(user_name); // 사용자 이름을 레이블로 생성
        labelID.setFont(titleFont);
        labelID.setForeground(textColor);
        labelID.setHorizontalAlignment(SwingConstants.CENTER); // 중앙 정렬

        // 텍스트 길이에 맞춰 중앙 정렬 계산
        FontMetrics fm = getFontMetrics(titleFont);
        int labelWidth = fm.stringWidth(user_name); // 텍스트 길이에 따른 너비 계산
        labelID.setBounds((getWidth() - labelWidth) / 2, 100, labelWidth, 25); // 중앙 정렬
        add(labelID); // 레이블 추가

        Font secFont = new Font("Arial", Font.ITALIC, 20); // Arial 폰트 설정
        String explain = "Press the button you want";
        JLabel explainString = new JLabel(explain); // 사용자 이름을 레이블로 생성
        FontMetrics second = getFontMetrics(secFont);
        int len = second.stringWidth(explain);
        explainString.setFont(secFont);
        explainString.setForeground(Color.ORANGE);
        explainString.setHorizontalAlignment(SwingConstants.CENTER); // 중앙 정렬
        explainString.setBounds(130,150,len+10,25);
        add(explainString);


        JButton skipButton = new JButton("skip"); // 회원가입 버튼 생성
        skipButton.setBounds(80, 280, 100, 25); // 위치 및 크기 설정
        skipButton.setBackground(new Color(0, 216, 251)); // 버튼 배경색
        skipButton.setForeground(Color.WHITE); // 버튼 텍스트 색상
        skipButton.setFont(new Font("Arial", Font.BOLD, 16)); // 폰트 설정
        add(skipButton); // 버튼 추가

        JLabel explainSkip1 = new JLabel("Skip Story & Guide");
        explainSkip1.setBounds(60, 220, 190, 25);
        explainSkip1.setForeground(Color.GRAY);
        explainSkip1.setFont(new Font("Arial", Font.BOLD, 15));
        add(explainSkip1);

        JLabel explainSkip2 = new JLabel("and Start Game");
        explainSkip2.setBounds(75, 240, 190, 25);
        explainSkip2.setForeground(Color.GRAY);
        explainSkip2.setFont(new Font("Arial", Font.BOLD, 15));
        add(explainSkip2);



        JButton nextButton = new JButton("next"); // 회원가입 버튼 생성
        nextButton.setBounds(310, 280, 100, 25); // 위치 및 크기 설정
        nextButton.setBackground(new Color(0, 216, 251)); // 버튼 배경색
        nextButton.setForeground(Color.WHITE); // 버튼 텍스트 색상
        nextButton.setFont(new Font("Arial", Font.BOLD, 16)); // 폰트 설정
        add(nextButton); // 버튼 추가
//Get to Know the Story and Gameplay
        JLabel explainNext1 = new JLabel("Get to Know");
        explainNext1.setBounds(315, 220, 190, 25);
        explainNext1.setForeground(Color.GRAY);
        explainNext1.setFont(new Font("Arial", Font.BOLD, 15));
        add(explainNext1);

        JLabel explainNext2 = new JLabel("the Story & Gameplay");
        explainNext2.setBounds(280, 240, 190, 25);
        explainNext2.setForeground(Color.GRAY);
        explainNext2.setFont(new Font("Arial", Font.BOLD, 15));
        add(explainNext2);





        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setStart(false); // start 값을 false로 설정
                dispose();
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setStart(true); // start 값을 true로 설정
                Explain_story();
            }
        });

        // Panel 추가
        JPanel drawingPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g); // 기본 그리기 호출
                Graphics2D g2d1 = (Graphics2D) g; // Graphics 객체를 Graphics2D로 캐스팅
                // 선 두께 설정
                g2d1.setStroke(new BasicStroke(4)); // 선 두께를 5로 설정
                g2d1.setColor(Color.GREEN);
                g2d1.drawRect(40, 200, 180, 120); // 사각형 그리기
                Graphics2D g2d2 = (Graphics2D) g; // Graphics 객체를 Graphics2D
                g2d2.setStroke(new BasicStroke(4)); // 선 두께를 4로 설정
                g2d2.setColor(Color.GREEN);
                g2d2.drawRect(270, 200, 180, 120); // 사각형 그리기
            }
        };
        drawingPanel.setBounds(0, 0, 500, 400); // Panel 크기 설정
        drawingPanel.setBackground(Color.black); // 연한 회색으로 설정

        add(drawingPanel);

        setVisible(true); // 로그인 창 표시

    }

    public void Explain_story() {
        JFrame storyFrame = new JFrame(); // 새로운 JFrame 생성
        storyFrame.setTitle(" invaders_story "); // 창 제목 설정
        storyFrame.setSize(400, 250); // 창 크기 설정
        storyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫을 때 프로그램 종료 설정
        storyFrame.setLocationRelativeTo(null); // 창 위치를 화면 중앙으로 설정
        storyFrame.setLayout(null); // 절대 레이아웃 설정

        JLabel temp_string = new JLabel("fucking invaders");
        temp_string.setBounds(0, 50, 400, 25); // x: 0, y: 50, 너비: 400, 높이: 25
        temp_string.setHorizontalAlignment(SwingConstants.CENTER); // 수평 정렬 중앙 설정
        storyFrame.add(temp_string); // 레이블 추가
        storyFrame.setVisible(true); // 새로운 창 표시
        setStart(false);
        dispose();
    }

    public static Boolean getStart(){
        return start;
    }

    public void setStart(boolean start){
        this.start = start;

    }

    public welcome_frame(String user_name){
        wellcome(user_name);
    }
}