package engine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class welcome_frame extends JFrame {
    static boolean start = true;




    public void wellcome(String user_name){

        setTitle(" Welcome frame "); // 창 제목 설정
        setSize(400, 250); // 창 크기 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫을 때 프로그램 종료 설정
        setLocationRelativeTo(null); // 창 위치를 화면 중앙으로 설정
        setLayout(null); // 절대 레이아웃 설정 (위치와 크기를 수동으로 설정)




        JLabel welcomeLabel = new JLabel("Welcome to invaders !!");
        welcomeLabel.setBounds(0, 50, 400, 25); // x: 0, y: 50, 너비: 400, 높이: 25
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER); // 수평 정렬 중앙 설정
        add(welcomeLabel); // 레이블 추가

        JLabel labelID = new JLabel(user_name); // ID 레이블 생성
        labelID.setBounds(0, 80, 400, 25); // x: 0, y: 80, 너비: 400, 높이: 25
        labelID.setHorizontalAlignment(SwingConstants.CENTER); // 수평 정렬 중앙 설정
        add(labelID); // 레이블 추가

        JButton nextButton = new JButton("next"); // 회원가입 버튼 생성
        nextButton.setBounds(250, 150, 100, 25); // 위치 및 크기 설정
        add(nextButton); // 버튼 추가

        JButton skipButton = new JButton("skip"); // 회원가입 버튼 생성
        skipButton.setBounds(50, 150, 100, 25); // 위치 및 크기 설정
        add(skipButton); // 버튼 추가



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
