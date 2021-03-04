import java.io.IOException;
import java.nio.channels.SocketChannel;

public class LoginServer
{
    public LoginServer(Dispatcher disp)
    {
        m_disp = disp;
    }

    public void RegisterNewGamer(MsgHeader msg) throws IOException {
        System.out.println("in register new gamer1");
        ClientData new_gamer = new ClientData();
        new_gamer.id = msg.usr_Id;
        new_gamer.password = msg.usr_pass;
        new_gamer.my_games.add(Games.XCircle);
        // add all free games

        new_gamer.port = 9000;

//        OperateServer.GetClientList().put(new_gamer.id,new_gamer);
        m_disp.m_client_data.AddClient(new_gamer);
        System.out.println("gamer written");
        MsgHeader ret = new MsgHeader();
        ret.buffer = "registration succeed";
        System.out.println("trying to set buff " + ret.buffer);
        m_disp.ReplayHandler(ret);

    }

    public void LogIn(MsgHeader msg) throws IOException {
        ClientData log_c = m_disp.m_client_data.GetClientList().get(msg.usr_Id);
        MsgHeader ret = new MsgHeader();
        if(log_c == null)
        {
           ret.buffer = "wrong id";
           ret.login_status = false;
           log_c.log_status = false;
        }
        else if(log_c.password != msg.usr_pass)
        {
            ret.buffer = "wrong pass";
            ret.login_status = false;
            log_c.log_status = true;
        }
        else
        {
            //send msg login succeed
            //copy games
            ret.login_status = true;
            ret.buffer = m_disp.m_client_data.GetClientList().get(log_c.id).my_games;
            log_c.log_status = true;
        }
        m_disp.ReplayHandler(ret);
    }

    public void LogOut(MsgHeader msg) throws IOException {
        ClientData log_c = m_disp.m_client_data.GetClientList().get(msg.usr_Id);
        log_c.log_status = false;
        MsgHeader ret = new MsgHeader();
        ret.buffer = "logout";
        //server.Remove_gamer(socketChannel);
        m_disp.ReplayHandler(ret);
    }


    public void Purchase(MsgHeader msg) throws IOException {
        ClientData log_c = m_disp.m_client_data.GetClientList().get(msg.usr_Id);
        MsgHeader ret = new MsgHeader();
        System.out.println("in purchase request");
        if(log_c.log_status) {

            Games new_game = (Games) msg.buffer;
            m_disp.m_client_data.GetClientList().get(msg.usr_Id).my_games.add(new_game);

            System.out.println("After last purchase client games list: " + m_disp.m_client_data.GetClientList().get(msg.usr_Id).my_games);
            ret.buffer = "game added";

        }
        else{
            ret.buffer = "you should login first";
        }
        m_disp.ReplayHandler(ret);
    }

    Dispatcher m_disp;

}
