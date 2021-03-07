public interface IGamesManager {
    void JoinGame(ClientData joiner);
    void RestartGame();
    void LeaveGame(ClientData leaver);
    MsgHeader Next(MsgHeader last_move);
    MsgHeader SendStatus();
    MsgHeader GameResult();
    int GetMaxPlayers();
    int GetCurrActiveNumOfPlayers();
}
