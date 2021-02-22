package test;

public class Point
{
    public Point(int x, int y)
    {
        m_x = x;
        m_y = y;
    }
    public String toString()
    {
        return "(" + m_x + ", " + m_y + ")";
    }
    private int m_x;
    private int m_y;
}
