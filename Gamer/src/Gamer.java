import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    public void SetIdAndPass(int id, int pass)
    {
        m_id = id;
        m_pass = pass;
    }
    public void SetCurrLobby(int num)
    {
        curr_lobby = num;
    }
    public void SetCurrGame(String game)
    {
        curr_game = Games.valueOf(game);
    }
    public void Register(int pass, int id)
    {
        System.out.println("Sending register request");
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
        System.out.println("Sending Login request");
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
        System.out.println("Sending Logout request");
        MsgHeader msg = new MsgHeader();
        msg.req_type = ReqType.Logout;
        msg.usr_pass = m_pass;
        msg.usr_Id = m_id;

        // send msg to server
        // received back confirmation
    }

    public void CreateLobby()
    {
        System.out.println("Sending Create lobby request");
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
        System.out.println("Sending purchase request");
        // payment logic ...
        m_my_games.add(game);
    }
    public void JoinLobby(int lobby_num)
    {
        System.out.println("Sending join lobby request");

    }
    public void LeaveLobby()
    {
        System.out.println("Sending Leave lobby request");

    }
    public void StartGame(Games game)
    {
        System.out.println("Sending start game request");

    }
    public void JoinGame(Games game)
    {
        System.out.println("Sending join game request");

    }
    public void LeaveGame()
    {
        System.out.println("Sending leave game request");

    }
    public void RestartGame()
    {
        System.out.println("Sending restart game request");


    }

    private int m_id;
    private int m_pass;
    private int curr_port;
    private int curr_lobby;
    private Games curr_game;
    private Set<Games> m_my_games;
    public Map<Integer, Runnable> m_commands;

}
