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
        m_my_games = EnumSet.noneOf(Games.class);
        System.out.println("Gamer created");
        m_log_stat = false;
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
        m_commands.put(4,() -> {
            try {
                CreateLobby();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(5,() -> {
            try {
                Purchase(this.curr_game);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(6,() -> {
            try {
                JoinLobby(this.curr_lobby);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(7,() -> {
            try {
                LeaveLobby();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(8,() -> {
            try {
                StartGame(this.curr_game);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(9,() -> {
            try {
                JoinGame(this.curr_game);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(10,() -> {
            try {
                LeaveGame();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(11,() -> {
            try {
                RestartGame();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        int curr_port = 9000;

        //connect to main server
        InetAddress address=InetAddress.getLocalHost(); // server address?

        try {
            Socket m_s1 = new Socket(address, curr_port);
            m_os= new ObjectOutputStream(m_s1.getOutputStream());
            MsgHeader msgHeader = new MsgHeader();
            msgHeader.req_type = ReqType.connection;
            m_os.writeObject(msgHeader);
            m_is=new ObjectInputStream(m_s1.getInputStream());
            MsgHeader ret = (MsgHeader) m_is.readObject();
            System.out.println(ret.buffer);
        }
        catch (IOException | ClassNotFoundException e){
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
        System.out.println("Sending register request " + "pass: " + pass + " id: " + id);
        m_pass =pass;
        m_id = id;
        MsgHeader msg = InitMsg(ReqType.Register,m_id,m_pass,0);

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
        MsgHeader msg = InitMsg(ReqType.Login,m_id,m_pass,0);
        // send msg to server
        m_os.writeObject(msg);
        // received back confirmation + set of my games
        MsgHeader ret;
        ret = (MsgHeader) m_is.readObject();
        // update my games
        if (ret.login_status) {
            m_my_games = (EnumSet<Games>) ret.buffer;
            System.out.println("login succeed: my games: " + m_my_games);
            m_log_stat = true;
        }
        else
        {
            System.out.println(ret.buffer);
        }
    }

    public void LogOut() throws IOException, ClassNotFoundException {
        System.out.println("Sending Logout request");
        MsgHeader msg = InitMsg(ReqType.Logout,m_id,m_pass,0);
        // send msg to server
        m_os.writeObject(msg);
        // received back confirmation
        MsgHeader ret;
        ret = (MsgHeader) m_is.readObject();
        System.out.println(ret.buffer);
        m_log_stat = false;
    }

    public void CreateLobby() throws IOException, ClassNotFoundException {
        System.out.println("Sending Create lobby request");
        MsgHeader msg = InitMsg(ReqType.CreateLobby,m_id,m_pass,0);

        m_os.writeObject(msg);

        MsgHeader ret;
        ret = (MsgHeader) m_is.readObject();
        curr_lobby = ret.lobby_id;
        System.out.println("your lobby num is: " + curr_lobby + " go tell your friends");
    }

    public void Purchase(Games game) throws IOException, ClassNotFoundException {
        System.out.println("Sending purchase request for " + game);

        // payment logic ...
        MsgHeader msg = InitMsg(ReqType.Purchase,m_id,m_pass,0);
        msg.buffer = game;
        m_os.writeObject(msg);

        MsgHeader ret;
        ret = (MsgHeader) m_is.readObject();
        System.out.println(ret.buffer);
        if(ret.buffer.equals("game added")) {
            m_my_games.add(game);
        }

    }
    public void JoinLobby(int lobby_num) throws IOException, ClassNotFoundException {
        System.out.println("Sending join lobby request");
        MsgHeader msg = InitMsg(ReqType.JoinLobby,m_id,m_pass,lobby_num);
        m_os.writeObject(msg);

        MsgHeader ret;
        ret = (MsgHeader) m_is.readObject();
        curr_lobby = lobby_num;
        System.out.println(ret.buffer);
    }
    public void LeaveLobby() throws IOException, ClassNotFoundException {
        System.out.println("Sending Leave lobby request");
        MsgHeader msg = InitMsg(ReqType.LeaveLobby,m_id,m_pass,curr_lobby);
        m_os.writeObject(msg);

        MsgHeader ret;
        ret = (MsgHeader) m_is.readObject();

        System.out.println(ret.buffer);
    }
    public void StartGame(Games game) throws IOException, ClassNotFoundException {
        System.out.println("Sending start game request");
        MsgHeader msg = InitMsg(ReqType.StartGame,m_id,m_pass,curr_lobby);
        msg.buffer = game;
        m_os.writeObject(msg);

        MsgHeader ret;
        ret = (MsgHeader) m_is.readObject();

        System.out.println(ret.buffer);
        if(ret.buffer.equals("game on! waiting for other participants"))
        {
            switch (game)
            {
                case XCircle:
                    game_manger = new XCircleManager();
                    break;
                case TexasHoldem:
                    // Todo: buy in payment method
                    game_manger = new TexasHoldemManager(2500, m_id);
                    break;

                // add all games ....
            }
            WaitForManager();
        }

    }
    public void JoinGame(Games game) throws IOException, ClassNotFoundException {
        System.out.println("Sending join game request");
        MsgHeader msg = InitMsg(ReqType.JoinGame,m_id,m_pass,curr_lobby);
        msg.buffer = game;
        m_os.writeObject(msg);

        MsgHeader ret;
        ret = (MsgHeader) m_is.readObject();
        if(ret.game_status != 100) {
            switch (game)
            {
                case XCircle:
                    game_manger = new XCircleManager();
                    break;
                case TexasHoldem:
                    // Todo: buy in payment method
                    game_manger = new TexasHoldemManager(2500, m_id);
                    break;

                // add all games ....
            }

            System.out.println(ret.buffer + " " + game_manger.JoinMassage());
            WaitForManager();
        }
        else {
            System.out.println(ret.buffer);
        }
    }
    public void LeaveGame() throws IOException, ClassNotFoundException {
        System.out.println("Sending leave game request");
        MsgHeader msg = InitMsg(ReqType.LeaveGame,m_id,m_pass,curr_lobby);
        m_os.writeObject(msg);

        MsgHeader ret;
        ret = (MsgHeader) m_is.readObject();

        System.out.println(ret.buffer);
    }
    public void RestartGame() throws IOException, ClassNotFoundException {
        System.out.println("Sending restart game request");
        MsgHeader msg = InitMsg(ReqType.RestartGame,m_id,m_pass,curr_lobby);
        m_os.writeObject(msg);

        MsgHeader ret;
        ret = (MsgHeader) m_is.readObject();

        System.out.println(ret.buffer);
        WaitForManager();

    }
    public void DisplayMyGames()
    {
        System.out.println(m_my_games);
    }

    public void WaitForManager() throws IOException, ClassNotFoundException {
        System.out.println("I'm waiting");
        MsgHeader msg = InitMsg(ReqType.Wait,m_id,m_pass,curr_lobby);
        boolean status = true;
        while(status) {
            m_os.writeObject(msg);
            System.out.println("1");
            MsgHeader ret;
            ret = (MsgHeader) m_is.readObject();
            if (ret.game_status == 2) {
                game_manger.DisplayResults(ret);
                status = false;
            }else if(ret.game_status == 2000)
            {
                System.out.println("all participants has left building");
                status = false;
                // cash out...
            }
            else {
                MsgHeader next = game_manger.PlayTurn(ret);
                System.out.println("2");
                next.usr_Id = m_id;
                next.lobby_id = curr_lobby;
                if(ret.game_status != 200 && next.game_status != 20) {

                    System.out.println("Action sent");
                    m_os.writeObject(next);
                }if(next.game_status == 2000) {
                    status = false;
                    // cash out...
                    System.out.println("tnx for playing");

                }
            }
        }


    }

    private MsgHeader InitMsg(ReqType type, int id, int pass, int lobby_id)
    {
        MsgHeader msg = new MsgHeader();
        msg.req_type = type;
        msg.usr_Id = id;
        msg.usr_pass = pass;
        msg.lobby_id = lobby_id;
        msg.login_status = m_log_stat;
        return msg;
    }

    private int m_id;
    private int m_pass;
    private Boolean m_log_stat;
    private int curr_lobby;
    private Games curr_game;
    private EnumSet<Games> m_my_games;
    public Map<Integer, Runnable> m_commands;
    public IGamesClients game_manger;

    private ObjectInputStream m_is;
    private ObjectOutputStream m_os;

}
