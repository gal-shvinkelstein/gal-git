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
    }

    public void StartGame(Games game)
    {

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
}
