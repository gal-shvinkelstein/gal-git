import java.io.Serializable;
import java.util.HashMap;
import java.util.Scanner;

public class TexasHoldemManager implements IGamesClients {
    public TexasHoldemManager(int buy_in, int id) {
        System.out.println("in Table");
        m_scanner = new Scanner(System.in);
        doActionFactory = new DoAction();
        chips = buy_in;
        m_id = id;
        in_round_status = 1;
        m_round_step = 0;
    }

    @Override
    public MsgHeader PlayTurn(MsgHeader msg) {
        MsgHeader my_turn = new MsgHeader();
        if(msg.game_status == 0)
        {
            System.out.println("your cards is: " + msg.buffer.toString());
            System.out.println("your chip count: " + chips);
            ++m_round_step;
        }
        else if(msg.game_status == 100)
        {
            chips -= msg.quantity_param;
            System.out.println("your chip count: " + chips);
            ++m_round_step;
        }
        else if(msg.game_status == 200)
        {
            DisplayStatus(msg);
            System.out.println("your chip count: " + chips);
//            my_turn.req_type = ReqType.Wait;
//            my_turn.game_status = 200;

        }
        else  if (msg.game_status == 20)
        {
            DisplayResults(msg);
            in_round_status = 1;
            System.out.println("your chip count: " + chips);
            my_turn.game_status = 20;
        }
        else if(msg.game_status == 300) {
            System.out.println("please choose your action, ");
            System.out.println(msg.game_manger_msg);
            System.out.println("1 - call \n 2 - raise \n 3 - allin \n 4 - check \n 5 - fold \n 6 - cash out");
            int choice = m_scanner.nextInt();
            if (in_round_status == 1) {
                switch (choice) {
                    case 1:
                        if (chips <= msg.quantity_param) {
                            Action action = doActionFactory.GetAction(Action.Type.AllIn);
                            my_turn = action.Do(chips);
                        } else {
                            Action action = doActionFactory.GetAction(Action.Type.Call);
                            my_turn = action.Do(msg.quantity_param);

                            //chips -= msg.quantity_param;
                        }
                        break;
                    case 2:
                        if (chips <= msg.quantity_param) {
                            System.out.println("you don't have enough chips to raise you are allin");
                            Action action = doActionFactory.GetAction(Action.Type.AllIn);
                            my_turn = action.Do(chips);
                        } else {
                            System.out.println("insert your raise");
                            int raise = m_scanner.nextInt();
                            if (raise >= chips) {
                                System.out.println("you are allin");
                                Action action = doActionFactory.GetAction(Action.Type.AllIn);
                                my_turn = action.Do(chips);
                            } else {
                                Action action = doActionFactory.GetAction(Action.Type.Bet);
                                my_turn = action.Do(raise);

                            }
                        }
                        break;
                    case 3: {
                        Action action = doActionFactory.GetAction(Action.Type.AllIn);
                        my_turn = action.Do(chips);
                    }
                    break;
                    case 4: {
                        Action action = doActionFactory.GetAction(Action.Type.Check);
                        my_turn = action.Do(0);
                    }
                    break;
                    case 5: {
                        Action action = doActionFactory.GetAction(Action.Type.Fold);
                        my_turn = action.Do(0);
                    }
                    break;
                    case 6: {
                        Action action = doActionFactory.GetAction(Action.Type.Continue);
                        my_turn = action.Do(0);
                        //todo: cash out method
                    }
                    break;
                }
                ++m_round_step;
            }
        }
        my_turn.req_type = ReqType.PlayNext;

        my_turn.usr_Id = m_id;
        return my_turn;
    }

    @Override
    public void DisplayStatus(MsgHeader msg)
    {
        System.out.println(msg.game_manger_msg);
    }
    @Override
    public void DisplayResults(MsgHeader msg) {
        System.out.println(msg.game_manger_msg);
        if(msg.usr_Id == m_id)
        {
            System.out.println ("You win the hand");
            chips += msg.quantity_param;
        }
        else
        {
            System.out.println ("Checking for winnings");
            HashMap<Integer,Integer> round_winners;
            round_winners = (HashMap<Integer, Integer>) msg.buffer;
            if (round_winners.get(m_id) != null)
            {
                chips += round_winners.get(m_id);
                System.out.println ("you won: " + round_winners.get(m_id));
            }
        }
        System.out.println("your update chips count is: " + chips);

    }

    @Override
    public String JoinMassage() {
        return "Joined successfully";
    }

    public interface Action{
        enum Type {
            SmallBlinds,
            BigBlinds,
            Call,
            Bet,
            AllIn,
            Check,
            Fold, //update contributors
            Continue //update contributors next_turn in case of leaving
        }

        MsgHeader Do(int bet);

    }

    public class DoAction {
        public Action GetAction(Action.Type act) {
            return switch (act) {

                case SmallBlinds, BigBlinds -> null;
                case Call -> new CallA();
                case Bet -> new BetA();
                case AllIn -> new AllInA();
                case Check -> new CheckA();
                case Fold -> new FoldA();
                case Continue -> new ContinueA();
            };
        }
    }

    public class CallA implements Action {
        public MsgHeader Do(int bet) {
            MsgHeader my_turn = new MsgHeader();
            my_turn.quantity_param = bet;
            my_turn.buffer = "Call";
            chips -= bet;
            System.out.println("your update chips count is: " + chips);

            return my_turn;
        }
    }


    public class BetA implements Action {
        public MsgHeader Do(int bet) {
            MsgHeader my_turn = new MsgHeader();
            my_turn.quantity_param = bet;
            my_turn.buffer = "Bet";
            chips -= bet;
            System.out.println("your update chips count is: " + chips);

            return my_turn;
        }

    }

    public class CheckA implements Action {
        public MsgHeader Do(int bet) {
            // do nothing pass turn
            MsgHeader my_turn = new MsgHeader();
            my_turn.quantity_param = bet;
            my_turn.buffer = "Check";
            return my_turn;
        }

    }

    public class AllInA implements Action {
        public MsgHeader Do(int bet) {
            MsgHeader my_turn = new MsgHeader();
            my_turn.quantity_param = bet;
            my_turn.buffer = "AllIn";
            my_turn.req_type = ReqType.PlayNext;
            chips = 0;
            in_round_status = 0;
            return my_turn;
        }

    }

    public class FoldA implements Action {
        public MsgHeader Do(int bet) {
            MsgHeader my_turn = new MsgHeader();
            my_turn.quantity_param = bet;
            my_turn.buffer = "Fold";
            in_round_status = 0;
            return my_turn;
        }

    }

    public class ContinueA implements Action {
        public MsgHeader Do(int bet) {
            MsgHeader my_turn = new MsgHeader();
            my_turn.quantity_param = bet;
            my_turn.buffer = "Continue";
            in_round_status = 0;
            return my_turn;
        }


    }

    private Scanner m_scanner;
    private int chips;
    DoAction doActionFactory;
    private int m_id;
    public int in_round_status;
    private int m_round_step;

    public enum HandValueType {
        ROYAL_FLUSH("a Royal Flush", 9),
        STRAIGHT_FLUSH("a Straight Flush", 8),
        FOUR_OF_A_KIND("Four of a Kind", 7),
        FULL_HOUSE("a Full House", 6),
        FLUSH("a Flush", 5),
        STRAIGHT("a Straight", 4),
        THREE_OF_A_KIND("Three of a Kind", 3),
        TWO_PAIRS("Two Pairs", 2),
        ONE_PAIR("One Pair", 1),
        HIGH_CARD("a High Card", 0),
        ;
        private String description;
        private int value;

        HandValueType(String description, int value) {
            this.description = description;
            this.value = value;
        }
        public String getDescription() {
            return description;
        }
        public int getValue() {
            return value;
        }

    }
}
