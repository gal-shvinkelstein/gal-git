package test;

import java.util.Scanner;
class Test
{
    public static void main(String args[])
    {
        Point p = new Point(5,3);
//        int x = 5;
//        int y = 10;
//        System.out.println(x+y);
//        System.out.println("x+y");
//        System.out.println("" + x + y);
//        System.out.println("5" + "10");
//        System.out.println(5 + 10 + x + y);
//        System.out.println("output " + (x + y));
//        System.out.println("output " + x + y);
        System.out.println(p.toString());
        Scanner s = new Scanner(System.in);
        System.out.println("enter digit string and double: ");
        int x = s.nextInt();
        String str = s.next();
        double d = s.nextDouble();

        System.out.println("x = " + (x) + "d = " + d + "str = " + str);
    }
}





