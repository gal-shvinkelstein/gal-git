public class LoginServer
{
    public LoginServer()
    {

    }

    public void RegisterNewGamer()
    {

    }

    public void LogIn()
    {

    }

    public void LogOut()
    {

    }

    public int CreateLobby (ClientData opener)
    {
        ++m_lobby_id;
        //open new lobby

        return (m_lobby_id);
    }

    private static int m_lobby_id;
}
