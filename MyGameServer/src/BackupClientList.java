import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BackupClientList implements Serializable{
    public BackupClientList() throws IOException, ClassNotFoundException {
            FileOutputStream f = new FileOutputStream("client_backup.ser", true);
            m_o = new ObjectOutputStream(f);
            FileInputStream fi = new FileInputStream("client_backup.ser");
            m_i = new ObjectInputStream(fi);
            my_backup_data = new HashMap<>();
            HashMap<Integer, ClientData> to_copy;
            to_copy = (HashMap<Integer, ClientData>) m_i.readObject();
            if (to_copy != null) {
                my_backup_data = to_copy;
            }

    }
    public void DoBackup(ClientData cd) throws IOException {
        my_backup_data.put(cd.id,cd);
        m_o.writeObject(my_backup_data);
    }
    public Map<Integer,ClientData> LoadBackup()
    {
        return my_backup_data;
    }

    private Map<Integer,ClientData> my_backup_data;
    private final ObjectOutputStream m_o;
    private ObjectInputStream m_i;
}
