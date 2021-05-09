package my_game_lobby.server_spring;

import java.util.HashMap;

class XCircleManger implements IGamesManager{
    public XCircleManger(ClientData opener)
    {
        next_turn = new int[2];
        player_turn = 0;
        step = 0;
        next_turn[0] = opener.id;
        board = new int[]{0,0,0,0,0,0,0,0,0};
        m_active_players = new HashMap<>();
        m_active_players.put(opener.id,opener);
    }
    @Override
    public boolean JoinGame(ClientData joiner)
    {
        m_active_players.put(joiner.id, joiner);
        next_turn[1] = joiner.id;
        return true;
    }
    @Override
    public void RestartGame()
    {
        for(int i = 0; i < max_players; ++i)
        {
            board[i] = 0;
        }
        player_turn = 0;
        step = 0;
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
        ret.game_status = 1;
        int status = 0;
        if (step == 0)
        {
            System.out.println(" move step: " + step);
            ret.buffer = board;
        }
        else
        {
            ret.buffer = last_move.buffer;
            player_turn = (player_turn + 1) % max_players;
            board = (int[]) last_move.buffer;
            status = GameResult();
            System.out.println(" move step: " + step);
        }
        ++step;
        ret.usr_Id = next_turn[player_turn];

        if(status != 0)
        {
            ret.game_status = 2;
            ret.game_manger_msg = "The winner is: " + next_turn[status - 1];
            ret.buffer = board;
        }
        ret.req_type = ReqType.PlayNext;
        return ret;
    }

    public int GameResult()
    {
        int winner = 0;
        if((board[0] == 1 && board[1] == 1 && board[2] == 1) || (board[3] == 1 && board[4] == 1 && board[5] == 1) ||
                (board[6] == 1 && board[7] == 1 && board[8] == 1) || (board[0] == 1 && board[4] == 1 && board[8] == 1) ||
                (board[2] == 1 && board[4] == 1 && board[6] == 1) || (board[0] == 1 && board[3] == 1 && board[6] == 1) ||
                (board[1] == 1 && board[4] == 1 && board[7] == 1) || (board[2] == 1 && board[5] == 1 && board[8] == 1))
        {
            winner = 1;
        }
        else if((board[0] == 2 && board[1] == 2 && board[2] == 2) || (board[3] == 2 && board[4] == 2 && board[5] == 2) ||
                (board[6] == 2 && board[7] == 2 && board[8] == 2) || (board[0] == 2 && board[4] == 2 && board[8] == 2) ||
                (board[2] == 2 && board[4] == 2 && board[6] == 2)|| (board[0] == 2 && board[3] == 2 && board[6] == 2) ||
                (board[1] == 2 && board[4] == 2 && board[7] == 2) || (board[2] == 2 && board[5] == 2 && board[8] == 2))
        {
            winner = 2;
        }

        return winner;
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

    @Override
    public int GetGameStep() {
        return 0;
    }

    private final int max_players = 2;
    private int player_turn;
    private int[] board;
    private int [] next_turn;
    private HashMap<Integer, ClientData > m_active_players;
    private int step;

}