import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.EnumSet;
import java.util.Set;

public class ClientData implements Serializable
{
    ClientData()
    {
        my_games = EnumSet.noneOf(Games.class);
    }

    //general data
    public int password;
    public int id;
    public boolean log_status;
    public EnumSet<Games> my_games;

    //data for lobby server
    public int curr_lobby_id;
    public Dispatcher client_disp;
    int curr_game_score;
    int curr_status;
}