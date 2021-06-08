package server_spring;
import java.io.Serializable;

public class Card implements Comparable<Card> , Serializable {

    public static final int NO_OF_RANKS = 13;
    public static final int NO_OF_SUITS = 4;

    // The ranks.
    public static final int ACE      = 12;
    public static final int KING     = 11;
    public static final int QUEEN    = 10;
    public static final int JACK     = 9;
    public static final int TEN      = 8;
    public static final int NINE     = 7;
    public static final int EIGHT    = 6;
    public static final int SEVEN    = 5;
    public static final int SIX      = 4;
    public static final int FIVE     = 3;
    public static final int FOUR     = 2;
    public static final int THREE    = 1;
    public static final int DEUCE    = 0;

    // The suits.
    public static final int SPADES   = 3;
    public static final int HEARTS   = 2;
    public static final int CLUBS    = 1;
    public static final int DIAMONDS = 0;

    public static final String[] RANK_SYMBOLS = {
            "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"
    };
    public static final char[] SUIT_SYMBOLS = { 'd', 'c', 'h', 's' };
    private final int rank;
    private final int suit;

    /**
     * Constructor based on rank and suit.
     * @throws IllegalArgumentException
     */
    public Card(int rank, int suit) {
        if (rank < 0 || rank > NO_OF_RANKS - 1) {
            throw new IllegalArgumentException("Invalid rank");
        }
        if (suit < 0 || suit > NO_OF_SUITS - 1) {
            throw new IllegalArgumentException("Invalid suit");
        }
        this.rank = rank;
        this.suit = suit;
    }

    public int getSuit() {
        return suit;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public int hashCode() {
        return (rank * NO_OF_SUITS + suit);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Card) {
            return ((Card) obj).hashCode() == hashCode();
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Card card) {
        int thisValue = hashCode();
        int otherValue = card.hashCode();
        if (thisValue < otherValue) {
            return -1;
        } else if (thisValue > otherValue) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return RANK_SYMBOLS[rank] + SUIT_SYMBOLS[suit];
    }

}
