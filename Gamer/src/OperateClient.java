import java.nio.CharBuffer;
import java.util.Set;

public class OperateClient
{
    public class Gamer
    {
        public Gamer(int pass, int id, Set<Games> my_games)
        {
            m_pass =pass;
            m_id = id;
            m_my_games = my_games;
            curr_port = 9000;
        }
        public void Purchase(Games game)
        {
            // payment logic ...
            m_my_games.add(game);
        }
        public void JoinLobby(int lobby_num)
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

}
