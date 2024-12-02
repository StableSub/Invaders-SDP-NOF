package blackjack;

public class Dealer extends Player {
    public Dealer() {
        super();
    }

    // 딜러의 턴을 진행하는 메서드
    public void playTurn(CardDeck cardDeck) {
        System.out.println("\n딜러의 턴입니다.");

        while (shouldHit()) {
            receiveCard(cardDeck.drawCard());
            System.out.println("딜러가 카드를 한 장 받았습니다.");
        }

        System.out.println("딜러의 최종 카드: " + openCards());
        System.out.println("딜러의 최종 점수: " + getScore());
    }

    // 딜러는 점수가 17 미만이면 카드를 더 받습니다
    public boolean shouldHit() {
        return getScore() < 17;
    }

    @Override
    public String toString() {
        return "딜러의 카드: " + openCards();
    }
}
