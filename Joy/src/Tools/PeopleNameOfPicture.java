package Tools;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hp on 2015/1/4.
 */
public class PeopleNameOfPicture
{
    public static void main(String[] args)
    {
        File inFile = new File("D:\\tempdata\\expData\\tempData");
        String []strs = inFile.list();
        Set<String> set = new HashSet();
        for (int i = 0; i < strs.length; i++)
        {
            try
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\tempdata\\expData\\tempData\\" + strs[i])));
                String line;
                while ((line = br.readLine()) != null)
                {
                    line = br.readLine();
                    line = line.replaceAll("\\[", "");
                    line = line.replaceAll("\\]", "");
                    String[]ss = line.split(", ");
                    for (int j = 0; j < ss.length; j++)
                    {
                        set.add(ss[j].split("\t")[1]);
                    }
                }
                br.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:\\tempdata\\tempPeopleNew")));
            for (String s: set)
            {
                bw.write(s);
                bw.newLine();
            }
            System.out.println(set.size());
            bw.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
