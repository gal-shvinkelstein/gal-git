import java.util.HashMap;

public interface IGamesManager {
    boolean JoinGame(ClientData joiner);
    void RestartGame();
    void LeaveGame(ClientData leaver);
    MsgHeader Next(MsgHeader last_move);
    int GetMaxPlayers();
    int GetCurrActiveNumOfPlayers();
    HashMap<Integer, ClientData > GetForBroadcast();
}
