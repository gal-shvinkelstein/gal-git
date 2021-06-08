package server_spring;

import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.Objects;

@Entity
//@javax.persistence.Table(name="game_server_client_data")
@Table(name="game_server_client_data")
public class ClientData implements Serializable
{

    ClientData(Integer id, Integer pass)
    {
        my_games = EnumSet.noneOf(Games.class);
        this.id = id;
        this.password = pass;
    }
    public void AddGame(Games new_game)
    {
        my_games.add (new_game);
    }
    //general data
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @NonNull
    public Integer id;
    @NonNull
    public int password;
    @NonNull
    public boolean log_status;
    public EnumSet<Games> my_games;

    //data for lobby server
    @NonNull
    public int curr_lobby_id;
    @NonNull
    int curr_game_score;
    @NonNull
    int curr_status;


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Objects.hashCode(this.log_status);
        hash = 79 * hash + this.password;
        return hash;
    }

}
