import java.nio.channels.SocketChannel;

public class LoginServer
{
    public LoginServer()
    {

    }

    public void RegisterNewGamer(MsgHeader msg)
    {
        ClientData new_gamer = new ClientData();
        new_gamer.id = msg.usr_Id;
        new_gamer.password = msg.usr_pass;
        new_gamer.my_games.add(Games.XCircle);
        // add all free games
        new_gamer.port = 9000;

        OperateServer.GetClientList().put(new_gamer.id,new_gamer);
    }

    public void LogIn(MsgHeader msg)
    {
        ClientData log_c = OperateServer.GetClientList().get(msg.usr_Id);
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
    }

    public void LogOut()
    {
        //server.Remove_gamer(socketChannel);
    }


    public void Purchase(MsgHeader msg)
    {
        Games new_game = (Games) msg.buffer;
        OperateServer.GetClientList().get(msg.usr_Id).my_games.add(new_game);
    }

}
