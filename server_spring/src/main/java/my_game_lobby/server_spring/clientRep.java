package my_game_lobby.server_spring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface clientRep extends CrudRepository<ClientData, Integer> {

}
