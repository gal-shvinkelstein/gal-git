import java.nio.CharBuffer;
import java.util.Set;

public class OperateClient
{
    public class Gamer
    {
        public Gamer(int pass, int id)
        {

        }
        public void Purchase(Games game)
        {
            m_my_games.add(game);
        }
        public void JoinLobby(int port_num)
        {

        }
        public void LeaveLobby()
        {

        }
        public void StartGame(Games game)
        {

        }
        public void JoinGame(Games game)
        {

        }
        public void LeaveGame()
        {

        }
        public void RestartGame()
        {

        }

        private int m_id;
        private int m_pass;
        private int curr_port;
        private Set<Games> m_my_games;

    }
    public class MsgHeader
    {
        public int req_type;
        public int req_len;
        public int usr_Id;
        public int game_id;
        public CharBuffer buffer;
    }
}
