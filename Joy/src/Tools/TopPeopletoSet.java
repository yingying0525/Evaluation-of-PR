package Tools;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hp on 2015/1/12.
 */
public class TopPeopletoSet
{
    public static void main(String[] args)
    {
        Set<String> set = new HashSet<String>();
        String inFile = "D:\\data\\topPeople.txt";
        String outFile = "D:\\data\\topPeopleSet_n";
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
            String temp;
            int count = 0;
            while ((temp = br.readLine()) != null)
            {
                String peopleList = br.readLine();
                String []strs = peopleList.split("\t");
                if (strs.length != 10)
                    System.out.println(temp);
                count = count + strs.length;
                for (int i = 0; i < strs.length; i++)
                {
                    set.add(strs[i]);
                }
            }
            br.close();
            for (String s : set)
            {
                bw.write(s);
                bw.newLine();
            }
            bw.close();
            System.out.println("read in set end!" + " count:" + count + " size:" + set.size());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
