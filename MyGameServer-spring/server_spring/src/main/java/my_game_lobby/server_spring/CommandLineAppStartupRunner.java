package my_game_lobby.server_spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CommandLineAppStartupRunner.class);
    @Autowired
    AllClients m_clients;

    @Override
    public void run(String...args) throws Exception {
        System.out.println("Server Listening......");
        logger.info ("Server Listening......");
        Socket s = null;
        ServerSocket login = null;
        m_clients.DoBackup ();

        try
        {
            login = new ServerSocket(9000); // can also use static final PORT_NUM , when defined
            //lobby = new ServerSocket(9001);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.out.println("Server error");

        }

        while(true)
        {
            try
            {
                assert login != null;
                s= login.accept();
                System.out.println("connection Established");
                ServerThread st=new ServerThread(s);
                st.start();
            }

            catch(Exception e)
            {
                e.printStackTrace();
                System.out.println("Connection Error");

            }
        }


    }

}
