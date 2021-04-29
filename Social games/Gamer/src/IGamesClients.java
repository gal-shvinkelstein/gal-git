public interface IGamesClients {
    MsgHeader PlayTurn(MsgHeader last_move);
    void DisplayResults(MsgHeader ret);
    void DisplayStatus(MsgHeader ret);
    String JoinMassage();

}
