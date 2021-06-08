package server_spring;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {

    private static final int NO_OF_CARDS = 52;
    private Card[] cards;
    private int nextCardIndex = 0;
    private Random random = new SecureRandom();

    public Deck() {
        cards = new Card[NO_OF_CARDS];
        int index = 0;
        for (int suit = Card.NO_OF_SUITS - 1; suit >= 0; suit--) {
            for (int rank = Card.NO_OF_RANKS - 1; rank >= 0 ; rank--) {
                cards[index++] = new Card(rank, suit);
            }
        }
    }

    public void shuffle() {
        for (int oldIndex = 0; oldIndex < NO_OF_CARDS; oldIndex++) {
            int newIndex = random.nextInt(NO_OF_CARDS);
            Card tempCard = cards[oldIndex];
            cards[oldIndex] = cards[newIndex];
            cards[newIndex] = tempCard;
        }
        nextCardIndex = 0;
    }

    public void reset() {
        nextCardIndex = 0;
    }

    /**
     * Deals a single card.
     *
     * @return  the card dealt
     */
    public Card deal() {
        if (nextCardIndex + 1 >= NO_OF_CARDS) {
            throw new IllegalStateException("No cards left in deck");
        }
        return cards[nextCardIndex++];
    }

    /**
     * Deals multiple cards at once.
     *
     * @throws IllegalArgumentException
     *             If the number of cards is invalid.
     * @throws IllegalStateException
     *             If there are no cards left in the deck.
     */
    public List<Card> deal(int noOfCards) {
        if (noOfCards < 1) {
            throw new IllegalArgumentException("noOfCards < 1");
        }
        if (nextCardIndex + noOfCards >= NO_OF_CARDS) {
            throw new IllegalStateException("No cards left in deck");
        }
        List<Card> dealtCards = new ArrayList<Card>();
        for (int i = 0; i < noOfCards; i++) {
            dealtCards.add(cards[nextCardIndex++]);
        }
        return dealtCards;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            sb.append(card);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

}
