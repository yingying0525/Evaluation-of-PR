package Tools;

import java.io.*;
import java.util.*;

/**
 * Created by hp on 2015/3/26.
 */
public class AnalyzeBaikeData
{
    public static void main(String[] args)
    {
        String dir = "D:\\Evolution of People Relationship\\data\\extrData_3";
        String inFile = "no";
        String outFile = "except";
        //extractWord(dir, inFile, outFile);
        //extractMultiNameFromKeyword(dir, inFile, outFile);
        //extractName(dir, inFile, outFile);
        checkNoPage(dir, inFile, outFile);
    }
    //people 和 total可以用它提取
    public static void extractWord(String dir, String inFile, String outFile)
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + File.separator + inFile)));
            Set<String> set = new HashSet<String>();
            String line = "";
            while ((line = br.readLine()) != null)
            {
                String []strs = line.split(" !##! ");
                set.add(strs[1]);
            }
            SetMapUtil.WriteOutSet(dir + File.separator + outFile, set);
            br.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //keywords 提取 输出的是一个列表 一行是一个人的所有名字
    public static void extractMultiNameFromKeyword(String dir, String inFile, String outFile)
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + File.separator + inFile)));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir + File.separator + outFile)));
            String line = "";
            while ((line = br.readLine()) != null)
            {
                String []strs = line.split(" !##! ");
                if (strs.length < 3)
                    continue;
                else
                if (strs.length == 3)
                {
                    int index = strs[2].lastIndexOf("百度百科 baike ");
                    if (index == -1)
                        continue;
                    index = index + 11;
                    if (strs[2].length() > index)
                    {
                        bw.write(strs[0] + " !##! " + strs[1] + " !##! " + strs[2].substring(index));
                        bw.newLine();
                    }
                }
                else
                {
                    strs[3] = strs[3].replace(",", " ");
                    String []ss = strs[3].split(" ");
                    List<String> list = Arrays.asList(ss);
                    int index = strs[2].lastIndexOf("百度百科 baike ");
                    if (index == -1)
                        continue;
                    index = index + 11;
                    if (strs[2].length() > index)
                    {
                        String [] sss = strs[2].substring(index).split(" ");
                        String temp = "";
                        for (String a : sss)
                        {
                            if (!list.contains(a))
                            {
                                temp = temp + a + " ";
                            }
                        }
                        if (temp.length() != 0)
                        {
                            bw.write(strs[0] + " !##! " + strs[1] + " !##! " + temp);
                            bw.newLine();
                        }
                    }
                }
            }
            br.close();
            bw.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //从keyword提取好的文件中 把所有的名字都提取出来
    public static void extractName(String dir, String inFile, String outFile)
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + File.separator + inFile)));
            Set<String> set = new HashSet<String>();
            String line = "";
            while ((line = br.readLine()) != null)
            {
                String []strs = line.split(" !##! ");
                if (strs.length < 3)
                    continue;
                else
                {
                    set.add(strs[1]);
                    set.addAll(Arrays.asList(strs[2].split(" ")));
                }
            }
            SetMapUtil.WriteOutSet(dir + File.separator + outFile, set);
            br.close();
        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void checkNoPage(String dir, String inFile, String outFile)
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + File.separator + inFile)));
            BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(dir + File.separator + "WordPage")));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir + File.separator + outFile)));
            List<String> list = new ArrayList<String>();
            String line = "";
            while ((line = br2.readLine()) != null)
            {
                String []str = line.split(" !##! ");
                list.add(str[0]);
            }

            while ((line = br.readLine()) != null)
            {

                if (!list.contains(line))
                {
                    bw.write(line);
                    bw.newLine();
                }
            }

            br.close();
            br2.close();
            bw.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
