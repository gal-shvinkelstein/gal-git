import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class OperateServer
{
    public static class AllClients {
        private Map<Integer, ClientData> m_client_list;
        private HashMap<Integer,Lobby> m_lob_list;
        private static int m_lobby_id;

        private BackupClientList my_backup;


        public int GetNewLobbyId()
        {
            return ++m_lobby_id;
        }
        public Map<Integer, ClientData> GetClientList() {
            return m_client_list;
        }
        public HashMap<Integer, Lobby> GetLobList() {
            return m_lob_list;
        }

        public void AddClient(ClientData cd) throws IOException
        {
            System.out.println("trying to write new gamer in add func");

            m_client_list.put(cd.id, cd);
            my_backup.DoBackup(cd);
            System.out.println("ok");

        }


        public AllClients() throws IOException, ClassNotFoundException {

                my_backup = new BackupClientList();
                m_client_list = new HashMap<>();
                // ToDo: load m_clients_list from backup file
                m_client_list = my_backup.LoadBackup();

//
                m_lob_list = new HashMap<>();
                m_lobby_id = 0;

        }
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket s = null;
        ServerSocket login = null;
        //ServerSocket lobby = null;
        System.out.println("Server Listening......");
        AllClients clients_data = new AllClients();
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
                assert login != null;
                s= login.accept();
                System.out.println("connection Established");
                ServerThread st=new ServerThread(s, clients_data);
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




