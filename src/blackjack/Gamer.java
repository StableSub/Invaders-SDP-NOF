package blackjack;

import java.util.Scanner;

public class Gamer extends Player {
    private final String name;

    public Gamer(String name) {
        super();
        this.name = name;
    }
    @Override
    public String toString() {
        return name + "의 카드: " + openCards();
    }
}
