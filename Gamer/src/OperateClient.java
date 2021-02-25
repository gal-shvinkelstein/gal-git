import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class OperateClient
{
    public static void main(String[] args)
    {
        //test interface to be replaced with formal UI
        Scanner scanner = new Scanner(System.in);
        Gamer gamer = new Gamer();
        while(true)
        {
            System.out.println("Enter choice :" + "\n" + "1 for Register / Login" + "\n" + "2 for join lobby" + "\n" +
                    "3 for purchase / start / join a game" + "\n" + "4 for other");
            int choice = scanner.nextInt();
            int req = 0;
            switch (choice)
            {
                case 1:
                    System.out.println("Enter id and password (with space):");
                    int id = scanner.nextInt();
                    int pass = scanner.nextInt();
                    gamer.SetIdAndPass(id,pass);
                    System.out.println("Enter request : " + "\n" + "1 -> Register" + "\n" + "2 -> Login");
                    req = scanner.nextInt();
                    break;
                case 2:
                    System.out.println("Enter lobby num:");
                    int num = scanner.nextInt();
                    gamer.SetCurrLobby(num);
                    req = 6;
                    break;
                case 3:
                    System.out.println("Enter game name from list:");
                    String curr = scanner.next();
                    gamer.SetCurrGame(curr);
                    System.out.println("Enter request : " + "\n" + "5 -> Purchase" + "\n" + "8 -> Start game" + "\n" + "9 -> Join game");
                    req = scanner.nextInt();
                    break;
                case 4:
                    System.out.println("Enter request : " + "\n" + "3 -> Logout" + "\n" + "4 -> Create lobby" + "\n" + "7 -> Leave lobby" +
                             "\n" + "10 -> Leave game" + "\n" + "11 -> Restart game");
                    req = scanner.nextInt();
                    break;

            }
            gamer.m_commands.get(req);
        }

    }


}
