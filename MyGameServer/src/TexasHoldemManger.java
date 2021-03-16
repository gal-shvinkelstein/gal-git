//package Poker;
import java.util.*;
import java.util.stream.Collectors;

public class TexasHoldemManger implements IGamesManager{
    public TexasHoldemManger(ClientData opener)
    {
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
        m_active_players.put(opener.id,opener);
        board = new ArrayList<>();

        next_turn.set(joined_num++, opener);


        game_deck = new Deck();
        game_deck.shuffle();



    }
    @Override
    public boolean JoinGame(ClientData joiner)
    {
        joiner.curr_game_score = 2500;
        joiner.curr_status = 1;
        m_active_players.put(joiner.id, joiner);
        next_turn.set(joined_num++, joiner);

        return true;
    }
    @Override
    public void RestartGame()
    {
        StartNewRound();
    }
    @Override
    public void LeaveGame(ClientData leaver)
    {
        //cash out...
        if(game_step != 0) {
            MsgHeader ret = new MsgHeader();
            ret.usr_Id = leaver.id;
            ret.req_type = ReqType.LeaveGame;
            Next(ret);
        }

        m_active_players.remove(leaver.id);
    }
    @Override
    public MsgHeader Next(MsgHeader last_move)
    {
        MsgHeader ret = new MsgHeader();
        ret.req_type = ReqType.PlayNext;
        ret.usr_Id = next_turn.get(player_turn_index).id;

        if(game_step == 0) // dealing cards
        {
            StartNewRound();
            ret.buffer = DealNextHand();
            ret.game_status = 0;
            ++in_hand_counter;
            player_turn_index = (player_turn_index + 1) % next_turn.size();
            //todo: saving each player hand data
        }
        if(game_step == 1) // placing first bet
        {
            if (massage_or_action == 0)
            {
                ret.game_status = 100;
                //small blind
                if(next_turn.get(player_turn_index).id == next_turn.get(SmallBlindIndex).id)
                {
                    ret.buffer = Action.Type.SmallBlinds;
                    ret.quantity_param = SmallBlind;
                    new Action(Action.Type.SmallBlinds,SmallBlind);
                }
                else {
                    //big blind
                    ret.buffer = Action.Type.BigBlinds;
                    ret.quantity_param = SmallBlind * 2;
                    new Action(Action.Type.BigBlinds, SmallBlind * 2);

                    massage_or_action = 2;
                }
                player_turn_index = (player_turn_index + 1) % next_turn.size();
            }
            else {
                ret = BettingRound(last_move);
            }

        }
        else if (game_step == 2) // flop
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
        }
        else if(game_step == 3 || game_step == 5 || game_step == 7) // bet round after cards open
        {
            ret = BettingRound(last_move);
        }
        else if(game_step == 4 || game_step == 6) // deal turn or river
        {
            ret = DealOneToBoard();
        }
        else
        {

        }

        if(in_hand_counter == curr_in_hand)
        {
            game_step = (game_step + 1) % NumOfGameSteps;
            in_hand_counter = 0;
            player_turn_index = next_turn.indexOf(pots.get(0).contributors.stream().findAny());
            if(game_step == 0)
            {
                ret = RoundResult();
                ++SmallBlindIndex;
            }
        }

         // update according to contributors
        return ret;
    }

    private MsgHeader BettingRound(MsgHeader last_move)
    {
        MsgHeader ret = new MsgHeader();
        if(massage_or_action == 1)
        {
            //asking next turn to take an action
            ret.game_status = 100; // 100 - take an action, client read instruction
            int curr_bet = 0;
            for(Pot pot : pots)
            {
                curr_bet += pot.GetCurrBet();
            }
            ret.game_manger_msg = "Curr bet is:  " + curr_bet;
            massage_or_action = 2;
            ++in_hand_counter;
            player_turn_index = (player_turn_index + 1) % next_turn.size();
        }
        else
        {
            //check last move action + update pot data
            Action.Type type = (Action.Type) last_move.buffer;
            new Action(type,last_move.quantity_param);
            //update all players
            ret.game_status = 200; // client read massage
            ret.game_manger_msg = pots.stream().map(Object::toString).collect(Collectors.joining(", "));
            massage_or_action = 1;
        }
        curr_in_hand = pots.get(0).contributors.size();
        if(curr_in_hand == 1)
        {
            ret = RoundResult();
        }
        return ret;
    }

    private MsgHeader DealOneToBoard()
    {
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

        //calculate results;

        return ret;
    }
    private List<Card> DealNextHand()
    {
        List<Card> new_hand = game_deck.deal(2);
        return new_hand;
    }
    private void StartNewRound()
    {
        player_turn_index = SmallBlindIndex;
        pots.forEach((n) -> n.Clear());
        int counter = next_turn.size();
        int i;
        for(i = SmallBlindIndex; i < next_turn.size(); ++i)
        {
            --counter;
            pots.get(0).contributors.add(next_turn.get(i));
        }
        i = 0;
        while (counter != 0)
        {
            pots.get(0).contributors.add(next_turn.get(i));
            ++i;
            --counter;
        }
        game_deck.reset();
        game_deck.shuffle();
    }

    @Override
    public int GetMaxPlayers()
    {
        return max_players;
    }
    @Override
    public int GetCurrActiveNumOfPlayers()
    {
        return m_active_players.size();
    }
    @Override
    public HashMap<Integer, ClientData > GetForBroadcast()
    {
        return m_active_players;
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
    private HashMap<Integer, ClientData > m_active_players;
    private int in_hand_counter;
    private int curr_in_hand;
    private final List<Pot> pots;
    private int massage_or_action;
    private final List<Card> board;



    public static class Pot
    {
        public Pot(int initial_val)
        {
            this.curr_bet = initial_val;
            contributors = new HashSet<>();
        }
        public void Clear() {
            pot_val = 0;
            contributors.clear();
        }
        public void addContributor(ClientData player) {
            contributors.add(player);
        }
        public int GetCurrBet()
        {
            return curr_bet;
        }

        public Pot SplitVal(ClientData player, int partialBet) {
            Pot pot = new Pot(curr_bet - partialBet);
            for (ClientData contributor : contributors) {
                pot.addContributor(contributor);
            }
            pot_val = partialBet;
            contributors.add(player);
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

    }
    public static class Action
    {
        public Action(Type act,int bet)
        {
            //calling the proper action
        }

        public enum Type{
            SmallBlinds,
            BigBlinds,
            Call,
            Bet,
            AllIn,
            Fold, //update contributors
            Continue //update contributors next_turn in case of leaving
        }

        private void Small(int small_blind)
        {

        }
        private void Big(int big_blind)
        {

        }

    }


}
