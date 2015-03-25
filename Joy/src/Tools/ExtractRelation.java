package Tools;

import java.io.*;
import java.util.*;

/**
 * Created by hp on 2015/3/23.
 * 提取两个人物关系的关键词，利用相似度相乘
 * 拿到两个词的训练得出的相似性高的词，然后把相同的词的相似度相乘，排序得到最相关的词
 */
public class ExtractRelation
{
    public static void main(String[] args)
    {
        String dir = "D:\\Evolution of People Relationship\\data";
        BufferedReader br1 = null;
        BufferedReader br2 = null;
        BufferedWriter bw = null;
        Map<String, Double> person1 = new HashMap<String, Double>();
        Map<String, Double> person2 = new HashMap<String, Double>();
        Map<String, Double> result = new HashMap<String, Double>();
        ValueComparator bvc =  new ValueComparator(result);
        TreeMap<String, Double> sorted_result = new TreeMap<String,Double>(bvc);
        Set<String> peopleSet = SetMapUtil.ReadInSet("D:\\Evolution of People Relationship\\EPR\\Joy\\src\\people");
        try
        {
            br1 = new BufferedReader(new InputStreamReader(new FileInputStream(dir + File.separator + "fanbingbing")));
            br2 = new BufferedReader(new InputStreamReader(new FileInputStream(dir + File.separator + "liuyifei")));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir + File.separator + "keywords-fan&liu")));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        String line;
        try
        {
            line = br1.readLine();
            System.out.println(line);
            line = br1.readLine();
            line = line.replace("[", "");
            line = line.replace("]", "");
            String []strs = line.split(", ");
            for (String s : strs)
            {
                person1.put(s.split("\t")[0], Double.parseDouble(s.split("\t")[1]));
            }
            br1.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            line = br2.readLine();
            System.out.println(line);
            line = br2.readLine();
            line = line.replace("[", "");
            line = line.replace("]", "");
            String []strs = line.split(", ");
            for (String s : strs)
            {
                person2.put(s.split("\t")[0], Double.parseDouble(s.split("\t")[1]));
            }
            br2.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        for (String s : person2.keySet())
        {
            if (person1.keySet().contains(s) && !peopleSet.contains(s))
            {
                result.put(s, person1.get(s) * person2.get(s));
            }
        }
        sorted_result.putAll(result);

        try
        {
            bw.write(sorted_result.toString());
            bw.newLine();
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

}
class ValueComparator implements Comparator<String>
{

    Map<String, Double> base;
    public ValueComparator(Map<String, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
