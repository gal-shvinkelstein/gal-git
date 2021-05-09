package my_game_lobby.server_spring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Repository
public interface clientRep extends JpaRepository<ClientData, Integer> {
    Optional<Object> findAllById(Integer id);
}
