import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher
{
    public Dispatcher(ObjectOutputStream os)
    {
        m_os = os;
        m_log_req = new LoginServer(this);
        m_commands = new HashMap<>();

        m_commands.put(ReqType.connection, () -> {
            try {
                Connected();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(ReqType.Register,() -> m_log_req.RegisterNewGamer(this.m_curr_msg));
        m_commands.put(ReqType.Login, () -> m_log_req.LogIn(this.m_curr_msg));
        m_commands.put(ReqType.Logout, () -> m_log_req.LogOut());
        m_commands.put(ReqType.Purchase, () -> m_log_req.Purchase(this.m_curr_msg));
        m_commands.put(ReqType.CreateLobby, () -> {
            try {
                CreateLobby(OperateServer.GetClientList().get(this.m_curr_msg.usr_Id));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        //m_commands.put();

    }
    public void Connected() throws IOException {
        System.out.println("Connection to client succeed");
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.buffer = "Connected";
        ReplayHandler(msgHeader);
    }
    public void CreateLobby (ClientData opener) throws IOException {
        ++m_lobby_id;
        //open new lobby
        Lobby new_lobby = new Lobby(opener);
        m_lob_list.put(m_lobby_id,new_lobby);
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.game_id = m_lobby_id;
        ReplayHandler(msgHeader);
    }

    public void RequestHandler(MsgHeader msg) throws IOException {
        m_curr_msg = msg;

        m_commands.get(msg.req_type).run();
        ReplayHandler(m_curr_msg);
    }

    public void ReplayHandler(MsgHeader msg) throws IOException
    {
        m_os.writeObject(msg);
    }
    public void SetCurrMsg(MsgHeader msg)
    {
        m_curr_msg = new MsgHeader();
        m_curr_msg = msg;
    }
    private MsgHeader m_curr_msg;
    private final ObjectOutputStream m_os;
    private final LoginServer m_log_req;
    private HashMap<Integer,Lobby> m_lob_list;
    public Map<ReqType, Runnable> m_commands;
    private static int m_lobby_id;
}
