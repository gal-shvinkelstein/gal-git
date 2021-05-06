package my_game_lobby.server_spring;

import java.io.Serializable;

public class MsgHeader implements Serializable {
    public ReqType req_type;
    public int usr_Id;
    public int usr_pass;
    public boolean login_status;
    public int lobby_id;
    public int game_status;
    public String game_manger_msg;
    public int quantity_param;
    public Object buffer;
}
