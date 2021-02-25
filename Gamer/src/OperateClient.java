import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class OperateClient
{
    public static void main(String[] args)
    {
        //test interface to be replaced with formal UI
        Scanner scanner = new Scanner(System.in);


    }

    public class Gamer
    {
        public Gamer()
        {

            m_commands = new HashMap<>();
            m_commands.put(1,() -> Register(this.m_pass,this.m_id));
            m_commands.put(2,() -> LogIn(this.m_pass,this.m_id));
            m_commands.put(3,() -> LogOut());
            m_commands.put(4,() -> CreateLobby());
            m_commands.put(5,() -> Purchase(this.curr_game));
            m_commands.put(6,() -> JoinLobby(this.curr_lobby));
            m_commands.put(7,() -> LeaveLobby());
            m_commands.put(8,() -> StartGame(this.curr_game));
            m_commands.put(9,() -> JoinGame(this.curr_game));
            m_commands.put(10,() -> LeaveGame());
            m_commands.put(11,() -> RestartGame());





            curr_port = 9000;

            //connect to main server

        }
        public void Register(int pass, int id)
        {
            m_pass =pass;
            m_id = id;
            MsgHeader msg = new MsgHeader();
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
            MsgHeader msg = new MsgHeader();
            msg.req_type = ReqType.Login;
            msg.usr_pass = m_pass;
            msg.usr_Id = m_id;

            // send msg to server
            // received back confirmation + set of my games
            // update my games
        }

        public void LogOut()
        {
            MsgHeader msg = new MsgHeader();
            msg.req_type = ReqType.Logout;
            msg.usr_pass = m_pass;
            msg.usr_Id = m_id;

            // send msg to server
            // received back confirmation
        }

        public void CreateLobby()
        {
            MsgHeader msg = new MsgHeader();
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
        private Games curr_game;
        private Set<Games> m_my_games;
        Map<Integer, Runnable> m_commands;

    }

}
