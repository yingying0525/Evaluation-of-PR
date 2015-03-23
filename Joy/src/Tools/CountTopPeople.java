package Tools;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hp on 2014/12/25.
 * 已经将人物从每个月的新闻中提取出来了，并且有每天出现的次数
 * 本程序是将人物的每天出现次数加起来，找到每个月出现次数最多的人物
 */
public class CountTopPeople
{
    public static void main(String[] args)
    {
        String dir = "D:\\data\\";
        File files = new File(dir);
        for (String file: files.list())
        {
            file = "2014-09";
            if (file.length() > 7)
                continue;
            List<People> TopPeople = new ArrayList<People>();
            System.out.println(file);
            try
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + File.separator + file)));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir + File.separator + "Top_" + file)));
                String line;

                while ((line = br.readLine() )!= null)
                {
                    String []str = line.split("\t");
                    str[1] = str[1].replaceAll("\\[", "");
                    str[1] = str[1].replaceAll("\\]", "");
                    String []ss = str[1].split(", ");
                    int count = 0;
                    for (int i = 0; i < ss.length; i++)
                    {
                        count = count + Integer.parseInt(ss[i]);
                        /*if (Integer.parseInt(ss[i]) > 100)
                            count ++;*/
                    }
                    /*if (count > 500)
                    {
                        System.out.println(str[0]);
                        continue;
                    }*/
                    /*if (count >= 30)
                    {
                        System.out.println(str[0]);
                        continue;
                    }*/

                    People person = new People(str[0], count);
                    if (TopPeople.size() < 500)
                    {
                        TopPeople.add(person);
                        Collections.sort(TopPeople);
                    }
                    else
                    {
                        //System.out.println("== 20");
                        if(person.compareTo(TopPeople.get(0)) == 1)
                        {
                            TopPeople.remove(0);
                            TopPeople.add(person);
                            Collections.sort(TopPeople);
                        }
                    }
                }
                bw.write(TopPeople.toString());
                bw.close();
                br.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            break;
        }
    }
}

class People implements Comparable
{
    public String name;
    public int num;
    People(String name, int num)
    {
        this.name = name;
        this.num = num;
    }

    @Override
    public int compareTo(Object o)
    {
        People p = (People)o;
        if(this.num > p.num)
            return 1;
        else
        if (this.num == p.num)
            return 0;
        else
            return -1;
    }
    @Override
    public String toString()
    {
        return this.name + "\t" + this.num + "\n";
    }
}
