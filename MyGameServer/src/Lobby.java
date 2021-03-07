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
        if(m_active_players.get(msg.usr_Id).my_games.contains(new_game)) {

            //create new game manger
            switch (new_game) {
                case XCircle:
                    m_active_game = new XCircleManger(log_c);
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

    }
    public void RestartGame()
    {
        m_active_game.RestartGame();
    }

    public void JoinGame(MsgHeader joiner) throws IOException {
        MsgHeader ret = new MsgHeader();
        ClientData log_c = m_active_players.get(joiner.usr_Id);
        if(m_active_game.GetCurrActiveNumOfPlayers() < m_active_game.GetMaxPlayers()) {

            m_active_game.JoinGame(log_c);
            ret.buffer = "player joined";
        }
        else
        {
            ret.buffer = "game is full";
        }
        log_c.client_disp.ReplayHandler(ret);
    }
    public void LeaveGame(MsgHeader leaver) throws IOException {
        MsgHeader ret = new MsgHeader();
        ClientData log_c = m_active_players.get(leaver.usr_Id);
        m_active_game.LeaveGame(log_c);
        ret.buffer = "tnx for playing";
        log_c.client_disp.ReplayHandler(ret);
    }

    private void Next(MsgHeader last_turn) throws IOException {
        MsgHeader ret;
        ret = m_active_game.Next(last_turn);

        m_active_players.get(ret.usr_Id).client_disp.ReplayHandler(ret);
    }

    private HashMap<Integer, ClientData > m_active_players;
    private IGamesManager m_active_game;
}
