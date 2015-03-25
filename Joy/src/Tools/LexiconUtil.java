package Tools;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hp on 2014/11/3.
 * 处理词库中的增删改查
 */
public class LexiconUtil
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

    //去除一个文件中重复的字符串 将它们输入到另一个文件中
    public static void CheckOneSet(String in, String out)
    {
        Set<String> set = ReadInSet(in);
        WriteOutSet(out, set);
    }

    public static void CheckTwoSet(String in1, String in2, String out)
    {
        System.out.println("Set 1:");
        Set<String> set1 = ReadInSet(in1);
        System.out.println("Set 2:");
        Set<String> set2 = ReadInSet(in2);
        set1.addAll(set2);
        WriteOutSet(out, set1);
    }

    public static void AppendWord(String file, String word)
    {
        if (SearchWord(file,word))
            return;

        try
        {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            bw.write(word);
            bw.newLine();
            bw.close();
            System.out.println("The word " + word + " has been appended!!");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static boolean SearchWord(String file, String word)
    {
        Set<String> set = ReadInSet(file);
        if (set.contains(word))
        {
            System.out.println("The word " + word + " has existed!!");
            return true;
        }
        else
        {
            System.out.println("The word " + word + " doesn't exist!!");
            return false;
        }
    }
    public static void removeWordSet(String fileAll, String remove)
    {
        Set<String> setAll = ReadInSet(fileAll);
        Set<String> setRemove = ReadInSet(remove);
        for (String s: setRemove)
        {
            if (setAll.contains(s))
                setAll.remove(s);
        }
        WriteOutSet(fileAll, setAll);
    }

    public static void main(String[] args)
    {
        //CheckOneSet("D:\\EventTeller\\EventTellerCode\\total1M.dic", "D:\\EventTeller\\EventTellerCode\\total1M.dic");

        //boolean b = SearchWord("D:\\EventTeller\\EventTellerCode\\total1M.dic", "麦蒂");
        //System.out.println(b);

        //AppendWord("D:\\EventTeller\\EventTellerCode\\people", "麦蒂");

        //ReadInSet("D:\\EventTeller\\EventTellerCode\\total1M.dic");

        /*String fileIn1 = "D:\\tempdata\\tempPeopleNew";
        String fileIn2 = "D:\\data\\topPeopleSet";
        String fileOut = "D:\\tempdata\\tempPeopleNewALL";
        CheckTwoSet(fileIn1, fileIn2, fileOut);*/

        SearchWord("D:\\Evolution of People Relationship\\EPR\\Joy\\src\\people", "傅艺伟");
        SearchWord("D:\\datadd\\name", "傅艺伟");
        //removeWordSet("D:\\EventTeller\\EventTellerCode\\people", "D:\\data\\remove_1.txt");
        //CheckOneSet(fileIn2, "D:\\EventTeller\\EventTellerCode\\日本艺人");

    }

}
