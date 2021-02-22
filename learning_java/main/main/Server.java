package main;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    static int PORT = 29000;
    //Clients c;
    public static void main(String[] args) throws IOException
    {
        Socket socket = null;
        ServerSocket server = new ServerSocket(PORT);
//        DataInputStream inputStream;

        PrintStream printStream;
        String line = "";

        try
        {
            System.out.println("Server wait for connection" );
            socket = server.accept();
            System.out.println("client connect from" + socket.getInetAddress() + ":" + socket.getPort());
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            inputStream = new DataInputStream(socket.getInputStream());
            printStream = new PrintStream(socket.getOutputStream());
//            printStream.println("Welcome let's start");
            while (!line.equals("exit"))
            {
                line = inputStream.readLine();
                printStream.println(line);
                System.out.println("Client: " + line);
            }

        }
        catch(Exception e)
        {
            System.err.println(e);
        }
        finally
        {
            socket.close();
            System.out.println("Server close after all clients disconnect");
        }

    }
}
