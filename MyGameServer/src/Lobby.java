import java.io.IOException;
import java.util.HashMap;

public class Lobby
{
    public Lobby(ClientData opener, Dispatcher disp)
    {
        m_active_players = new HashMap<>();
        m_active_players.put(opener.id,opener);
        m_disp = disp;
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
        MsgHeader ret = new MsgHeader();
        System.out.println("in start game request, log status: " + msg.login_status);
        Games new_game = (Games) msg.buffer;
        if(m_active_players.get(msg.usr_Id).my_games.contains(new_game)) {

            //create new game manger
            ret.buffer = "game on!";

        }
        else{
            System.out.println("client doesn't have this game");
            ret.buffer = "you should buy the game first";
        }
        m_disp.ReplayHandler(ret);

    }
    public void RestartGame()
    {

    }

    public void JoinGame()
    {

    }
    public void LeaveGame()
    {

    }

    private void Next()
    {

    }

    private void SendStatus()
    {

    }
    private void GameResult()
    {

    }

    private HashMap<Integer, ClientData > m_active_players;
    private IGamesManager m_active_game;
    Dispatcher m_disp;
}
