package blackjack;

import java.util.Scanner;

public class BlackJackGame {
    private final Dealer dealer;
    private final Gamer gamer;
    private final CardDeck cardDeck;
    private final Rule rule;
    private boolean isRunning; // 게임 실행 상태

    public BlackJackGame() {
        this.dealer = new Dealer();
        this.gamer = new Gamer("Player 1");
        this.cardDeck = new CardDeck();
        this.rule = new Rule();
        this.isRunning = true; // 게임 시작 상태
    }

    public void startGame() {
        // 초기 카드 배분
        dealer.receiveCard(cardDeck.drawCard());
        dealer.receiveCard(cardDeck.drawCard());
        gamer.receiveCard(cardDeck.drawCard());
        gamer.receiveCard(cardDeck.drawCard());

        // 초기 상태 출력
        System.out.println("Dealer's first card: " + dealer.openCards().get(0)); // 딜러 첫 번째 카드만 공개
        System.out.println(gamer + "\n");

        // 플레이어 턴
        playerTurn();

        // 플레이어가 Bust(21 초과)되었으면 종료
        if (gamer.getScore() > 21) {
            System.out.println("Bust! You exceeded 21. Dealer wins!");
            return;
        }

        // 딜러 턴
        dealerTurn();

        // 최종 결과 출력
        endGame();
    }

    private void playerTurn() {
        Scanner scanner = new Scanner(System.in);

        while (isRunning) {
            System.out.println("Your current cards: " + gamer.openCards());
            System.out.println("Your current score: " + gamer.getScore());
            System.out.println("Press Z to Hit, X to Stand");

            String input = scanner.nextLine().trim().toUpperCase();

            switch (input) {
                case "Z": // Hit
                    gamer.receiveCard(cardDeck.drawCard());
                    System.out.println("You drew a card!");
                    System.out.println(gamer);

                    if (gamer.getScore() > 21) {
                        System.out.println("Bust! You exceeded 21.");
                        isRunning = false; // 게임 종료
                    }
                    break;

                case "X": // Stand
                    System.out.println("You chose to stand.");
                    isRunning = false; // 턴 종료
                    break;

                default:
                    System.out.println("Invalid input. Please press Z to Hit or X to Stand.");
            }
        }
    }

    private void dealerTurn() {
        System.out.println("\nDealer's turn...");

        while (dealer.shouldHit()) {
            dealer.receiveCard(cardDeck.drawCard());
            System.out.println("Dealer drew a card.");
        }

        System.out.println("Dealer's final cards: " + dealer.openCards());
        System.out.println("Dealer's final score: " + dealer.getScore());
    }

    private void endGame() {
        System.out.println("\nGame Over!");
        System.out.println("Your final score: " + gamer.getScore());
        System.out.println("Dealer's final score: " + dealer.getScore());

        // 승자 결정
        String winner = rule.determineWinner(dealer, gamer);
        System.out.println("Winner: " + winner);
    }
}