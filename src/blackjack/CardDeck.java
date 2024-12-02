package blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardDeck {
    private final List<Card> cards; // 카드 덱 리스트

    public CardDeck() {
        this.cards = new ArrayList<>();
        reset(); // 카드 덱 초기화 및 섞기
    }

    // 카드 덱 초기화 및 섞기
    public void reset() {
        cards.clear();
        String[] patterns = {"스페이드", "하트", "다이아몬드", "클럽"};
        String[] denominations = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        for (String pattern : patterns) {
            for (String denomination : denominations) {
                cards.add(new Card(pattern, denomination));
            }
        }

        shuffle(); // 카드 섞기
    }

    // 카드 덱 섞기
    public void shuffle() {
        Collections.shuffle(cards);
    }

    // 카드 한 장 뽑기
    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("카드 덱에 남은 카드가 없습니다.");
        }
        return cards.remove(0); // 맨 앞의 카드 반환 후 제거
    }

    // 남은 카드 수 반환
    public int getRemainingCards() {
        return cards.size();
    }

    // 카드 덱 확인 (디버깅용)
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("카드 덱: ");
        for (Card card : cards) {
            sb.append(card.toString()).append(", ");
        }
        return sb.toString();
    }
}
