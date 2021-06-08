package server_spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

@Component
@Controller
@RequestMapping(path="/client")
public class BackupClientList implements Serializable{
    @Autowired
    private ClientRepService service;

    @PostMapping(path="/add")
    public void DoBackup(@RequestParam ClientData cd){
        service.SaveClient (cd);
    }

    @PostMapping(path="/update/{id}")
    public void UpdatePurchase(Integer id, Games new_game){
        service.AddGameToClient (id, new_game);
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<ClientData> LoadBackup()
    {

        return service.LoadBackup ();
    }


}