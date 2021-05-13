package my_game_lobby.server_spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CommandLineAppStartupRunner.class);
    private HashMap<Integer, Dispatcher> m_client_communication;
    public void LoginClient(Integer id, Dispatcher dispatcher)
    {
        m_client_communication.put (id, dispatcher);
    }
    public void LogoutClient(Integer id)
    {
        m_client_communication.remove (id);
    }
    public Dispatcher GetDispatcher(Integer id)
    {
        return m_client_communication.get (id);
    }

    public class AllClients {
        private Map<Integer, ClientData> m_client_list;
        private HashMap<Integer, Lobby> m_lob_list;
        private int m_lobby_id;
        @Autowired
        public BackupClientList my_backup;

        public AllClients() {
            m_client_list = new HashMap<> ();
            m_lob_list = new HashMap<> ();
            m_client_communication = new HashMap<> ();
//            my_backup = new BackupClientList ();
            m_lobby_id = 0;

        }
        public int GetNewLobbyId() {
            return ++m_lobby_id;
        }

        public Map<Integer, ClientData> GetClientList() {
            return m_client_list;
        }

        public HashMap<Integer, Lobby> GetLobList() {
            return m_lob_list;
        }

        public void AddClient(ClientData cd) {
            System.out.println ("trying to write new gamer in add func");
            m_client_list.put (cd.id, cd);
            my_backup.DoBackup (cd);
            System.out.println ("ok");
        }

        public void DoBackup() {
            Iterable<ClientData> all_backup = my_backup.LoadBackup ();
            all_backup.forEach (target -> m_client_list.put (target.id, target));
        }
    }

    @Override
    public void run(String...args) throws Exception {
        System.out.println("Server Listening......");
        logger.info ("Server Listening......");
        Socket s = null;
        ServerSocket login = null;
        m_client_communication = new HashMap<> ();
        //ServerSocket lobby = null;
        CommandLineAppStartupRunner.AllClients clients_data = new CommandLineAppStartupRunner.AllClients ();
        clients_data.DoBackup ();

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


}
