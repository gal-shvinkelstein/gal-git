import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.io.IOException;

public class OperateServer
{

    private static HashMap<Integer,ClientData> m_client_list;

    public static HashMap<Integer, ClientData> GetClientList()
    {
        return m_client_list;
    }
    public OperateServer() {
        m_client_list = new HashMap<>();
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

//    public static void setM_client_list(HashMap<Integer, ClientData> m_client_list) {
//        OperateServer.m_client_list = m_client_list;
//    }
}




