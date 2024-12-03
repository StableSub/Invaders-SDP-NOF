package blackjack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import engine.manager.*;
import engine.utility.Sound;

public class BlackJackScreen {
    private final JFrame frame;
    private final JPanel statusPanel;
    private final JLabel actionMessageLabel;
    private final JLabel gamerStatusLabel;
    private final JLabel dealerStatusLabel;

    // 카드와 점수를 관리하기 위한 변수
    private String gamerCards = "";
    private int gamerScore = 0;
    private String dealerCards = "";
    private int dealerScore = 0;
    private boolean isRunning = true; // 게임 실행 상태 플래그

    // InputManager 및 SoundManager 인스턴스
    private final InputManager inputManager = InputManager.getInstance();
    private final SoundManager soundManager = SoundManager.getInstance();

    public BlackJackScreen() {
        frame = new JFrame("BlackJack Game");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 상단 패널: 제목
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(Color.DARK_GRAY);

        JLabel titleLabel = new JLabel("BlackJack Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel);

        frame.add(topPanel, BorderLayout.NORTH);

        // 상태 패널: 플레이어 및 딜러 상태 표시
        statusPanel = new JPanel(new GridLayout(3, 1));
        statusPanel.setBackground(Color.LIGHT_GRAY);

        gamerStatusLabel = new JLabel("Your Cards: [] | Your Score: 0", SwingConstants.CENTER);
        gamerStatusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        statusPanel.add(gamerStatusLabel);

        dealerStatusLabel = new JLabel("Dealer's Cards: [] | Dealer's Score: 0", SwingConstants.CENTER);
        dealerStatusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        statusPanel.add(dealerStatusLabel);

        actionMessageLabel = new JLabel("", SwingConstants.CENTER);
        actionMessageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        actionMessageLabel.setForeground(Color.RED);
        statusPanel.add(actionMessageLabel);

        frame.add(statusPanel, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.setBackground(Color.DARK_GRAY);

        JButton hitButton = createStyledButton("Hit", e -> hitAction());
        JButton standButton = createStyledButton("Stand", e -> standAction());
        JButton exitButton = createStyledButton("Exit", e -> exitAction());

        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        buttonPanel.add(exitButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // 창 포커스 이벤트 리스너 추가: 키 상태 초기화
        frame.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                // 포커스 획득 시 처리할 동작 없음
            }

            @Override
            public void windowLostFocus(java.awt.event.WindowEvent e) {
                inputManager.resetKeys(); // 키 상태 초기화
            }
        });

        // 창이 닫힐 때 사운드 전환 및 키 초기화
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                soundManager.stopSound(Sound.BGM_GAMBLING);
                soundManager.loopSound(Sound.BGM_MAIN);
                inputManager.resetKeys();
            }
        });

        frame.setVisible(true);

        // 초기 상태 업데이트
        startGame();
    }

    private JButton createStyledButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.addActionListener(actionListener);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(0, 87, 179));
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(0, 123, 255));
            }
        });

        return button;
    }

    private void startGame() {
        // 초기 카드 배분
        gamerCards = "[7 of Hearts, 5 of Diamonds]";
        gamerScore = 12;
        dealerCards = "[10 of Spades]";
        dealerScore = 10;

        updateGameStatus("Game Started! Good luck!");
    }

    private void hitAction() {
        if (!isRunning) return; // 실행 중복 방지
        isRunning = false; // 동작 중 플래그 설정

        // 히트 동작 (예시로 카드 추가)
        gamerCards += ", 4 of Clubs";
        gamerScore += 4;

        if (gamerScore > 21) {
            updateGameStatus("Bust! Your score exceeded 21.");
            gamerStatusLabel.setText("Your Cards: " + gamerCards + " | Your Score: " + gamerScore);
            endGame();
        } else {
            updateGameStatus("You chose to Hit! Drawing a card...");
        }

        gamerStatusLabel.setText("Your Cards: " + gamerCards + " | Your Score: " + gamerScore);

        // 버튼 활성화 타이머
        Timer timer = new Timer(1000, e -> isRunning = true);
        timer.setRepeats(false);
        timer.start();
    }

    private void standAction() {
        if (!isRunning) return; // 실행 중복 방지
        isRunning = false; // 동작 중 플래그 설정

        // 스탠드 동작 (딜러 카드 추가)
        dealerCards += ", 7 of Diamonds";
        dealerScore += 7;

        updateGameStatus("You chose to Stand. Dealer's turn...");
        dealerStatusLabel.setText("Dealer's Cards: " + dealerCards + " | Dealer's Score: " + dealerScore);

        endGame();

        // 버튼 활성화 타이머
        Timer timer = new Timer(1000, e -> isRunning = true);
        timer.setRepeats(false);
        timer.start();
    }

    private void exitAction() {
        // 게임 종료 시 사운드 전환
        soundManager.stopSound(Sound.BGM_GAMBLING);
        soundManager.loopSound(Sound.BGM_MAIN);

        updateGameStatus("Exiting the game...");
        frame.dispose();
    }

    private void updateGameStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            actionMessageLabel.setText(message);

            // 메시지를 2초 후에 지우는 타이머
            Timer timer = new Timer(2000, e -> actionMessageLabel.setText(""));
            timer.setRepeats(false);
            timer.start();
        });
    }

    private void endGame() {
        String winner = gamerScore > 21 ? "Dealer" : dealerScore > gamerScore ? "Dealer" : "You";
        JOptionPane.showMessageDialog(frame, "Game Over! Winner: " + winner);

        // 게임 종료 시 사운드 전환
        soundManager.stopSound(Sound.BGM_GAMBLING);
        soundManager.loopSound(Sound.BGM_MAIN);

        frame.dispose(); // 창 닫기
    }

    public static void main(String[] args) {
        new BlackJackScreen();
    }
}