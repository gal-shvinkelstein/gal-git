import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.*;

public class Gamer
{
    //private BitSet ByteStreams;

    public Gamer() throws UnknownHostException {
        //ByteStreams = new BitSet();

        System.out.println("Gamer created");
        m_commands = new HashMap<>();
        m_commands.put(1,() -> {
            try {
                Register(this.m_pass,this.m_id);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(2,() -> {
            try {
                LogIn(this.m_pass,this.m_id);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(3,() -> {
            try {
                LogOut();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(4,() -> CreateLobby());
        m_commands.put(5,() -> {
            try {
                Purchase(this.curr_game);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(6,() -> JoinLobby(this.curr_lobby));
        m_commands.put(7,() -> LeaveLobby());
        m_commands.put(8,() -> StartGame(this.curr_game));
        m_commands.put(9,() -> JoinGame(this.curr_game));
        m_commands.put(10,() -> LeaveGame());
        m_commands.put(11,() -> RestartGame());

        curr_port = 9000;

        //connect to main server
        InetAddress address=InetAddress.getLocalHost(); // server address?

        try {
            m_s1 = new Socket(address, curr_port);
            m_os= new ObjectOutputStream(m_s1.getOutputStream());
            m_is=new ObjectInputStream(new ObjectInputStream(m_s1.getInputStream()));

        }
        catch (IOException e){
            e.printStackTrace();
            System.err.print("IO Exception");
        }

        System.out.println("Client connected at Address : "+address);

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

    public void Register(int pass, int id) throws IOException, ClassNotFoundException
    {
        System.out.println("Sending register request " + "pass: " + pass + "id: " + id);
        m_pass =pass;
        m_id = id;
        MsgHeader msg = new MsgHeader();
        msg.req_type = ReqType.Register;
        msg.usr_pass = m_pass;
        msg.usr_Id = m_id;

        // send msg to server
        m_os.writeObject(msg);
        // received back confirmation
        MsgHeader ret;
        ret = (MsgHeader) m_is.readObject();
        System.out.println(ret.buffer);
    }
    public void LogIn(int pass, int id ) throws IOException, ClassNotFoundException {
        System.out.println("Sending Login request");
        m_pass =pass;
        m_id = id;
        MsgHeader msg = new MsgHeader();
        msg.req_type = ReqType.Login;
        msg.usr_pass = m_pass;
        msg.usr_Id = m_id;

        // send msg to server
        m_os.writeObject(msg);
        // received back confirmation + set of my games
        MsgHeader ret;
        ret = (MsgHeader) m_is.readObject();
        // update my games
        m_my_games = (Set<Games>) ret.buffer;

    }

    public void LogOut() throws IOException, ClassNotFoundException {
        System.out.println("Sending Logout request");
        MsgHeader msg = new MsgHeader();
        msg.req_type = ReqType.Logout;
        msg.usr_pass = m_pass;
        msg.usr_Id = m_id;

        // send msg to server
        m_os.writeObject(msg);
        // received back confirmation
        MsgHeader ret;
        ret = (MsgHeader) m_is.readObject();
        System.out.println(ret.buffer);
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

    public void Purchase(Games game) throws IOException, ClassNotFoundException {
        System.out.println("Sending purchase request");

        // payment logic ...

        m_my_games.add(game);
        MsgHeader msg = new MsgHeader();
        msg.req_type = ReqType.Purchase;
        msg.buffer = game;

        m_os.writeObject(msg);

        MsgHeader ret;
        ret = (MsgHeader) m_is.readObject();
        System.out.println(ret.buffer);


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
    public void DisplayMyGames()
    {
        System.out.println(m_my_games);
    }

    private int m_id;
    private int m_pass;
    private int curr_port;
    private int curr_lobby;
    private Games curr_game;
    private Set<Games> m_my_games;
    public Map<Integer, Runnable> m_commands;

    private Socket m_s1;
    private ObjectInputStream m_is;
    private ObjectOutputStream m_os;

}
