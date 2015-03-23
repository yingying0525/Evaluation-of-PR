package Tools;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hp on 2014/10/21.
 */

public class Estimation
{
    public static void main(String[] args)
    {
        String dir = "D:\\tempdata\\expData\\";
        //String filename = "yaoming_new";
        File inFiles = new File(dir);
        for (String filename: inFiles.list())
        {
            FileToPeopleFile(filename, dir);
        }



        /*List<Item> list1 = mapPeople.get("2014-06--2014-07");
        List<Item> list2 = mapPeople.get("2014-08--2014-09");
        double sum = compareTwoList_ndcg(list1, list2);
        System.out.println(sum);*/

        //CompareBasedOnFirstByDay(filename,dir);
        //CompareBasedOnPrev(filename,dir);

    }

    public static List<Double> CompareBasedOnFirst(String filename, String dir)
    {
        Map<String, List<Item>> mapPeople;
        List<Double> result = new ArrayList<Double>();
        mapPeople = ReadPeopleFileToList(dir + filename + "_p");
        DateFormat df = new SimpleDateFormat("yyyy-MM");
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        String start, end;
        try
        {
            startCal.setTime(df.parse("2013-04"));
            startCal.set(Calendar.DATE, 01);
            endCal.setTime(df.parse("2013-04"));
            endCal.set(Calendar.DATE, 01);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        start = df.format(startCal.getTime());
        end = df.format(endCal.getTime());
        //System.out.println(start + "--" + end);
        List<Item> list1 = mapPeople.get(start + "--" + end);
        for (int i = 0; i < mapPeople.size() - 1; i ++)
        {
            startCal.add(Calendar.MONTH, 1);
            endCal.add(Calendar.MONTH, 1);
            start = df.format(startCal.getTime());
            end = df.format(endCal.getTime());
            //System.out.println(start + "--" + end);
            List<Item> list2 = mapPeople.get(start + "--" + end);
            double dif = compareTwoList_ndcg(list1, list2);
            //System.out.println(list1);
            //System.out.println(list2);
            result.add(new Double(dif));
            System.out.println(dif);
        }
        return result;
    }
    public static List<Double> CompareBasedOnFirstByDay(String filename, String dir)
    {
        Map<String, List<Item>> mapPeople;
        List<Double> result = new ArrayList<Double>();
        mapPeople = ReadPeopleFileToList(dir + filename + "_p");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar startCal = Calendar.getInstance();
        String start;
        try
        {
            startCal.setTime(df.parse("2013-04-01"));
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        start = df.format(startCal.getTime());
        //System.out.println(start + "--" + end);
        List<Item> list1 = mapPeople.get(start);
        for (int i = 0; i < mapPeople.size() - 1; i ++)
        {
            startCal.add(Calendar.DATE, 1);
            start = df.format(startCal.getTime());
            //System.out.println(start + "--" + end);
            List<Item> list2 = mapPeople.get(start);
            double dif = compareTwoList_ndcg(list1, list2);
            //System.out.println(list1);
            //System.out.println(list2);
            result.add(new Double(dif));
            System.out.println(dif);
        }
        return result;
    }
    public static List<Double> CompareBasedOnPrev(String filename, String dir)
    {
        Map<String, List<Item>> mapPeople;
        List<Double> result = new ArrayList<Double>();
        mapPeople = ReadPeopleFileToList(dir + filename + "_p");
        DateFormat df = new SimpleDateFormat("yyyy-MM");
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        String start, end;
        try
        {
            startCal.setTime(df.parse("2013-04"));
            startCal.set(Calendar.DATE, 01);
            endCal.setTime(df.parse("2013-04"));
            endCal.set(Calendar.DATE, 01);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        for (int i = 0; i < mapPeople.size() - 1; i ++)
        {
            start = df.format(startCal.getTime());
            end = df.format(endCal.getTime());
            //System.out.println(start + "--" + end);
            List<Item> list1T =  mapPeople.get(start + "--" + end);
            List<Item> list1 = null;
            try
            {
                list1 = deepCopy(list1T);
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
            startCal.add(Calendar.MONTH, 1);
            endCal.add(Calendar.MONTH, 1);
            start = df.format(startCal.getTime());
            end = df.format(endCal.getTime());
            //System.out.println(start + "--" + end);
            List<Item> list2T =  mapPeople.get(start + "--" + end);
            List<Item> list2 = null;
            try
            {
                list2 = deepCopy(list2T);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            //System.out.println(list1);
            //System.out.println(list2);

            double sum = compareTwoList_ndcg(list1, list2);
            //System.out.println(list1);
            //System.out.println(list2);
            result.add(new Double(sum));
            System.out.println(sum);
        }
        return result;
    }

    public static Map<String, List<Item>> ReadFileToList(String file)
    {
        Map<String, List<Item>> map = new TreeMap<String, List<Item>>();
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String time, words;

            while((time = br.readLine()) != null)
            {
                //System.out.println(time);
                words = br.readLine();
                words = words.replaceAll("\\[", "");
                words = words.replaceAll("\\]", "");
                if (words.length() == 0)
                    continue;
                String [] strs = words.split(", ");
                List<Item> tempList = new ArrayList<Item>();

                for (int i = 0; i < strs.length; i ++)
                {
                    String []subs = strs[i].split("\t");
                    String w = subs[0];
                    double r = Double.parseDouble(subs[1]);
                    Item item = new Item(i,w, r);
                    tempList.add(item);
                }
                map.put(time, tempList);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(map.keySet());
        }
        return map;
    }

    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    public static Map<String, List<Item>> ReadPeopleFileToList(String file)
    {
        Map<String, List<Item>> map = new TreeMap<String, List<Item>>();
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String time, words;

            while((time = br.readLine()) != null)
            {
                //System.out.println(time);
                words = br.readLine();
                words = words.replaceAll("\\[", "");
                words = words.replaceAll("\\]", "");
                String [] strs = words.split(", ");
                List<Item> tempList = new ArrayList<Item>();

                for (int i = 0; i < strs.length; i ++)
                {
                    String []subs = strs[i].split("\t");
                    int num = Integer.parseInt(subs[0]);
                    String w = subs[1];
                    double r = Double.parseDouble(subs[2]);
                    Item item = new Item(num,w, r);
                    tempList.add(item);
                }
                map.put(time, tempList);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return map;
    }
    public static void FileToPeopleFile(String file, String dir)
    {
        Map<String, List<Item>> mapPeople = new TreeMap<String, List<Item>>();
        Set<String> set = ReadInSet("D:\\EventTeller\\EventTellerCode\\people");

        Map<String, List<Item>> map = ReadFileToList(dir + file);
        for (Map.Entry<String, List<Item>> m : map.entrySet())
        {
            mapPeople.put(m.getKey(), peopleList(set, m.getValue()));
        }
        WriteListToFile(mapPeople,dir + "peopleListData" + File.separator +file +"_p");
    }
    public static void WriteListToFile(Map<String, List<Item>> map, String file)
    {
        try
        {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            for (Map.Entry<String, List<Item>> m : map.entrySet())
            {
                bw.write(m.getKey());
                bw.newLine();
                bw.write(String.valueOf(m.getValue()));
                bw.newLine();
            }
            bw.close();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static double compareTwoList(List<Item> list1, List<Item> list2)
    {
        double sum = 0;
        for (Item i : list2)
        {
            boolean index = false;
            Item k = new Item(0,"",0.0);
            for (Item j : list1)
            {
                if (j.compareTo(i) == 1)
                {
                    index = true;
                    k = j;
                    break;
                }
            }

            double minus = 0;
            if (index == false)
                minus = list1.size();
            else
                minus = Math.abs(k.SerialNum * k.Relation - i.SerialNum * i.Relation);
            sum = sum + minus;
        }
        return sum;
    }

    public static double compareTwoList_ndcg(List<Item> list1, List<Item> list2)
    {
        double sum = 0;
        for (Item k : list1)
        {
            k.SerialNum = list1.size() - k.SerialNum + 1;
        }
        for (Item i : list2)
        {
            boolean index = false;
            for (Item j : list1)
            {
                if (j.compareTo(i) == 1)
                {
                    index = true;
                    i.SerialNum = j.SerialNum;
                    break;
                }
            }

            double minus = 0;
            if (index == false)
                i.SerialNum = 0;
        }
        double result1 = list1.get(0).SerialNum * list1.get(0).Relation;
        for (int i = 1; i < list1.size(); i ++)
        {
            result1 += (list1.get(i).SerialNum * list1.get(i).Relation) / (Math.log10(i + 1) / Math.log10(2));
        }
        double result2 = list2.get(0).SerialNum *list2.get(0).Relation;
        for (int i = 1; i < list2.size(); i ++)
        {
            result2 += (list2.get(i).SerialNum * list2.get(i).Relation) / (Math.log10(i + 1) / Math.log10(2));
        }
        sum = 1 - result2 / result1;
        return sum;
    }

    public static List<Item> peopleList(Set<String> set, List<Item> list)
    {
        List<Item> people = new ArrayList<Item>();
        int count = 1;
        for (Item item : list)
        {
            if (count > 30)
                break;
            if (set.contains(item.Word))
            {
                item.SerialNum = count;
                people.add(item);
                count ++;
            }
        }
        return people;
    }

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
}

class Item implements Comparable<Item>, Serializable
{
    public int SerialNum;
    public String Word;
    public double Relation;

    Item(Item i)
    {
        SerialNum = i.SerialNum;
        Word = i.Word;
        Relation = i.Relation;
    }

    Item(int num, String w, double r)
    {
        SerialNum = num;
        Word = w;
        Relation = r;
    }

    @Override
    public String toString()
    {
        return SerialNum + "\t" + Word + "\t" + Relation;
    }

    @Override
    public int compareTo(Item o)
    {
        if (Word.equals(o.Word))
            return 1;
        else
            if (Word.compareTo(o.Word) > 0)
                return 0;
            else
                return -1;
    }
}