package blackjack;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    protected List<Card> hand;

    public Player() {
        hand = new ArrayList<>();
    }

    // 현재 손에 있는 카드 반환
    public List<Card> openCards() {
        return hand;
    }

    // 점수 계산
    public int getScore() {
        int total = 0;
        int aceCount = 0;

        for (Card card : hand) {
            String denomination = card.getDenomination();
            switch (denomination) {
                case "A":
                    total += 11;
                    aceCount++;
                    break;
                case "K":
                case "Q":
                case "J":
                    total += 10;
                    break;
                default:
                    total += Integer.parseInt(denomination);
            }
        }

        // Ace 조정 (총 점수가 21을 초과하면 Ace를 1로 계산)
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        return total;
    }
}
