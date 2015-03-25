package Tools;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hp on 2015/3/24.
 * set和Map的一些工具，也不知道管不管用
 */
public class SetMapUtil
{
    public static Set<String> ReadInSet(String file)
    {
        Set<String> set = new HashSet<String>();
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String temp;
            int count = 0;
            while ((temp = br.readLine()) != null)
            {
                count++;
                if (temp.equals(""))
                    continue;
                set.add(temp);
            }
            br.close();
            System.out.println("read in set end!" + " count:" + count + " size:" + set.size());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return set;
    }
    public static void WriteOutSet(String file, Set<String> set)
    {
        try
        {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            for (String s : set)
            {
                bw.write(s);
                bw.newLine();
            }
            bw.close();
            System.out.println("write out set end!" + " size:" + set.size());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void CheckTwoSet(String in1, String in2, String out)
    {
        System.out.println("Set 1:");
        Set<String> set1 = ReadInSet(in1);
        System.out.println("Set 2:");
        Set<String> set2 = ReadInSet(in2);
        Set<String> set3 = new HashSet<String>();
        for (String s : set1)
        {
            if (!set2.contains(s))
            {
                set3.add(s);
            }
        }
        WriteOutSet(out, set3);
    }

    public static void main(String[] args)
    {
        CheckTwoSet("D:\\Evolution of People Relationship\\EPR\\Joy\\src\\people", "D:\\datadd\\name", "D:\\datadd\\set3");
    }
}
