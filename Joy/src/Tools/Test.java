package Tools;

/**
 * Created by hp on 2015/3/27.
 */
public class Test
{
    public static void main(String[] args)
    {
        String s = " !##! 卞华玉 !##! 卞华玉 bianhuayu 百度百科 baike  !##! ";
        String [] ss = s.split(" !##! ");
        System.out.println(ss[2].length());
        int a = ss[2].lastIndexOf("百度百科 baike ");
        System.out.println(a);
    }
}
