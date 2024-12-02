package blackjack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import engine.manager.*;
import engine.utility.*;

public class BlackJackScreen {
    private final JFrame frame;
    private final Dealer dealer;
    private final Gamer gamer;
    private final CardDeck cardDeck;
    private final Rule rule;
    private boolean isRunning; // 게임 실행 상태
    private final JPanel statusPanel; // 상태 패널을 인스턴스 변수로 변경
    private final JLabel actionMessageLabel; // 액션 메시지 라벨
    private final InputManager inputManager = InputManager.getInstance(); // InputManager 인스턴스 추가\
    private final SoundManager soundManager = SoundManager.getInstance();

    public BlackJackScreen() {
        this.dealer = new Dealer();
        this.gamer = new Gamer("Player 1");
        this.cardDeck = new CardDeck();
        this.rule = new Rule();
        this.isRunning = true; // 게임 시작 상태

        // 새로운 블랙잭 게임 창 생성
        frame = new JFrame("BlackJack Game");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 상태 패널 초기화
        statusPanel = new JPanel();
        statusPanel.setLayout(new GridLayout(3, 1));
        frame.add(statusPanel, BorderLayout.CENTER);

        // 액션 메시지 라벨 초기화
        actionMessageLabel = new JLabel("", SwingConstants.CENTER);
        actionMessageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusPanel.add(actionMessageLabel);

        // 초기 인터페이스 설정
        setupUI();

        // 게임 창이 포커스를 잃을 때 키 상태 초기화
        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                // 포커스를 얻었을 때 처리할 필요 없음
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                inputManager.resetKeys(); // 모든 키 상태 초기화
            }
        });

        frame.setVisible(true);

        // 게임 창이 닫힐 때 실행 상태를 false로 설정하고 키 상태 초기화
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                isRunning = false; // 게임 루프 종료
                inputManager.resetKeys(); // 모든 키 상태 초기화
            }
        });

        // 게임 시작
        startGame();
    }

    private void startGame() {
        resetGame();

        // 초기 카드 배분
        dealer.receiveCard(cardDeck.drawCard());
        dealer.receiveCard(cardDeck.drawCard());
        gamer.receiveCard(cardDeck.drawCard());
        gamer.receiveCard(cardDeck.drawCard());

        // 초기 상태 출력
        System.out.println("딜러의 첫 번째 카드: " + dealer.openCards().get(0)); // 딜러 첫 번째 카드만 공개
        System.out.println(gamer + "\n");

        // 초기 상태 패널 업데이트
        updateGameStatus();
    }

    private void resetGame() {
        // 게임 상태 초기화
        isRunning = true;

        // 카드 덱과 플레이어, 딜러의 카드 초기화
        cardDeck.reset();
        gamer.resetCards();
        dealer.resetCards();
    }

    private void setupUI() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));

        // Hit 버튼
        JButton hitButton = new JButton("Hit");
        hitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isRunning) {
                    gamer.receiveCard(cardDeck.drawCard());
                    showActionMessage("카드를 한 장 더 받았습니다!");
                    updateGameStatus();

                    if (gamer.getScore() > 21) {
                        System.out.println("버스트! 점수가 21을 초과했습니다.");
                        isRunning = false; // 게임 종료
                        endGame();
                    }
                }
            }
        });

        // Stand 버튼
        JButton standButton = new JButton("Stand");
        standButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isRunning) {
                    showActionMessage("턴을 종료하셨습니다.");
                    isRunning = false; // 턴 종료
                    dealerTurn();
                    endGame();
                }
            }
        });

        // Exit 버튼
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("게임 종료.");
                soundManager.stopSound(Sound.BGM_GAMBLING);
                soundManager.loopSound(Sound.BGM_MAIN);
                frame.dispose(); // 창 닫기
            }
        });

        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        buttonPanel.add(exitButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // 초기 상태 출력
        updateGameStatus();
    }

    private void updateGameStatus() {
        statusPanel.removeAll(); // 기존 상태 제거 후 다시 추가

        JLabel gamerStatus = new JLabel("Your Cards: " + gamer.openCards() + " | Your Score: " + gamer.getScore(), SwingConstants.CENTER);
        JLabel dealerStatus = new JLabel("Dealer's Cards: " + dealer.openCards() + " | Dealer's Score: " + dealer.getScore(), SwingConstants.CENTER);

        statusPanel.add(actionMessageLabel); // 액션 메시지 라벨 유지
        statusPanel.add(gamerStatus);
        statusPanel.add(dealerStatus);

        statusPanel.revalidate(); // 패널 업데이트
        statusPanel.repaint();    // 패널 다시 그리기
    }

    private void showActionMessage(String message) {
        actionMessageLabel.setText(message);
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionMessageLabel.setText(""); // 1초 후 메시지 지우기
            }
        });
        timer.setRepeats(false); // 타이머가 반복되지 않도록 설정
        timer.start();
    }

    private void dealerTurn() {
        System.out.println("\n딜러의 턴...");
        while (dealer.shouldHit() && isRunning) {
            dealer.receiveCard(cardDeck.drawCard());
            System.out.println("딜러가 카드를 한 장 받았습니다.");
        }

        System.out.println("딜러의 최종 카드: " + dealer.openCards());
        System.out.println("딜러의 최종 점수: " + dealer.getScore());
    }

    private void endGame() {
        if (!isRunning) return; // 게임 실행 중이 아니면 종료

        System.out.println("\n게임 종료!");
        System.out.println("당신의 최종 점수: " + gamer.getScore());
        System.out.println("딜러의 최종 점수: " + dealer.getScore());

        // 승자 결정
        String winner = rule.determineWinner(dealer, gamer);
        System.out.println("승자: " + winner);

        JOptionPane.showMessageDialog(frame, "게임 종료! 승자: " + winner);
    }
}
