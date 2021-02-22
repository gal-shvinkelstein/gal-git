package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Clients {
    static int PORT = 29000;

    public static void main(String args[]) throws IOException {
        Socket socket = null;
        PrintStream printStream;
        String line = "";
        Scanner scanner = new Scanner(System.in);
        try
        {
            socket = new Socket("localhost", PORT);
            System.out.println("Connected to server: " + socket.getLocalAddress() + ":" + socket.getLocalPort());
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printStream = new PrintStream(socket.getOutputStream());
            while(!line.equals("exit"))
            {
                System.out.println("enter msg: ");
                line = scanner.nextLine();
                printStream.println(line);
                System.out.println("From Server: " + inputStream.readLine());
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            socket.close();
        }
    }
}

