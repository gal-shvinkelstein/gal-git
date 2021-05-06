package my_game_lobby.server_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class ServerSpringApplication {

	public static class AllClients {
		private Map<Integer, ClientData> m_client_list;
		private HashMap<Integer,Lobby> m_lob_list;
		private static int m_lobby_id;

		public BackupClientList my_backup;


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

		public void AddClient(ClientData cd) throws IOException, ClassNotFoundException {
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

			m_lob_list = new HashMap<>();
			m_lobby_id = 0;

		}
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		SpringApplication.run(ServerSpringApplication.class, args);
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
}


