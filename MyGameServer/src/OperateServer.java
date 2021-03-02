import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.io.IOException;

public class OperateServer
{
    private HashMap<Integer, ClientData> m_client_list;

    public HashMap<Integer, ClientData> GetClientList()
    {
        return m_client_list;
    }


    public OperateServer(String host, int port) {
    }

    public static void main(String[] args)
    {
        Socket s = null;
        ServerSocket login = null;
        //ServerSocket lobby = null;
        System.out.println("Server Listening......");
        try
        {
            login = new ServerSocket(9000); // can also use static final PORT_NUM , when defined
            //lobby = new ServerSocket(9001);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.out.println("Server error");

        }

        while(true)
        {
            try
            {
                s= login.accept();
                System.out.println("connection Established");
                ServerThread st=new ServerThread(s);
                st.start();
            }

            catch(Exception e)
            {
                e.printStackTrace();
                System.out.println("Connection Error");

            }
        }

    }

}




