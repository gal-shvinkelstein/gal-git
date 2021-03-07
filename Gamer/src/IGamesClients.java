public interface IGamesClients {
    MsgHeader PlayTurn(MsgHeader last_move);
    void DisplayResults(MsgHeader ret);
    String JoinMassage();

}
