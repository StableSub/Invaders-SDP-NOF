package blackjack;

public class Card {
    private final String pattern;       // 카드의 무늬 (spade, heart, diamond, club)
    private final String denomination; // 카드의 값 (A, 2, ..., K)

    public Card(String pattern, String denomination) {
        this.pattern = pattern;
        this.denomination = denomination;
    }

    public String getPattern() {
        return pattern;
    }

    public String getDenomination() {
        return denomination;
    }

    public int getValue() {
        if ("A".equals(denomination)) return 1; // 에이스는 항상 1
        if ("K".equals(denomination) || "Q".equals(denomination) || "J".equals(denomination)) return 10;
        return Integer.parseInt(denomination);
    }

    @Override
    public String toString() {
        return denomination + " of " + pattern;
    }
}