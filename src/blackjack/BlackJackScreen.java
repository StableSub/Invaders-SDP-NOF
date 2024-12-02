package blackjack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BlackJackScreen {
    private final JFrame frame;
    private final Gamer gamer;
    private final Dealer dealer;
    private final CardDeck cardDeck;
    private final Rule rule;

    public BlackJackScreen() {
        this.gamer = new Gamer("Player 1");
        this.dealer = new Dealer();
        this.cardDeck = new CardDeck();
        this.rule = new Rule();

        // 새로운 블랙잭 게임 창 생성
        frame = new JFrame("BlackJack Game");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 초기 인터페이스 설정
        setupUI();

        frame.setVisible(true);
    }

    private void setupUI() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));

        // Hit 버튼
        JButton hitButton = new JButton("Hit");
        hitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamer.receiveCard(cardDeck.drawCard());
                System.out.println("카드를 한 장 더 받았습니다!");
                updateGameStatus();
            }
        });

        // Stand 버튼
        JButton standButton = new JButton("Stand");
        standButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dealerTurn();
                endGame();
            }
        });

        // Exit 버튼
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // 창 닫기
            }
        });

        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        buttonPanel.add(exitButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // 카드 상태 출력 패널 추가
        updateGameStatus();
    }

    private void updateGameStatus() {
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new GridLayout(2, 1));
        statusPanel.removeAll(); // 기존 상태 제거 후 다시 추가

        JLabel gamerStatus = new JLabel("Your Cards: " + gamer.openCards() + " | Your Score: " + gamer.getScore());
        JLabel dealerStatus = new JLabel("Dealer's Cards: " + dealer.openCards() + " | Dealer's Score: " + dealer.getScore());

        statusPanel.add(gamerStatus);
        statusPanel.add(dealerStatus);

        frame.add(statusPanel, BorderLayout.CENTER);
        frame.revalidate(); // 프레임 업데이트
        frame.repaint();    // 프레임 다시 그리기
    }

    private void dealerTurn() {
        System.out.println("\n딜러의 턴...");
        while (dealer.shouldHit()) {
            dealer.receiveCard(cardDeck.drawCard());
            System.out.println("딜러가 카드를 한 장 받았습니다.");
        }
    }

    private void endGame() {
        String winner = rule.determineWinner(dealer, gamer);
        System.out.println("게임 종료!");
        System.out.println("승자: " + winner);

        JOptionPane.showMessageDialog(frame, "게임 종료! 승자: " + winner);
    }
}
