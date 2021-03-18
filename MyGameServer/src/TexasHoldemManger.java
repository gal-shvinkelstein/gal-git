//package Poker;
import java.util.*;
import java.util.stream.Collectors;

public class TexasHoldemManger implements IGamesManager {
    public TexasHoldemManger(ClientData opener) {
        pots = new ArrayList<>();
        joined_num = 0;
        player_turn_index = 0;
        game_step = 0;
        in_hand_counter = 0;
        curr_in_hand = 1;
        massage_or_action = 0;
        SmallBlindIndex = 0;
        m_active_players = new HashMap<>();
        next_turn = new Vector<>();
        opener.curr_game_score = 2500; // default buy in
        opener.curr_status = 1; // wait for next hand
        m_active_players.put(opener.id, opener);
        board = new ArrayList<>();
        doActionFactory = new DoAction();

        next_turn.set(joined_num++, opener);


        game_deck = new Deck();
        game_deck.shuffle();


    }

    @Override
    public boolean JoinGame(ClientData joiner) {
        joiner.curr_game_score = 2500;
        joiner.curr_status = 1;
        m_active_players.put(joiner.id, joiner);
        next_turn.set(joined_num++, joiner);

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

        m_active_players.remove(leaver.id);
    }

    @Override
    public MsgHeader Next(MsgHeader last_move) {
        MsgHeader ret = new MsgHeader();
        ret.req_type = ReqType.PlayNext;
        ret.usr_Id = next_turn.get(player_turn_index).id;

        if (game_step == 0) // dealing cards
        {
            StartNewRound();
            ret.buffer = DealNextHand();
            ret.game_status = 0;
            ++in_hand_counter;
            player_turn_index = (player_turn_index + 1) % next_turn.size();
            //todo: saving each player hand data
        }
        if (game_step == 1) // placing first bet
        {
            if (massage_or_action == 0) {
                ret.game_status = 100;
                //small blind
                if (next_turn.get(player_turn_index).id == next_turn.get(SmallBlindIndex).id) {
                    ret.buffer = Action.Type.SmallBlinds;
                    ret.quantity_param = SmallBlind;
                    Action small = doActionFactory.GetAction(Action.Type.SmallBlinds);
                    small.Do(m_active_players.get(last_move.usr_Id), SmallBlind);
                } else {
                    //big blind
                    ret.buffer = Action.Type.BigBlinds;
                    ret.quantity_param = SmallBlind * 2;
                    Action big = doActionFactory.GetAction(Action.Type.BigBlinds);
                    big.Do(m_active_players.get(last_move.usr_Id), SmallBlind * 2);

                    massage_or_action = 2;
                }
                player_turn_index = (player_turn_index + 1) % next_turn.size();
            } else {
                ret = BettingRound(last_move);
            }

        } else if (game_step == 2) // flop
        {
            // creating string table;
            game_deck.deal(); // burned card
            board.add(game_deck.deal());
            board.add(game_deck.deal());
            board.add(game_deck.deal());
//            ret.game_manger_msg = board.get(0).toString() + " " + board.get(1).toString() + " "  + board.get(2).toString();
            ret.game_manger_msg = board.stream().map(Objects::toString).collect(Collectors.joining(", "));
            ret.game_status = 200;
            game_step++;
        } else if (game_step == 3 || game_step == 5 || game_step == 7) // bet round after cards open
        {
            ret = BettingRound(last_move);
        } else if (game_step == 4 || game_step == 6) // deal turn or river
        {
            ret = DealOneToBoard();
        }
        if (in_hand_counter == curr_in_hand) {
            game_step = (game_step + 1) % NumOfGameSteps;
            in_hand_counter = 0;
            player_turn_index = next_turn.indexOf(pots.get(0).contributors.stream().findAny());
            for (Pot pot : pots)
            {
                    pot.players_curr_pot_invest.forEach((k,v) ->
                            pot.players_curr_pot_invest.put(k,pot.players_curr_pot_invest.get(k) - v));
            }
            if (game_step == 0) {
                ret = RoundResult();
                ++SmallBlindIndex;
            }
        }

        // update according to contributors
        return ret;
    }

    private MsgHeader BettingRound(MsgHeader last_move) {
        MsgHeader ret = new MsgHeader();
        if (massage_or_action == 1) {
            //asking next turn to take an action
            ret.game_status = 100; // 100 - take an action, client read instruction
            int curr_bet = 0;
            for (Pot pot : pots) {
                curr_bet += pot.GetCurrBet();
            }
            ret.game_manger_msg = "Curr bet is:  " + curr_bet;
            massage_or_action = 2;
            ++in_hand_counter;
            player_turn_index = (player_turn_index + 1) % next_turn.size();
        } else {
            //check last move action + update pot data
            Action.Type type = (Action.Type) last_move.buffer;
            Action action = doActionFactory.GetAction(type);
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
        game_step = 0;
        ++SmallBlindIndex;
        in_hand_counter = 0;
        ret.game_status = 2;
        int total_pot = 0;
        for (Pot pot : pots) {
            total_pot += pot.GetPotVal();
        }

        if (curr_in_hand > 1) {
            //calculate results
        } else {
            Optional<ClientData> winner = pots.get(0).contributors.stream().findAny();

            ret.game_manger_msg = "pot winner is: " + winner.get().id + " pot value is: " + total_pot;
            ret.usr_Id = winner.get().id;
            ret.quantity_param = total_pot;
        }


        return ret;
    }

    private List<Card> DealNextHand() {
        List<Card> new_hand = game_deck.deal(2);
        return new_hand;
    }

    private void StartNewRound() {
        player_turn_index = SmallBlindIndex;
        pots.forEach((n) -> n.Clear());
        Pot pot = new Pot(SmallBlind * 2);
        int counter = next_turn.size();
        int i;
        for (i = SmallBlindIndex; i < next_turn.size(); ++i) {
            --counter;
            pot.contributors.add(next_turn.get(i));
            pot.players_curr_pot_invest.put(next_turn.get(i).id,0);
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

    private final int NumOfGameSteps = 7;
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

    public static class Pot {
        public Pot(int initial_val) {
            this.curr_bet = initial_val;
            contributors = new HashSet<>();
            players_curr_pot_invest = new HashMap<>();
        }

        public void Clear() {
            pot_val = 0;
            contributors.clear();
            players_curr_pot_invest.clear();
        }

        public void addContributor(ClientData player) {
            contributors.add(player);
        }

        public int GetCurrBet() {
            return curr_bet;
        }

        public int GetPotVal() {
            return pot_val;
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
            pot_val = curr_bet * contributors.size();
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

    public interface Action {
        enum Type {
            SmallBlinds,
            BigBlinds,
            Call,
            Bet,
            AllIn,
            Check,
            Fold, //update contributors
            Continue //update contributors next_turn in case of leaving
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
            };
        }
    }

    public class SmallA implements Action {
        public void Do(ClientData player, int bet) {
            pots.get(0).pot_val = bet;
            pots.get(0).players_curr_pot_invest.put(player.id,bet);
            player.curr_game_score -= bet;
        }
    }

    public class BigA implements Action {
        public void Do(ClientData player, int bet) {
            pots.get(0).pot_val += bet;
            pots.get(0).curr_bet = bet;
            pots.get(0).players_curr_pot_invest.put(player.id,bet);
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
            pots.get(pots.size()-1).players_curr_pot_invest.put(player.id,pots.get(pots.size()-1).players_curr_pot_invest.get(player.id) + gap);
            pots.get(pots.size()-1).curr_bet += gap;
            pots.get(pots.size()-1).pot_val += gap;
            in_hand_counter = 1;
        }

    }

    public int CalculateCallGap(ClientData player, int bet) {
        int total_gap = 0;
        for (Pot pot : pots) {
            //place partial bet in each pot
            int curr_investment = pot.players_curr_pot_invest.get(player.id);
            if (curr_investment < bet) {
                int gap = bet - curr_investment;
                pot.players_curr_pot_invest.put(player.id, pot.players_curr_pot_invest.get(player.id) + gap);
                m_active_players.get(player.id).curr_game_score -= gap;
                pot.pot_val += gap;
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
            int gap = 0;
            for(Pot pot : pots) {
                if(chips >= pot.curr_bet)
                {
                    gap = chips - pot.curr_bet;
                    pot.players_curr_pot_invest.put(player.id,pot.players_curr_pot_invest.get(player.id) + gap);
                    chips -= gap;
                    pot.pot_val += gap;
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
            for(Pot pot : pots) {
                pot.contributors.remove(player);
                pot.players_curr_pot_invest.remove(player.id);
            }
        }

    }

}


