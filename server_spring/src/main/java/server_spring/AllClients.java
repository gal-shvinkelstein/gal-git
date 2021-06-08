package server_spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AllClients {
    private Map<Integer, ClientData> m_client_list;
    private HashMap<Integer, Lobby> m_lob_list;
    private int m_lobby_id;
    private HashMap<Integer, Dispatcher> m_client_communication;
    @Autowired
    public BackupClientList my_backup;

    public AllClients() {
        m_client_list = new HashMap<> ();
        m_lob_list = new HashMap<> ();
        m_client_communication = new HashMap<> ();
        m_lobby_id = 0;

    }
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

