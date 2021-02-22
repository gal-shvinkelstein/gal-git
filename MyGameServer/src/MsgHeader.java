import java.nio.CharBuffer;

public class MsgHeader {
    public int req_type;
    public int req_len;
    public int usr_Id;
    public int game_id;
    public CharBuffer buffer;
}
