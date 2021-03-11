//package Poker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class TexasHoldemManger implements IGamesManager{
    public TexasHoldemManger(ClientData opener)
    {
        joined_num = 0;
        player_turn_index = 0;
        game_step = 0;
        next_turn = new Vector<>();

        m_active_players.put(opener.id,opener);
        next_turn.set(joined_num++, opener.id);
        game_deck = new Deck();
        game_deck.shuffle();
    }
    @Override
    public boolean JoinGame(ClientData joiner)
    {

        m_active_players.put(joiner.id, joiner);
        next_turn.set(joined_num++, joiner.id);
        return true;
    }
    @Override
    public void RestartGame()
    {

    }
    @Override
    public void LeaveGame(ClientData leaver)
    {
        //cash out...

        m_active_players.remove(leaver.id);
    }
    @Override
    public MsgHeader Next(MsgHeader last_move)
    {
        MsgHeader ret = new MsgHeader();
        if(game_step == 0)
        {
            StartNewGame();

        }


        return ret;
    }

    private MsgHeader RoundResult() //refer to round results
    {
        MsgHeader ret = new MsgHeader();



        return ret;
    }
    private List<Card> DealNextHand()
    {
        List<Card> new_hand = game_deck.deal(2);
        return new_hand;
    }
    private void StartNewGame()
    {
        //creating pot
        //...
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

    private int game_step;
    private Vector<Integer> next_turn;
    private int player_turn_index;
    private int joined_num;
    private Deck game_deck;
    private final int max_players = 8;
    private HashMap<Integer, ClientData > m_active_players;


}
