import java.net.Socket;
import java.util.Set;

public class ClientData
{
    //general data
    public int password;
    public int id;
    private Set<Games> my_games;

    //data for lobby server
    public int port;
    public Socket client_socket;
    int curr_game_score;
    int curr_status;
}