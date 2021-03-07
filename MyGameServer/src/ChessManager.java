import java.util.HashMap;

public class ChessManager implements IGamesManager{
    public ChessManager(ClientData opener)
    {
        m_active_players.put(opener.id,opener);
    }
    @Override
    public void JoinGame(ClientData joiner)
    {

        m_active_players.put(joiner.id, joiner);

    }
    @Override
    public void RestartGame()
    {

    }
    @Override
    public void LeaveGame(ClientData leaver)
    {
        m_active_players.remove(leaver.id);
    }
    @Override
    public MsgHeader Next(MsgHeader last_move)
    {
        MsgHeader ret = new MsgHeader();



        return ret;
    }
    @Override
    public MsgHeader SendStatus()
    {
        MsgHeader ret = new MsgHeader();



        return ret;
    }
    @Override
    public MsgHeader GameResult()
    {
        MsgHeader ret = new MsgHeader();



        return ret;
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
    private final int max_players = 2;
    private HashMap<Integer, ClientData > m_active_players;
}
