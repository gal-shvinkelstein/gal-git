import java.nio.CharBuffer;

public class MsgHeader {
    public ReqType req_type;
    public int usr_Id;
    public int usr_pass;
    public int game_id;
    public CharBuffer buffer;
}
