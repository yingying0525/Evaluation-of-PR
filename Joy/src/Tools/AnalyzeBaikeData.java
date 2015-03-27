package Tools;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hp on 2015/3/26.
 */
public class AnalyzeBaikeData
{
    public static void main(String[] args)
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\Evolution of People Relationship\\data\\extrData\\WordPage")));
            Set<String> set = new HashSet<String>();
            String line = "";
            while ((line = br.readLine()) != null)
            {
                String []strs = line.split(" !##! ");
                set.add(strs[1]);
            }
            SetMapUtil.WriteOutSet("D:\\Evolution of People Relationship\\data\\extrData\\word", set);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
