import java.nio.CharBuffer;
import java.util.Set;

public class OperateClient
{
    public class Gamer
    {
        public Gamer()
        {
//            m_pass =pass;
//            m_id = id;
//            m_my_games = my_games;
            curr_port = 9000;

            //connect to main server

        }
        public void Register(int pass, int id)
        {
            m_pass =pass;
            m_id = id;
            MsgHeader msg = null;
            msg.req_type = ReqType.Register;
            msg.usr_pass = m_pass;
            msg.usr_Id = m_id;

            // send msg to server
            // received back confirmation

        }
        public void LogIn(int pass, int id )
        {
            m_pass =pass;
            m_id = id;
            MsgHeader msg = null;
            msg.req_type = ReqType.Login;
            msg.usr_pass = m_pass;
            msg.usr_Id = m_id;

            // send msg to server
            // received back confirmation + set of my games
            // update my games
        }

        public void LogOut()
        {
            MsgHeader msg = null;
            msg.req_type = ReqType.Logout;
            msg.usr_pass = m_pass;
            msg.usr_Id = m_id;

            // send msg to server
            // received back confirmation
        }

        public void CreateLobby()
        {
            MsgHeader msg = null;
            msg.req_type = ReqType.CreateLobby;
            msg.usr_pass = m_pass;
            msg.usr_Id = m_id;

            // send msg to server
            // received back confirmation + lobby id and port
            // update my games
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
        private int curr_lobby;
        private Set<Games> m_my_games;

    }

}
