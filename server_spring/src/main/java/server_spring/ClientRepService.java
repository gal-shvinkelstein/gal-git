package server_spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;

@Service
public class ClientRepService {
    @Autowired
    private clientRep repository;

    public void SaveClient(ClientData cd){
        repository.save (cd);
    }

    public void AddGameToClient(Integer id, Games new_game){
        ClientData clientData = (ClientData) repository.findAllById (Collections.singleton (id));
        clientData.AddGame (new_game);
        repository.save (clientData);

    }

    public Iterable<ClientData> LoadBackup()
    {
        System.out.println ("Data loaded");
        return repository.findAll ();
    }
}
