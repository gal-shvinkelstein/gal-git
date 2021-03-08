import java.util.Scanner;

public class XCircleManager implements IGamesClients{
    public XCircleManager()
    {
        System.out.println("XCircle started you are X (1)");
        m_scanner = new Scanner(System.in);
    }

    @Override
    public MsgHeader PlayTurn(MsgHeader last_move) {
        MsgHeader my_turn = new MsgHeader();
        my_turn.req_type = ReqType.PlayNext;
        if(last_move.game_status != 2) {
            int[] curr_board = (int[]) last_move.buffer;
            int count = 0;
            char c;
            for (int i = 0; i < 9; ++i) {
                if (curr_board[i] == 1) {
                    c = 'X';
                } else if (curr_board[i] == 2) {
                    c = 'O';
                } else {
                    c = (char) (i + 48);
                }
                if (count == 3) {
                    System.out.println("");
                    count = 0;
                }
                System.out.print(" | " + c);
                ++count;
            }
            System.out.println("");
            System.out.println("please pick your move from the free spots then space and your shape num");

            int choice = m_scanner.nextInt();
            int shape = m_scanner.nextInt();

            curr_board[choice] = shape;

            my_turn.buffer = curr_board;
            my_turn.game_status = 1;
        }
        else
        {
            my_turn.game_status = 2;
        }
        return my_turn;
    }


    @Override
    public void DisplayResults(MsgHeader ret) {
        System.out.println(ret.winning_msg);
        int[] curr_board = (int[]) ret.buffer;
        int count = 0;
        char c;
        for (int i = 0; i < 9; ++i) {
            if (curr_board[i] == 1) {
                c = 'X';
            } else if (curr_board[i] == 2) {
                c = 'O';
            } else {
                c = (char) (i + 48);
            }
            if (count == 3) {
                System.out.println("");
                count = 0;
            }
            System.out.print(" | " + c);
            ++count;
        }
        System.out.println("");
    }

    @Override
    public String JoinMassage() {
        return "You are O (2)";
    }

    private Scanner m_scanner;
}
