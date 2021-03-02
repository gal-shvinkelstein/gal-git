import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread {
    private ObjectInputStream m_is;
    private ObjectOutputStream m_os;
    Socket m_s = null;

    public ServerThread(Socket s){
        this.m_s = s;
    }

    public void run()
    {
    try{
        m_os= new ObjectOutputStream(m_s.getOutputStream());
        m_is=new ObjectInputStream(new ObjectInputStream(m_s.getInputStream()));

    }catch(IOException e){
        System.out.println("IO error in server thread");
    }

    try {
        while(m_s.isConnected())
        {
            MsgHeader msg = new MsgHeader();
            msg = (MsgHeader) m_is.readObject();
            //call dispatcher handler with massage

        }
    } catch (IOException e) {

        System.out.println("IO Error/ Client " +"terminated abruptly");
    }
    catch(NullPointerException e){
        System.out.println("Client "+" Closed");
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
    finally{
    try{
        System.out.println("Connection Closing..");
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

