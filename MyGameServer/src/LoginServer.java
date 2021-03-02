import java.nio.channels.SocketChannel;

public class LoginServer
{
    public LoginServer()
    {

    }

    public void RegisterNewGamer(int id,int pass, OperateServer server)
    {
        ClientData new_gamer = new ClientData();
        new_gamer.id = id;
        new_gamer.password = pass;
        new_gamer.my_games.add(Games.XCircle);
        // add all free games
        new_gamer.port = 9000;

        server.GetClientList().put(new_gamer.id,new_gamer);
    }

    public void LogIn(int id, int pass, OperateServer server, SocketChannel socketChannel)
    {
        ClientData log_c = server.GetClientList().get(id);
        if(log_c == null)
        {
           //send msg incorrect id, fixed or register
        }
        else if(log_c.password != pass)
        {
            //send msg incorrect pass, fixed or register
        }
        else
        {
            //send msg login succeed
            //copy games
        }
    }

    public void LogOut(OperateServer server, SocketChannel socketChannel)
    {
        server.Remove_gamer(socketChannel);
    }

    public int CreateLobby (ClientData opener)
    {
        ++m_lobby_id;
        //open new lobby

        return (m_lobby_id);
    }

    private static int m_lobby_id;
}
