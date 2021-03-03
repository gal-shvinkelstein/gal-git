import java.nio.channels.SocketChannel;

public class LoginServer
{
    public LoginServer(Dispatcher disp)
    {
        m_disp = disp;
    }

    public void RegisterNewGamer(MsgHeader msg)
    {
        System.out.println("in register new gamer1");
        ClientData new_gamer = new ClientData();
        new_gamer.id = msg.usr_Id;
        new_gamer.password = msg.usr_pass;
        new_gamer.my_games.add(Games.XCircle);
        // add all free games

        new_gamer.port = 9000;

        System.out.println("trying to write new gamer in list");
//        OperateServer.GetClientList().put(new_gamer.id,new_gamer);
        OperateServer.AddClient(new_gamer);
        System.out.println("gamer written");
        MsgHeader ret = new MsgHeader();
        ret.buffer = "registration succeed";
        System.out.println("trying to set buff " + ret.buffer);
        m_disp.SetCurrMsg(ret);

    }

    public void LogIn(MsgHeader msg)
    {
        ClientData log_c = OperateServer.GetClientList().get(msg.usr_Id);
        MsgHeader ret = new MsgHeader();
        if(log_c == null)
        {
           //send msg incorrect id, fixed or register
        }
        else if(log_c.password != msg.usr_pass)
        {
            //send msg incorrect pass, fixed or register
        }
        else
        {
            //send msg login succeed
            //copy games
        }
        m_disp.SetCurrMsg(ret);
    }

    public void LogOut()
    {
        MsgHeader ret = new MsgHeader();
        ret.buffer = "logout";
        //server.Remove_gamer(socketChannel);
        m_disp.SetCurrMsg(ret);
    }


    public void Purchase(MsgHeader msg)
    {
        MsgHeader ret = new MsgHeader();
        Games new_game = (Games) msg.buffer;
        OperateServer.GetClientList().get(msg.usr_Id).my_games.add(new_game);

        ret.buffer = "game added";
        m_disp.SetCurrMsg(ret);
    }

    Dispatcher m_disp;

}
