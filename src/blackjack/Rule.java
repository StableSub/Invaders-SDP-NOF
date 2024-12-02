package blackjack;

public class Rule {
    public String determineWinner(Dealer dealer, Gamer gamer) {
        int dealerScore = dealer.getScore();
        int gamerScore = gamer.getScore();

        if (gamerScore > 21) return "Dealer wins! (Player busted)";
        if (dealerScore > 21) return "Player wins! (Dealer busted)";
        if (gamerScore > dealerScore) return "Player wins!";
        if (gamerScore < dealerScore) return "Dealer wins!";
        return "It's a draw!";
    }
}