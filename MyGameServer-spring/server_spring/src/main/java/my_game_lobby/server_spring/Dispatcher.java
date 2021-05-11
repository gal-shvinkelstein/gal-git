package my_game_lobby.server_spring;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher
{
    public Dispatcher(ObjectOutputStream os, CommandLineAppStartupRunner.AllClients clients_data)
    {
        m_os = os;
        m_log_req = new LoginServer(this);
        m_commands = new HashMap<>();
        m_curr_msg = new MsgHeader();
        m_client_data = clients_data;

        m_commands.put(ReqType.connection, () -> {
            try {
                Connected();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(ReqType.Register,() -> {
            try {
                m_log_req.RegisterNewGamer(this.m_curr_msg);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(ReqType.Login, () -> {
            try {
                m_log_req.LogIn(this.m_curr_msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(ReqType.Logout, () -> {
            try {
                m_log_req.LogOut(this.m_curr_msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(ReqType.Purchase, () -> {
            try {
                m_log_req.Purchase(this.m_curr_msg);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(ReqType.CreateLobby, () -> {
            try {
                CreateLobby(m_client_data.GetClientList().get(this.m_curr_msg.usr_Id));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(ReqType.JoinLobby, () -> {
            try {
                JoinLobby(m_client_data.GetClientList().get(this.m_curr_msg.usr_Id),this.m_curr_msg.lobby_id);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(ReqType.LeaveLobby, () -> {
            try {
                LeaveLobby(m_client_data.GetClientList().get(this.m_curr_msg.usr_Id));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(ReqType.StartGame, () -> {
            try {
                m_client_data.GetLobList().get(m_client_data.GetClientList().get(this.m_curr_msg.usr_Id).curr_lobby_id).StartGame(this.m_curr_msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        m_commands.put(ReqType.LeaveGame, () -> {
            try {
                m_client_data.GetLobList().get(m_client_data.GetClientList().get(this.m_curr_msg.usr_Id).curr_lobby_id).LeaveGame(this.m_curr_msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        m_commands.put(ReqType.JoinGame, () -> {
            try {
                m_client_data.GetLobList().get(m_client_data.GetClientList().get(this.m_curr_msg.usr_Id).curr_lobby_id).JoinGame(this.m_curr_msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(ReqType.PlayNext, () -> {
            try {
                m_client_data.GetLobList().get(m_client_data.GetClientList().get(this.m_curr_msg.usr_Id).curr_lobby_id).Next(this.m_curr_msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        m_commands.put(ReqType.RestartGame, () -> {
            try {
                m_client_data.GetLobList().get(m_client_data.GetClientList().get(this.m_curr_msg.usr_Id).curr_lobby_id).RestartGame();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
    public void Connected() throws IOException {
        System.out.println("Connection to client succeed");
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.buffer = "Connected";
        ReplayHandler(msgHeader);
    }
    public void CreateLobby (ClientData opener) throws IOException {
        //open new lobby
        opener.client_disp = this;
        Lobby new_lobby = new Lobby(opener);
        int lobby_id = m_client_data.GetNewLobbyId();
        m_client_data.GetLobList().put(lobby_id,new_lobby);
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.lobby_id = lobby_id;
        opener.curr_lobby_id = lobby_id;

        ReplayHandler(msgHeader);
    }

    public void JoinLobby (ClientData joiner, int lobby_id) throws IOException {
        joiner.client_disp = this;
        joiner.curr_lobby_id = lobby_id;
        m_client_data.GetLobList().get(lobby_id).AddPlayerToLobby(joiner);
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.buffer = "player joined";

        ReplayHandler(msgHeader);
    }
    public void LeaveLobby (ClientData joiner) throws IOException {

        m_client_data.GetLobList().get(joiner.curr_lobby_id).RemovePlayerFromLobby(joiner);
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.buffer = "player leave";

        ReplayHandler(msgHeader);
    }

    public void RequestHandler(MsgHeader msg) throws IOException {
        m_curr_msg = msg;
        if (msg.req_type != ReqType.Wait)
        {
            m_commands.get(msg.req_type).run();
        }
        else if(m_client_data.GetLobList().get(msg.lobby_id).GetGameStep() > 1)
        {
            System.out.println("client " +msg.usr_Id +  " wait for his turn");


        }
    }

    public void ReplayHandler(MsgHeader msg) throws IOException
    {

        m_os.writeObject(msg);
    }

    private MsgHeader m_curr_msg;
    private final ObjectOutputStream m_os;
    private final LoginServer m_log_req;

    public Map<ReqType, Runnable> m_commands;

    public CommandLineAppStartupRunner.AllClients m_client_data;
}