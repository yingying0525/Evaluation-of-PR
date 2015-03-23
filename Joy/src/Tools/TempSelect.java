package Tools;

import java.io.*;

/**
 * Created by hp on 2014/12/30.
 */
public class TempSelect
{
    public static void main(String[] args)
    {

        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\data\\topPeopleSet")));
            String people;
            int count = 1;
            while((people = br.readLine()) != null)
            {
                generateHotNum(count, people);
                count ++;
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }


    }
    public static void generateHotNum(int num, String people)
    {
        String out = "D:\\tempdata\\expData\\tempNum\\";
        String dir = "D:\\data\\";
        File file = new File(dir);

        System.out.println(file.list().length);
        String []aa = file.list();
        for (String s: aa)
        {
            int flag = 0;
            if (s.length() > 7)
                continue;
            BufferedReader br = null;
            BufferedWriter bw = null;
            String line;
            try
            {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + File.separator + s)));
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out + num + "_" + people + "_num",true)));
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            try
            {
                while ((line = br.readLine() )!= null)
                {
                    String []str = line.split("\t");
                    if (str[0].equals(people))
                    {
                        bw.write(s);
                        bw.newLine();
                        bw.write(str[1]);
                        bw.newLine();
                        flag = 1;
                    }

                }
                if (flag == 0)
                {
                    bw.write(s);
                    bw.newLine();
                    String numLine = "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]";
                    bw.write(numLine);
                    bw.newLine();
                }
                bw.close();
                br.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }
}
