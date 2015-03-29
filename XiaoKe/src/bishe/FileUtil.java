package bishe;

import java.io.*;

/**
 * Created by hp on 2015/3/26.
 */
public class FileUtil
{
    public static void mergeFile()
    {
        System.out.println("start!");
        try
        {
            BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\sure\\peopleExpe")));
            BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\sure\\peopleExpepp")));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:\\sure\\people")));
            String temp;
            while ((temp = br1.readLine()) != null)
            {
                bw.write(temp);
                bw.newLine();
            }
            br1.close();
            while ((temp = br2.readLine()) != null)
            {
                bw.write(temp);
                bw.newLine();
            }
            br2.close();
            bw.close();

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("end!");
    }

    public static void seperateFile()
    {
        System.out.println("start!");
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("E:\\data\\fetch_1901w-end.dat")));
            BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:\\data\\fetch_1.dat")));
            BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:\\data\\fetch_2.dat")));
            String temp;
            int count_1 = 3000000;
            int count_2 = 6000000;
            int count_3 = 9000000;
            int i = 0;
            while ((temp = br.readLine()) != null && i < count_1)
            {
                bw1.write(temp);
                bw1.newLine();
                i ++;
            }
            bw1.close();
            while ((temp = br.readLine()) != null && i < count_2)
            {
                bw2.write(temp);
                bw2.newLine();
                i ++;
            }
            bw2.close();
            bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:\\data\\fetch_3.dat")));
            while ((temp = br.readLine()) != null && i < count_3)
            {
                bw1.write(temp);
                bw1.newLine();
                i ++;
            }
            bw1.close();
            bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:\\data\\fetch_4.dat")));
            while ((temp = br.readLine()) != null)
            {
                bw2.write(temp);
                bw2.newLine();
                i ++;
            }
            bw2.close();
            br.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("end!");
    }

    public static BufferedReader Reader(String file)
    {
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return br;
    }
    public static BufferedWriter Writer(String file)
    {
        BufferedWriter bw = null;
        try
        {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return bw;
    }
}
