package my_game_lobby.server_spring;

import java.io.*;
import java.util.HashMap;

public class BackupClientList implements Serializable{
    public BackupClientList() throws IOException, ClassNotFoundException {
        FileOutputStream f = new FileOutputStream("client_backup.ser", true);
        m_o = new ObjectOutputStream(f);
        FileInputStream fi = new FileInputStream("client_backup.ser");
        m_i = new ObjectInputStream(fi);
        my_backup_data = new HashMap<>();
        HashMap<Integer, ClientData> to_copy1;
        to_copy1 = (HashMap<Integer, ClientData>) m_i.readObject();
        if (to_copy1 != null) {
            System.out.println("upload backup succeed");
            my_backup_data = to_copy1;
        }
        else {
            System.out.println("upload backup failed");
        }

    }
    public void DoBackup(ClientData cd) throws IOException, ClassNotFoundException {
        my_backup_data.put(cd.id,cd);
        m_o.writeObject(my_backup_data);

    }
    public void UpdatePurchase(int id, Games new_game) throws IOException, ClassNotFoundException {
        my_backup_data.get(id).my_games.add(new_game);
        m_o.writeObject(my_backup_data);
    }
    public HashMap<Integer, ClientData> LoadBackup()
    {
        return my_backup_data;
    }

    private HashMap<Integer, ClientData> my_backup_data;
    private final ObjectOutputStream m_o;
    private ObjectInputStream m_i;
}