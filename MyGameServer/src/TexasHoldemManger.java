//package Poker;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class TexasHoldemManger implements IGamesManager {
    public TexasHoldemManger(ClientData opener) {
        pots = new ArrayList<>();
        m_active_players = new HashMap<>();
        next_turn = new Vector<>();
        board = new ArrayList<>();
        doActionFactory = new DoAction();
        game_deck = new Deck();
        players_hand = new HashMap<>();

        joined_num = 1;
        player_turn_index = 0;
        game_step = -1;
        in_hand_counter = 0;
        curr_in_hand = 1;
        massage_or_action = 0;
        SmallBlindIndex = 0;
        all_in_counter = 0;

        opener.curr_game_score = 2500; // default buy in
        opener.curr_status = 1; // wait for next hand
        m_active_players.put(opener.id, opener);
        next_turn.add(opener);

        game_deck.shuffle();
    }

    @Override
    public boolean JoinGame(ClientData joiner) {
        joiner.curr_game_score = 2500;
        joiner.curr_status = 1;
        m_active_players.put(joiner.id, joiner);
//        next_turn.set(joined_num++, joiner);
        next_turn.add(joiner);
        joined_num++;
        return true;
    }

    @Override
    public void RestartGame() {
        StartNewRound();
    }

    @Override
    public void LeaveGame(ClientData leaver) {
        //cash out...
        if (game_step != 0) {
            MsgHeader ret = new MsgHeader();
            ret.usr_Id = leaver.id;
            ret.req_type = ReqType.LeaveGame;
            Next(ret);
        }
        --joined_num;
        m_active_players.remove(leaver.id);
    }

    @Override
    public MsgHeader Next(MsgHeader last_move) {
        MsgHeader ret = new MsgHeader();
        ret.req_type = ReqType.PlayNext;
        ret.usr_Id = next_turn.get(player_turn_index).id;

        if(true)
        {
            if (game_step < 1) // dealing cards
            {
                if (game_step == -1) {
                    StartNewRound ();
                    ret.usr_Id = next_turn.get(player_turn_index).id;
                    System.out.println("start new round");
                }
                List<Card> next = DealNextHand();
                System.out.println("deal hand");

                ret.buffer = next;
                ret.game_status = 0;
                ++in_hand_counter;
                player_turn_index = (player_turn_index + 1) % next_turn.size();
                Hand newHand = new Hand(next);
                System.out.println("crating hand");
                players_hand.put(ret.usr_Id, newHand);
                System.out.println("updating hand");
            }
            if (game_step == 1) // placing first bet
            {
                if (massage_or_action == 0) {
                    System.out.println("blinds step");
                    ret.game_status = 100;
                    //small blind
                    if (next_turn.get(player_turn_index).id == next_turn.get(SmallBlindIndex).id) {
                        System.out.println("Small");
                        ret.quantity_param = SmallBlind;
                        Action small = doActionFactory.GetAction(Action.Type.SmallBlinds);
                        small.Do(m_active_players.get(next_turn.get(player_turn_index).id), SmallBlind);
                    } else {
                        //big blind
                        System.out.println("Big");
                        ret.quantity_param = SmallBlind * 2;
                        Action big = doActionFactory.GetAction(Action.Type.BigBlinds);
                        big.Do(m_active_players.get(next_turn.get(player_turn_index).id), SmallBlind * 2);

                        massage_or_action = 1;
                    }
                    player_turn_index = (player_turn_index + 1) % next_turn.size();
                } else {
                    ret = BettingRound(last_move);
                }

            } else if (game_step == 2) // flop
            {
                // creating string table;
                System.out.println("Dealing flop");
                game_deck.deal(); // burned card
                board.add(game_deck.deal());
                board.add(game_deck.deal());
                board.add(game_deck.deal());
                ret.game_manger_msg = "Flop: " + board.stream().map(Objects::toString).collect(Collectors.joining(", "));
                ret.game_status = 200;
                game_step++;
            } else if (game_step == 3 || game_step == 5 || game_step == 7 || game_step == 8) // bet round after cards open
            {
                System.out.println("game step: " + game_step);
                if ((curr_in_hand - all_in_counter) > 1 && game_step != 8) {
                    System.out.println("starting betting round after card open, " + curr_in_hand + " in hand " + all_in_counter + " AllIn");
                    pots.forEach(Pot::StartNewBetRound);
                    ret = BettingRound(last_move);
                } else if (game_step == 8) {
                    System.out.println("calculate results");
                    ret = RoundResult();

                } else {
                    System.out.println("open cards!");
                    ++game_step;
                }
            }
            if (game_step == 4 || game_step == 6) // deal turn or river
            {
                ret = DealOneToBoard();
            }
            if ((in_hand_counter == curr_in_hand) && massage_or_action != 2) {
                game_step = (game_step + 1) % NumOfGameSteps;
                System.out.println("to next step: " + game_step);
                in_hand_counter = 0;
                player_turn_index = SmallBlindIndex;
                for (Pot pot : pots) {
                    pot.players_curr_pot_invest.forEach((k, v) ->
                            pot.players_curr_pot_invest.put(k, pot.players_curr_pot_invest.get(k) - v));
                }

            }
        }
        System.out.println ("sending ret to lobby");
        return ret;
    }

    private void StartNewRound() {
        player_turn_index = SmallBlindIndex;
        pots.clear();
        board.clear ();
        massage_or_action = 0;
        Pot pot = new Pot(SmallBlind * 2);
        int counter = next_turn.size();
        curr_in_hand = counter;
        int i;
        for (i = SmallBlindIndex; i < next_turn.size(); ++i) {
            --counter;
            pot.contributors.add(next_turn.get(i));
            System.out.println("new round, adding: " + next_turn.get(i).id);
            pot.players_curr_pot_invest.put(next_turn.get(i).id,0);
            System.out.println("added");
        }
        i = 0;
        while (counter != 0) {
            pot.contributors.add(next_turn.get(i));
            pot.players_curr_pot_invest.put(next_turn.get(i).id,0);
            ++i;
            --counter;
        }

        pots.add(pot);
        game_deck.reset();
        game_deck.shuffle();
        game_step = 0;
    }

    private List<Card> DealNextHand() {
        List<Card> new_hand = game_deck.deal(2);
        return new_hand;
    }

    private MsgHeader BettingRound(MsgHeader last_move) {
        MsgHeader ret = new MsgHeader();
        if (massage_or_action == 1) {
            System.out.println("asking next turn to take an action");//asking next turn to take an action
            ret.game_status = 300; // 300 - take an action, client read instruction
            int curr_bet = 0;
            for (Pot pot : pots) {
                int counter = 1;
                System.out.println("in pot num : " + counter);
                curr_bet += pot.GetCurrGap(next_turn.get(player_turn_index).id);
                System.out.println("curr bet gap is: " + curr_bet);
                ++counter;
            }
            ret.game_manger_msg = "to call:  " + curr_bet;
            ret.quantity_param = curr_bet;
            ret.usr_Id = next_turn.get(player_turn_index).id;
            massage_or_action = 2;
            ++in_hand_counter;
            player_turn_index = (player_turn_index + 1) % next_turn.size();
        } else {
            //check last move action + update pot data
            System.out.println("preparing for broadcast update");
            String type = (String) last_move.buffer;
            Action.Type last_action = Action.Type.valueOf(type);
            Action action = doActionFactory.GetAction(last_action);
            action.Do(m_active_players.get(last_move.usr_Id), last_move.quantity_param);
            //update all players
            ret.game_status = 200; // client read massage
            ret.game_manger_msg = pots.stream().map(Object::toString).collect(Collectors.joining(", "));
            massage_or_action = 1;

        }
        curr_in_hand = pots.get(0).contributors.size();
        if (curr_in_hand == 1) {
            ret = RoundResult();
        }
        return ret;
    }

    private MsgHeader DealOneToBoard() {
        MsgHeader ret = new MsgHeader();
        game_deck.deal(); // burned card
        board.add(game_deck.deal());
        ret.game_manger_msg = board.stream().map(Objects::toString).collect(Collectors.joining(", "));
        ret.game_status = 200;
        game_step++;
        return ret;
    }

    private MsgHeader RoundResult() //refer to round results
    {
        MsgHeader ret = new MsgHeader();
        game_step = -1;
        SmallBlindIndex = (SmallBlindIndex + 1) % next_turn.size ();
        in_hand_counter = 0;
        ret.game_status = 20;
        int total_pot = 0;
        for (Pot pot : pots) {
            total_pot += pot.GetPotVal();
        }
        System.out.println ("checking total pot: " + total_pot);
        if (curr_in_hand > 1) {
            pots.get(0).contributors.forEach((k) -> players_hand.get(k.id).addCards(board));
            System.out.println ("adjusting player hands");
            ret.usr_Id = 0;
            HashMap<Integer,Integer> winners = new HashMap<>();
            Vector<Integer> tmp_winners = new Vector<>();
            int max_val = 0;
            for (Pot pot : pots) {
                System.out.println ("looking for the winner in each pot");
                for(ClientData clientData : pot.contributors) {
                    HandEvaluator handEvaluator = new HandEvaluator(players_hand.get(clientData.id));
                    System.out.println ("evaluate: " + clientData.id);
                    if(max_val < handEvaluator.value)
                    {
                        System.out.println ("update the new winner");
                        tmp_winners.clear();
                        tmp_winners.add(clientData.id);
                        max_val = handEvaluator.value;
                    }
                    else if(max_val == handEvaluator.value)
                    {
                        System.out.println ("pot tie");
                        tmp_winners.add(clientData.id);
                    }
                }
                int pot_share = pot.pot_val / tmp_winners.size();
                for (int i : tmp_winners)
                {
                    System.out.println ("update final winners");
                    if(winners.containsKey(i))
                    {
                        winners.put(i,winners.get(i) + pot_share);
                    }
                    else
                    {
                        winners.put(i,pot_share);
                    }
                }
            }
            ret.buffer = new HashMap<>();
            ret.buffer = winners;
            System.out.println ("the winner is: " + winners.toString ());
            //send display of the winnings hand
        } else {
            Optional<ClientData> winner = pots.get(0).contributors.stream().findAny();

            ret.game_manger_msg = "pot winner is: " + winner.get().id + " pot value is: " + total_pot;
            ret.usr_Id = winner.get().id;
            ret.quantity_param = total_pot;
        }

        System.out.println ("finish calculating");
        return ret;
    }

    @Override
    public int GetMaxPlayers() {
        return max_players;
    }

    @Override
    public int GetCurrActiveNumOfPlayers() {
        return m_active_players.size();
    }

    @Override
    public HashMap<Integer, ClientData> GetForBroadcast() {
        return m_active_players;
    }

    @Override
    public int GetGameStep() {
        return game_step;
    }

    public void AddBetToPot(int id, int bet, int pot_index)
    {
        System.out.println("in add before adding");
        pots.get(pot_index).AddBet(id,bet);
        System.out.println("in add after adding");
    }

    private final int NumOfGameSteps = 9;
    private final int SmallBlind = 5;
    private int SmallBlindIndex;
    private int game_step;
    private Vector<ClientData> next_turn;
    private int player_turn_index;
    private int joined_num;
    private Deck game_deck;
    private final int max_players = 8;
    private HashMap<Integer, ClientData> m_active_players;
    private int in_hand_counter;
    private int curr_in_hand;
    private final List<Pot> pots;
    private int massage_or_action;
    private final List<Card> board;
    private DoAction doActionFactory;
    private int all_in_counter;
    private HashMap<Integer,Hand> players_hand;

    public static class Pot {
        public Pot(int initial_val) {
            this.curr_bet = initial_val;
            contributors = new HashSet<>();
            players_curr_pot_invest = new HashMap<>();
            pot_val = 0;
        }

        public void Clear() {
            pot_val = 0;
            contributors.clear();
            players_curr_pot_invest.clear();
        }

        public void StartNewBetRound()
        {
            this.curr_bet = 0;
        }

        public void addContributor(ClientData player) {
            contributors.add(player);
        }

        public void SetUpCurrBet(int new_bet) {
             this.curr_bet += new_bet;
        }

        public int GetCurrGap(int id) {

            System.out.println ("curr bet is: " + this.curr_bet + " player: " + id + " invest: " + this.players_curr_pot_invest.get(id));
            return this.curr_bet - this.players_curr_pot_invest.get(id);
        }

        public int GetPotVal() {
            return pot_val;
        }

        public void AddBet(int id,int bet)
        {
            players_curr_pot_invest.computeIfPresent(id, (k, v) -> v + bet);
            System.out.println("in  pot before bet: " + this.pot_val);
            this.pot_val += bet;
            System.out.println("in  pot after bet: " + this.pot_val);
        }


        public Pot SplitVal(ClientData player, int partialBet) {
            int gap = curr_bet - partialBet;
            Pot pot = new Pot(gap);
            for (ClientData contributor : contributors) {
                pot.addContributor(contributor);
                pot.players_curr_pot_invest.put(contributor.id,gap);
                players_curr_pot_invest.put(contributor.id,players_curr_pot_invest.get(contributor.id) - gap);
            }
            curr_bet = partialBet;
            //pot_val = curr_bet * contributors.size();
            pot.contributors.remove(player);
            pot.players_curr_pot_invest.remove(player.id);
            pot.pot_val = pot.curr_bet * pot.contributors.size();

            return pot;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(curr_bet));
            sb.append(": {");
            boolean isFirst = true;
            for (ClientData contributor : contributors) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(", ");
                }
                sb.append(contributor.id);
            }
            sb.append('}');
            sb.append(" (Total: ");
            sb.append(String.valueOf(pot_val));
            sb.append(')');
            return sb.toString();
        }

        public final Set<ClientData> contributors;
        private int pot_val;
        private int curr_bet;
        private HashMap<Integer,Integer> players_curr_pot_invest;

    }

    public interface Action{
        enum Type {
            SmallBlinds,
            BigBlinds,
            Call,
            Bet,
            AllIn,
            Check,
            Fold, //update contributors
            Continue //update contributors next_turn in case of leaving
//            Wait
        }

        void Do(ClientData player, int bet);

    }

    public class DoAction {
        public Action GetAction(Action.Type act) {
            return switch (act) {
                case SmallBlinds -> new SmallA();
                case BigBlinds -> new BigA();
                case Call -> new CallA();
                case Bet -> new BetA();
                case AllIn -> new AllInA();
                case Check -> new CheckA();
                case Fold -> new FoldA();
                case Continue -> new ContinueA();
//                case Wait -> new Wait();
            };
        }
    }

    public class SmallA implements Action {
        public void Do(ClientData player, int bet) {
            AddBetToPot(player.id,bet,0);
            player.curr_game_score -= bet;
        }
    }

    public class BigA implements Action {
        public void Do(ClientData player, int bet) {
            AddBetToPot(player.id,bet,0);
            player.curr_game_score -= bet;
        }
    }

    public class CallA implements Action {
        public void Do(ClientData player, int bet) {
            CalculateCallGap(player,bet);
            }
        }


    public class BetA implements Action {
        public void Do(ClientData player, int bet) {
            int gap = CalculateCallGap(player,bet);
            AddBetToPot(player.id,bet - gap,pots.size() -1);
            pots.get(pots.size() -1).SetUpCurrBet(bet - gap);
            in_hand_counter = 1;
        }

    }

    public int CalculateCallGap(ClientData player, int bet) {
        int total_gap = 0;
        for (Pot pot : pots) {
            //place partial bet in each pot
            int curr_investment = pot.players_curr_pot_invest.get(player.id);
            System.out.println("curr invest for: " + player.id + " is: " + curr_investment );
            if (curr_investment < pot.curr_bet) {
                int gap = pot.curr_bet - curr_investment;
                pot.AddBet(player.id,gap);
                m_active_players.get(player.id).curr_game_score -= gap;
                total_gap += gap;
            }
        }
        return total_gap;
    }

    public class CheckA implements Action {
        public void Do(ClientData player, int bet) {
            // do nothing pass turn
        }

    }
    public class AllInA implements Action {
        public void Do(ClientData player, int bet) {
            int chips = m_active_players.get(player.id).curr_game_score;
            for(Pot pot : pots) {
                if(chips >= pot.curr_bet)
                {
                    int gap = chips - pot.curr_bet;
                    pot.AddBet(player.id,gap);
                    chips -= gap;
                }
                else
                {
                    pots.add(pot.SplitVal(player,chips));
                }

            }
            if(chips > 0)
            {
                pots.get(pots.size() -1).curr_bet += chips;
                pots.get(pots.size() -1).pot_val += chips;
            }
            m_active_players.get(player.id).curr_game_score = 0;
            all_in_counter++;
        }

    }

    public class FoldA implements Action {
        public void Do(ClientData player, int bet) {
            for(Pot pot : pots) {
                pot.contributors.remove(player);
                pot.players_curr_pot_invest.remove(player.id);
            }
        }

    }

    public class ContinueA implements Action {
        public void Do(ClientData player, int bet) {
            next_turn.remove(player);
            for (Pot pot : pots) {
                pot.contributors.remove(player);
                pot.players_curr_pot_invest.remove(player.id);
            }
        }
    }

    public class Hand {

        private static final int MAX_NO_OF_CARDS = 7;
        private Card[] cards = new Card[MAX_NO_OF_CARDS];
        private int noOfCards = 0;

        public Hand() {
            // Empty implementation.
        }

        public Hand(List<Card> cards) {
            addCards(cards);
        }

        public int size() {
            return noOfCards;
        }

        public void addCard(Card card) {
            if (card == null) {
                throw new IllegalArgumentException("Null card");
            }

            int insertIndex = -1;
            for (int i = 0; i < noOfCards; i++) {
                if (card.compareTo(cards[i]) > 0) {
                    insertIndex = i;
                    break;
                }
            }
            if (insertIndex == -1) {
                // Could not insert anywhere, so append at the end.
                cards[noOfCards++] = card;
            } else {
                for (int i = noOfCards; i > insertIndex; i--) {
                    cards[i] = cards[i - 1];
                }
                cards[insertIndex] = card;
                noOfCards++;
            }
        }

        public void addCards(List<Card> cards) {
            if (cards == null) {
                throw new IllegalArgumentException("Null array");
            }
            if (cards.size() > MAX_NO_OF_CARDS) {
                throw new IllegalArgumentException("Too many cards");
            }
            for (Card card : cards) {
                addCard(card);
            }
        }

        public Card[] getCards() {
            Card[] dest = new Card[noOfCards];
            System.arraycopy(cards, 0, dest, 0, noOfCards);
            return dest;
        }


        public void removeAllCards() {
            noOfCards = 0;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < noOfCards; i++) {
                sb.append(cards[i]);
                if (i < (noOfCards - 1)) {
                    sb.append(' ');
                }
            }
            return sb.toString();
        }

    }

    public static class HandEvaluator {
        public enum HandValueType {
            ROYAL_FLUSH("a Royal Flush", 9),
            STRAIGHT_FLUSH("a Straight Flush", 8),
            FOUR_OF_A_KIND("Four of a Kind", 7),
            FULL_HOUSE("a Full House", 6),
            FLUSH("a Flush", 5),
            STRAIGHT("a Straight", 4),
            THREE_OF_A_KIND("Three of a Kind", 3),
            TWO_PAIRS("Two Pairs", 2),
            ONE_PAIR("One Pair", 1),
            HIGH_CARD("a High Card", 0),
            ;
            private String description;
            private int value;

            HandValueType(String description, int value) {
                this.description = description;
                this.value = value;
            }
            public String getDescription() {
                return description;
            }
            public int getValue() {
                return value;
            }

        }

        private static final int NO_OF_RANKINGS  = 6;
        private static final int MAX_NO_OF_PAIRS = 2;
        /** The ranking factors (powers of 13, the number of ranks). */
        private static final int[] RANKING_FACTORS = {371293, 28561, 2197, 169, 13, 1};
        private HandValueType type;
        private int value = 0;
        private final Card[] cards;
        private int[] rankDist = new int[Card.NO_OF_RANKS];
        private int[] suitDist = new int[Card.NO_OF_SUITS];
        private int noOfPairs = 0;
        private int[] pairs = new int[MAX_NO_OF_PAIRS];
        private int flushSuit = -1;
        private int flushRank = -1;
        private int straightRank = -1;
        private boolean wheelingAce = false;
        private int tripleRank = -1;
        private int quadRank = -1;
        private int[] rankings = new int[NO_OF_RANKINGS];

        public HandEvaluator(Hand hand) {
            cards = hand.getCards();

            // Find patterns.
            calculateDistributions();
            findStraight();
            findFlush();
            findDuplicates();

            // Find special values.
            boolean isSpecialValue =
                    (isStraightFlush() ||
                            isFourOfAKind()   ||
                            isFullHouse()     ||
                            isFlush()         ||
                            isStraight()      ||
                            isThreeOfAKind()  ||
                            isTwoPairs()      ||
                            isOnePair());
            if (!isSpecialValue) {
                calculateHighCard();
            }

            // Calculate value.
            for (int i = 0; i < NO_OF_RANKINGS; i++) {
                value += rankings[i] * RANKING_FACTORS[i];
            }
        }

        public HandValueType getType() {
            return type;
        }

        public int getValue() {
            return value;
        }

        private void calculateDistributions() {
            for (Card card : cards) {
                rankDist[card.getRank()]++;
                suitDist[card.getSuit()]++;
            }
        }

        private void findFlush() {
            for (int i = 0; i < Card.NO_OF_SUITS; i++) {
                if (suitDist[i] >= 5) {
                    flushSuit = i;
                    for (Card card : cards) {
                        if (card.getSuit() == flushSuit) {
                            if (!wheelingAce || card.getRank() != Card.ACE) {
                                flushRank = card.getRank();
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }

        private void findStraight() {
            boolean inStraight = false;
            int rank = -1;
            int count = 0;
            for (int i = Card.NO_OF_RANKS - 1; i >= 0 ; i--) {
                if (rankDist[i] == 0) {
                    inStraight = false;
                    count = 0;
                } else {
                    if (!inStraight) {
                        // First card of the potential Straight.
                        inStraight = true;
                        rank = i;
                    }
                    count++;
                    if (count >= 5) {
                        // Found a Straight!
                        straightRank = rank;
                        break;
                    }
                }
            }
            // Special case for the 'Steel Wheel' (Five-high Straight with a 'wheeling Ace') .
            if ((count == 4) && (rank == Card.FIVE) && (rankDist[Card.ACE] > 0)) {
                wheelingAce = true;
                straightRank = rank;
            }
        }

        private void findDuplicates() {
            // Find quads, triples and pairs.
            for (int i = Card.NO_OF_RANKS - 1; i >= 0 ; i--) {
                if (rankDist[i] == 4) {
                    quadRank = i;
                } else if (rankDist[i] == 3) {
                    tripleRank = i;
                } else if (rankDist[i] == 2) {
                    if (noOfPairs < MAX_NO_OF_PAIRS) {
                        pairs[noOfPairs++] = i;
                    }
                }
            }
        }

        private void calculateHighCard() {
            type = HandValueType.HIGH_CARD;
            rankings[0] = type.getValue();
            // Get the five highest ranks.
            int index = 1;
            for (Card card : cards) {
                rankings[index++] = card.getRank();
                if (index > 5) {
                    break;
                }
            }
        }

        private boolean isOnePair() {
            if (noOfPairs == 1) {
                type = HandValueType.ONE_PAIR;
                rankings[0] = type.getValue();
                // Get the rank of the pair.
                int pairRank = pairs[0];
                rankings[1] = pairRank;
                // Get the three kickers.
                int index = 2;
                for (Card card : cards) {
                    int rank = card.getRank();
                    if (rank != pairRank) {
                        rankings[index++] = rank;
                        if (index > 4) {
                            // We don't need any more kickers.
                            break;
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        private boolean isTwoPairs() {
            if (noOfPairs == 2) {
                type = HandValueType.TWO_PAIRS;
                rankings[0] = type.getValue();
                // Get the value of the high and low pairs.
                int highRank = pairs[0];
                int lowRank  = pairs[1];
                rankings[1] = highRank;
                rankings[2] = lowRank;
                // Get the kicker card.
                for (Card card : cards) {
                    int rank = card.getRank();
                    if ((rank != highRank) && (rank != lowRank)) {
                        rankings[3] = rank;
                        break;
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        private boolean isThreeOfAKind() {
            if (tripleRank != -1) {
                type = HandValueType.THREE_OF_A_KIND;
                rankings[0] = type.getValue();
                rankings[1] = tripleRank;
                // Get the remaining two cards as kickers.
                int index = 2;
                for (Card card : cards) {
                    int rank = card.getRank();
                    if (rank != tripleRank) {
                        rankings[index++] = rank;
                        if (index > 3) {
                            // We don't need any more kickers.
                            break;
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        private boolean isStraight() {
            if (straightRank != -1) {
                type = HandValueType.STRAIGHT;
                rankings[0] = type.getValue();
                rankings[1] = straightRank;
                return true;
            } else {
                return false;
            }
        }

        private boolean isFlush() {
            if (flushSuit != -1) {
                type = HandValueType.FLUSH;
                rankings[0] = type.getValue();
                int index = 1;
                for (Card card : cards) {
                    if (card.getSuit() == flushSuit) {
                        int rank = card.getRank();
                        if (index == 1) {
                            flushRank = rank;
                        }
                        rankings[index++] = rank;
                        if (index > 5) {
                            // We don't need more kickers.
                            break;
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        private boolean isFullHouse() {
            if ((tripleRank != -1) && (noOfPairs > 0)) {
                type = HandValueType.FULL_HOUSE;
                rankings[0] = type.getValue();
                rankings[1] = tripleRank;
                rankings[2] = pairs[0];
                return true;
            } else {
                return false;
            }
        }

        private boolean isFourOfAKind() {
            if (quadRank != -1) {
                type = HandValueType.FOUR_OF_A_KIND;
                rankings[0] = type.getValue();
                rankings[1] = quadRank;
                // Get the remaining card as kicker.
                int index = 2;
                for (Card card : cards) {
                    int rank = card.getRank();
                    if (rank != quadRank) {
                        rankings[index++] = rank;
                        break;
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        private boolean isStraightFlush() {
            if (straightRank != -1 && flushRank == straightRank) {
                // Flush and Straight (possibly separate); check for Straight Flush.
                int straightRank2 = -1;
                int lastSuit = -1;
                int lastRank = -1;
                int inStraight = 1;
                int inFlush = 1;
                for (Card card : cards) {
                    int rank = card.getRank();
                    int suit = card.getSuit();
                    if (lastRank != -1) {
                        int rankDiff = lastRank - rank;
                        if (rankDiff == 1) {
                            // Consecutive rank; possible straight!
                            inStraight++;
                            if (straightRank2 == -1) {
                                straightRank2 = lastRank;
                            }
                            if (suit == lastSuit) {
                                inFlush++;
                            } else {
                                inFlush = 1;
                            }
                            if (inStraight >= 5 && inFlush >= 5) {
                                // Straight!
                                break;
                            }
                        } else if (rankDiff == 0) {
                            // Duplicate rank; skip.
                        } else {
                            // Non-consecutive; reset.
                            straightRank2 = -1;
                            inStraight = 1;
                            inFlush = 1;
                        }
                    }
                    lastRank = rank;
                    lastSuit = suit;
                }

                if (inStraight >= 5 && inFlush >= 5) {
                    if (straightRank == Card.ACE) {
                        // Royal Flush.
                        type = HandValueType.ROYAL_FLUSH;
                        rankings[0] = type.getValue();
                        return true;
                    } else {
                        // Straight Flush.
                        type = HandValueType.STRAIGHT_FLUSH;
                        rankings[0] = type.getValue();
                        rankings[1] = straightRank2;
                        return true;
                    }
                } else if (wheelingAce && inStraight >= 4 && inFlush >= 4) {
                    // Steel Wheel (Straight Flush with wheeling Ace).
                    type = HandValueType.STRAIGHT_FLUSH;
                    rankings[0] = type.getValue();
                    rankings[1] = straightRank2;
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

    }
}


