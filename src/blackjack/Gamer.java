package blackjack;

import java.util.Scanner;

public class Gamer extends Player {
    private final String name;

    public Gamer(String name) {
        super();
        this.name = name;
    }

    // 플레이어의 턴을 진행하는 메서드
    public void playTurn(CardDeck cardDeck) {
        System.out.println("\n당신의 턴입니다.");
        boolean isRunning = true;
        Scanner scanner = new Scanner(System.in);

        while (isRunning) {
            System.out.println("현재 당신의 카드: " + openCards());
            System.out.println("현재 점수: " + getScore());
            System.out.println("카드를 더 받으려면 Z를, 턴을 종료하려면 X를 입력하세요.");

            String input = scanner.nextLine().trim().toUpperCase();

            switch (input) {
                case "Z": // Hit
                    receiveCard(cardDeck.drawCard());
                    System.out.println("카드를 한 장 더 받았습니다!");
                    System.out.println(this);

                    if (getScore() > 21) {
                        System.out.println("버스트! 점수가 21을 초과했습니다.");
                        isRunning = false; // 턴 종료
                    }
                    break;

                case "X": // Stand
                    System.out.println("턴을 종료하셨습니다.");
                    isRunning = false; // 턴 종료
                    break;

                default:
                    System.out.println("잘못된 입력입니다. Z를 입력하여 카드를 받거나 X를 입력하여 턴을 종료하세요.");
            }
        }
    }

    @Override
    public String toString() {
        return name + "의 카드: " + openCards();
    }
}
