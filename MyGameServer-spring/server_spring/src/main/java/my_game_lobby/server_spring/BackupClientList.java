package my_game_lobby.server_spring;

import java.io.*;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
@Service
@Controller
@RequestMapping(path="/demo")
public class BackupClientList implements Serializable{
    @Autowired
    public clientRep repository;

    @PostMapping(path="/add")
    public void DoBackup(@RequestParam ClientData cd){
        repository.save (cd);
    }

    @Transactional
    public void UpdatePurchase(Integer id, Games new_game){
        repository.findAllById (id).map(target -> {
            ClientData clientData = (ClientData) target;
            clientData.AddGame (new_game);
            return target;
        });
    }

    @GetMapping(path="/all")
    public HashMap<Integer, ClientData> LoadBackup()
    {
        Iterable<ClientData> all_backup = repository.findAll ();
        HashMap<Integer, ClientData> ret_backup = new HashMap<> ();
        all_backup.forEach (target -> ret_backup.put (target.id, target));
        System.out.println ("Data loaded");
        return ret_backup;
    }


}