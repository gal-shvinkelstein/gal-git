package my_game_lobby.server_spring;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class LoginServer
{
    Dispatcher m_disp;
    @Autowired
    CommandLineAppStartupRunner all_clients;

    public LoginServer(Dispatcher disp)
    {
        m_disp = disp;
    }

    public void RegisterNewGamer(MsgHeader msg) throws IOException, ClassNotFoundException {
        System.out.println("in register new gamer1");
        ClientData new_gamer = new ClientData(msg.usr_Id, msg.usr_pass);
        new_gamer.my_games.add(Games.XCircle);
        // add all free games

        m_disp.m_client_data.AddClient(new_gamer);
        System.out.println("gamer written");
        MsgHeader ret = new MsgHeader();
        ret.buffer = "registration succeed";

        m_disp.ReplayHandler(ret);

    }

    public void LogIn(MsgHeader msg) throws IOException {
        ClientData log_c = m_disp.m_client_data.GetClientList().get(msg.usr_Id);
        MsgHeader ret = new MsgHeader();
        if(log_c == null)
        {
            ret.buffer = "wrong id";
            ret.login_status = false;
        }
        else if(log_c.password != msg.usr_pass)
        {
            ret.buffer = "wrong pass";
            ret.login_status = false;
            log_c.log_status = true;
        }
        else
        {
            //send msg login succeed
            //copy games
            ret.login_status = true;
            ret.buffer = log_c.my_games;
            log_c.log_status = true;
            all_clients.LoginClient (log_c.id, m_disp);
        }
        m_disp.ReplayHandler(ret);
    }

    public void LogOut(MsgHeader msg) throws IOException {
        ClientData log_c = m_disp.m_client_data.GetClientList().get(msg.usr_Id);
        log_c.log_status = false;
        MsgHeader ret = new MsgHeader();
        ret.buffer = "logout";
        all_clients.LogoutClient (log_c.id);
        m_disp.ReplayHandler(ret);
    }


    public void Purchase(MsgHeader msg) throws IOException, ClassNotFoundException {
        ClientData log_c = m_disp.m_client_data.GetClientList().get(msg.usr_Id);
        MsgHeader ret = new MsgHeader();
        System.out.println("in purchase request, log status: " + log_c.log_status);
        if(log_c.log_status) {
            Games new_game = (Games) msg.buffer;
            m_disp.m_client_data.GetClientList().get(msg.usr_Id).my_games.add(new_game);
            m_disp.m_client_data.my_backup.UpdatePurchase(msg.usr_Id,new_game);

            System.out.println("After last purchase for client id: " + log_c.id + " games list: " + log_c.my_games);
            ret.buffer = "game added";

        }
        else{
            System.out.println("client doesn't logged in");
            ret.buffer = "you should login first";
        }
        m_disp.ReplayHandler(ret);
    }



}