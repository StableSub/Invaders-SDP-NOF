package blackjack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import entity.Wallet;
import engine.manager.*;
import engine.utility.Sound;


public class BlackJackScreen {
    private final JFrame frame;
    private final JPanel statusPanel;
    private  JLabel actionMessageLabel;
    private  JLabel gamerStatusLabel;
    private  JLabel dealerStatusLabel;
    private final CardDeck cardDeck = new CardDeck(); // 카드 덱 추가
    private final Wallet wallet;
    private final int bettingAmount;

    // 카드와 점수를 관리하기 위한 변수
    private String gamerCards = "";
    private int gamerScore = 0;
    private String dealerCards = "";
    private int dealerScore = 0;
    private boolean isRunning = true; // 게임 실행 상태 플래그

    // InputManager 및 SoundManager 인스턴스
    private final InputManager inputManager = InputManager.getInstance();
    private final SoundManager soundManager = SoundManager.getInstance();

    public BlackJackScreen(Wallet wallet,int bettingAmount) {
        frame = new JFrame("BlackJack Game");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        this.wallet = wallet;
        this.bettingAmount = bettingAmount;

        setupTopPanel();
        statusPanel = setupStatusPanel();
        setupButtonPanel();
        setupWindowListeners();

        frame.setVisible(true);

        // 초기 상태 업데이트
        startGame();
    }

    // Top Panel 설정
    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(Color.DARK_GRAY);

        JLabel titleLabel = new JLabel("BlackJack Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel);

        frame.add(topPanel, BorderLayout.NORTH);
    }

    // Status Panel 설정
    private JPanel setupStatusPanel() {
        JPanel tempStatusPanel = new JPanel(new GridLayout(3, 1));
        tempStatusPanel.setBackground(Color.LIGHT_GRAY);

        gamerStatusLabel = new JLabel("Your Cards: [] | Your Score: 0", SwingConstants.CENTER);
        gamerStatusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        tempStatusPanel.add(gamerStatusLabel);

        dealerStatusLabel = new JLabel("Dealer's Cards: [] | Dealer's Score: 0", SwingConstants.CENTER);
        dealerStatusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        tempStatusPanel.add(dealerStatusLabel);

        actionMessageLabel = new JLabel("", SwingConstants.CENTER);
        actionMessageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        actionMessageLabel.setForeground(Color.RED);
        tempStatusPanel.add(actionMessageLabel);

        frame.add(tempStatusPanel, BorderLayout.CENTER);
        return tempStatusPanel;
    }

    // Button Panel 설정
    private void setupButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.setBackground(Color.DARK_GRAY);

        JButton hitButton = createStyledButton("Hit", e -> hitAction());
        JButton standButton = createStyledButton("Stand", e -> standAction());
        JButton exitButton = createStyledButton("Exit", e -> exitAction());

        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        buttonPanel.add(exitButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);
    }

    // 창 리스너 설정
    private void setupWindowListeners() {
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

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                soundManager.stopSound(Sound.BGM_GAMBLING);
                soundManager.loopSound(Sound.BGM_MAIN);
                inputManager.resetKeys();
            }
        });
    }

    // 스타일 적용 버튼 생성
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

    // 게임 시작 메서드
    private void startGame() {
        cardDeck.reset(); // 카드 덱 초기화

        // 게이머 초기 카드 2장 배분
        Card gamerCard1 = cardDeck.drawCard();
        Card gamerCard2 = cardDeck.drawCard();
        gamerCards = "[" + gamerCard1 + ", " + gamerCard2 + "]";
        gamerScore = gamerCard1.getValue() + gamerCard2.getValue();

        // 딜러 초기 카드 1장 배분
        Card dealerCard1 = cardDeck.drawCard();
        dealerCards = "[" + dealerCard1 + "]";
        dealerScore = dealerCard1.getValue();

        updateGameStatus("Game Started! Good luck!");
        gamerStatusLabel.setText("Your Cards: " + gamerCards + " | Your Score: " + gamerScore);
        dealerStatusLabel.setText("Dealer's Cards: " + dealerCards + " | Dealer's Score: " + dealerScore);
    }

    // 히트 액션 메서드
    private void hitAction() {
        if (!isRunning) return;
        isRunning = false;

        Card newCard = cardDeck.drawCard();
        gamerCards += ", " + newCard;
        gamerScore += newCard.getValue();

        if (gamerScore > 21) {
            updateGameStatus("Bust! Your score exceeded 21.");
            gamerStatusLabel.setText("Your Cards: " + gamerCards + " | Your Score: " + gamerScore);
            endGame();
        } else {
            updateGameStatus("You chose to Hit! Drawing a card...");
        }

        gamerStatusLabel.setText("Your Cards: " + gamerCards + " | Your Score: " + gamerScore);

        Timer timer = new Timer(1000, e -> isRunning = true);
        timer.setRepeats(false);
        timer.start();
    }

    // 스탠드 액션 메서드
    private void standAction() {
        if (!isRunning) return;
        isRunning = false;

        while (dealerScore < 17) {
            Card newCard = cardDeck.drawCard();
            dealerCards += ", " + newCard;
            dealerScore += newCard.getValue();
        }

        updateGameStatus("You chose to Stand. Dealer's turn...");
        dealerStatusLabel.setText("Dealer's Cards: " + dealerCards + " | Dealer's Score: " + dealerScore);

        endGame();

        Timer timer = new Timer(1000, e -> isRunning = true);
        timer.setRepeats(false);
        timer.start();
    }

    // 종료 액션 메서드
    private void exitAction() {
        soundManager.stopSound(Sound.BGM_GAMBLING);
        soundManager.loopSound(Sound.BGM_MAIN);

        updateGameStatus("Exiting the game...");
        frame.dispose();
    }

    // 게임 상태 업데이트 메서드
    private void updateGameStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            actionMessageLabel.setText(message);

            Timer timer = new Timer(2000, e -> actionMessageLabel.setText(""));
            timer.setRepeats(false);
            timer.start();
        });
    }

    // 게임 종료 메서드
    private void endGame() {
        if(gamerScore >21 & dealerScore >21){
            JOptionPane.showMessageDialog(frame, "DRAW");
        }
        else if (gamerScore > 21){
            wallet.withdraw(bettingAmount);
            JOptionPane.showMessageDialog(frame, "Game Over! Your bust Winner:Dealer" );
        }
        else if (dealerScore > 21){
            wallet.deposit(bettingAmount);
            JOptionPane.showMessageDialog(frame, "Game Over! Dealer bust Winner:Player" );
        }
        else if (gamerScore > dealerScore){
            wallet.deposit(bettingAmount);
            JOptionPane.showMessageDialog(frame, "Game Over! Winner: Player");
        }
        else{
            wallet.withdraw(bettingAmount);
            JOptionPane.showMessageDialog(frame, "Game Over! Winner:Dealer" );
        }

        soundManager.stopSound(Sound.BGM_GAMBLING);
        soundManager.loopSound(Sound.BGM_MAIN);

        frame.dispose();
    }


}
