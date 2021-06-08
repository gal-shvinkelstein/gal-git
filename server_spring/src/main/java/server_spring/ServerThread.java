package server_spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread{
    private ObjectOutputStream m_os;
    private ObjectInputStream m_is;
    private Socket m_s = null;
    private Dispatcher m_disp;
    private int m_id;

    public ServerThread(Socket s)
    {
        this.m_s = s;
    }

    public void run()
    {
        try{
            m_os= new ObjectOutputStream(m_s.getOutputStream());
            m_is=new ObjectInputStream(m_s.getInputStream());

        }catch(IOException e){
            System.out.println("IO error in server thread");
        }

        m_disp = new Dispatcher(m_os);
        try {
            while(m_s.isConnected())
            {
                MsgHeader msg;
                msg = (MsgHeader) m_is.readObject();
                m_id = msg.usr_Id;
                m_disp.RequestHandler(msg);
            }
        } catch (IOException e) {

            System.out.println("IO Error/ Client " +"terminated abruptly");
        }
        catch(NullPointerException e){
            System.out.println("Client "+" Closed");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally{
            try{
                System.out.println("Connection Closing.." + m_id);
                if (m_is!=null){
                    m_is.close();
                    System.out.println(" Socket Input Stream Closed");
                }

                if(m_os!=null){
                    m_os.close();
                    System.out.println("Socket Out Closed");
                }
                if (m_s!=null){
                    m_s.close();
                    System.out.println("Socket Closed");
                }

            }
            catch(IOException ie){
                System.out.println("Socket Close Error");
            }
        }//end finally
    }
}
