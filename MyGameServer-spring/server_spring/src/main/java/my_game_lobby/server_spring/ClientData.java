package my_game_lobby.server_spring;

import java.io.Serializable;
import java.util.EnumSet;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ClientData implements Serializable
{

    ClientData()
    {
        my_games = EnumSet.noneOf(Games.class);
    }
    public void AddGame(Games new_game)
    {
        my_games.add (new_game);
    }
    //general data
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer id;
    public int password;
    public boolean log_status;
    public EnumSet<Games> my_games;

    //data for lobby server
    public int curr_lobby_id;
    public Dispatcher client_disp;
    int curr_game_score;
    int curr_status;
}
