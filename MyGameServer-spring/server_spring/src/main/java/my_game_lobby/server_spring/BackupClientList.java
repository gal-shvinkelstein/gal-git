package my_game_lobby.server_spring;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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