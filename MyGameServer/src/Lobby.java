import java.io.IOException;
import java.util.HashMap;

public class Lobby
{
    public Lobby(ClientData opener)
    {
        m_active_players = new HashMap<>();
        m_active_players.put(opener.id,opener);

    }
    public void AddPlayerToLobby(ClientData joiner)
    {
        m_active_players.put(joiner.id,joiner);
        DisplayAllPlayers();
    }
    public void RemovePlayerFromLobby(ClientData joiner)
    {
        m_active_players.remove(joiner.id);
        DisplayAllPlayers();
    }
    public void DisplayAllPlayers()
    {
        m_active_players.forEach((k, v) ->{System.out.println("key :" + k + " client :" + v);});
    }
    public void StartGame(MsgHeader msg) throws IOException {
        ClientData log_c = m_active_players.get(msg.usr_Id);
        MsgHeader ret = new MsgHeader();
        System.out.println("in start game request, log status: " + msg.login_status);
        Games new_game = (Games) msg.buffer;
        System.out.println("for client id: " + log_c.id + " games list: " + log_c.my_games);
        if(m_active_players.get(msg.usr_Id).my_games.contains(new_game)) {

            //create new game manger
            switch (new_game) {
                case XCircle:
                    System.out.println("Creating XCircle manager");
                    m_active_game = new XCircleManger(log_c);
                    System.out.println("OK");
                    break;
                case CardsWar:
                    m_active_game = new CardsWarManager(log_c);
                    break;
                case Chess:
                    m_active_game = new ChessManager(log_c);
                    break;
                   // add all games ....

            }
            ret.buffer = "game on! waiting for other participants";
        }
        else{
            System.out.println("client doesn't have this game");
            ret.buffer = "you should buy the game first";
        }
        log_c.client_disp.ReplayHandler(ret);
        System.out.println("Reply sent");

    }
    public void RestartGame() throws IOException {
        MsgHeader ret = new MsgHeader();
        ret.game_status = 1;
        m_active_game.RestartGame();
        Next(ret);
    }

    public void JoinGame(MsgHeader joiner) throws IOException {
        MsgHeader ret = new MsgHeader();
        ClientData log_c = m_active_players.get(joiner.usr_Id);
        if(m_active_game == null)
        {
            ret.buffer = "game was not created";
            ret.game_status = 100;
            log_c.client_disp.ReplayHandler(ret);
        }
        else if(m_active_game.GetCurrActiveNumOfPlayers() < m_active_game.GetMaxPlayers()) {

            boolean status = m_active_game.JoinGame(log_c);

            if(status)
            {
                ret.buffer = "player joined - wait to your turn";
                ret.game_status = 1;
                log_c.client_disp.ReplayHandler(ret);
                Next(ret);
            }
            else
            {
                ret.buffer = "player joined - wait for more participants";
                ret.game_status = 0;
                log_c.client_disp.ReplayHandler(ret);
            }
        }
        else
        {
            ret.buffer = "game is full";
            ret.game_status = 100;
            log_c.client_disp.ReplayHandler(ret);
        }

    }
    public void LeaveGame(MsgHeader leaver) throws IOException {
        MsgHeader ret = new MsgHeader();
        ClientData log_c = m_active_players.get(leaver.usr_Id);
        m_active_game.LeaveGame(log_c);
        ret.buffer = "tnx for playing";
        log_c.client_disp.ReplayHandler(ret);
    }

    public void Next(MsgHeader last_turn) throws IOException {
        MsgHeader ret;
        ret = m_active_game.Next(last_turn);
        //check results
        if(ret.game_status != 2) {
            m_active_players.get(ret.usr_Id).client_disp.ReplayHandler(ret);
        }
        else
        {
            HashMap<Integer, ClientData> broadcast = m_active_game.GetForBroadcast();
            MsgHeader finalRet = ret;
            broadcast.forEach((k, v) -> {
                try {
                    v.client_disp.ReplayHandler(finalRet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }
    }

    private HashMap<Integer, ClientData > m_active_players;
    private IGamesManager m_active_game;
}
