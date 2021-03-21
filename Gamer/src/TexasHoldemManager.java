import java.util.Scanner;

public class TexasHoldemManager implements IGamesClients {
    public TexasHoldemManager() {
        System.out.println("XCircle started X(1) for starter and O(2) for joiner");
        m_scanner = new Scanner(System.in);
    }

    @Override
    public MsgHeader PlayTurn(MsgHeader last_move) {
        MsgHeader my_turn = new MsgHeader();
        my_turn.req_type = ReqType.PlayNext;

        return my_turn;
    }


    @Override
    public void DisplayResults(MsgHeader ret) {
        System.out.println(ret.game_manger_msg);

    }

    @Override
    public String JoinMassage() {
        return "Joined successfully";
    }

    public interface Action {
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

        void Do(int bet);

    }

    public class DoAction {
        public Action GetAction(Action.Type act) {
            return switch (act) {
                case SmallBlinds -> new SmallA();
                case BigBlinds -> new BigA();
                case Call -> new CallA();
                case Bet -> new BetA();
                case AllIn -> new AllInA();
                case Check -> new CheckA();
                case Fold -> new FoldA();
                case Continue -> new ContinueA();
            };
        }
    }

    public class SmallA implements Action {
        public void Do(int bet) {

        }
    }

    public class BigA implements Action {
        public void Do(int bet) {

        }
    }

    public class CallA implements Action {
        public void Do(int bet) {


        }
    }


    public class BetA implements Action {
        public void Do(int bet) {

        }

    }

    public class CheckA implements Action {
        public void Do(int bet) {
            // do nothing pass turn
        }

    }

    public class AllInA implements Action {
        public void Do(int bet) {

        }

    }

    public class FoldA implements Action {
        public void Do(int bet) {

        }

    }

    public class ContinueA implements Action {
        public void Do(int bet) {

        }


    }

    private Scanner m_scanner;

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
