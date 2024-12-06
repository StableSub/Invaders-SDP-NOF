package blackjack;

public class Dealer extends Player {
    public Dealer() {
        super();
    }

    @Override
    public String toString() {
        return "딜러의 카드: " + openCards();
    }
}
