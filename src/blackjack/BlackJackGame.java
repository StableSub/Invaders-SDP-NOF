package blackjack;

import java.util.ArrayList;
import java.util.List;

public class BlackJackGame {
    private final Dealer dealer;
    private final Gamer gamer;
    private final CardDeck cardDeck;
    private boolean isRunning;

    public BlackJackGame() {
        this.dealer = new Dealer();
        this.gamer = new Gamer("Player 1");
        this.cardDeck = new CardDeck();
        this.isRunning = true;
    }

    public void startGame() {
        resetGame();

        // 초기 카드 배분
        dealer.receiveCard(cardDeck.drawCard());
        dealer.receiveCard(cardDeck.drawCard());
        gamer.receiveCard(cardDeck.drawCard());
        gamer.receiveCard(cardDeck.drawCard());
    }

    public void resetGame() {
        isRunning = true;
        cardDeck.reset();
        dealer.resetCards();
        gamer.resetCards();
    }

    public Card hit() {
        if (!isRunning) {
            throw new IllegalStateException("게임이 종료된 상태입니다.");
        }
        Card newCard = cardDeck.drawCard();
        gamer.receiveCard(newCard);
        if (gamer.getScore() > 21) {
            isRunning = false;
        }
        return newCard;
    }

    public List<Card> dealerTurn() {
        List<Card> dealerCardsDrawn = new ArrayList<>();
        while (dealer.shouldHit()) {
            Card newCard = cardDeck.drawCard();
            dealer.receiveCard(newCard);
            dealerCardsDrawn.add(newCard);
        }
        isRunning = false;
        return dealerCardsDrawn;
    }

    public boolean isGameRunning() {
        return isRunning;
    }

    public int getGamerScore() {
        return gamer.getScore();
    }

    public List<Card> getGamerCards() {
        return gamer.openCards();
    }

    public int getDealerScore() {
        return dealer.getScore();
    }

    public List<Card> getDealerCards() {
        return dealer.openCards();
    }
}
