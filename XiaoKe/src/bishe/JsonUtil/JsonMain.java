package bishe.JsonUtil;

/**
 * Created by hp on 2014/3/28.
 */

import bishe.FileUtil;
import bishe.Statistics;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JsonMain
{
    public static void main(String[] args)
    {
        JsonRead jR = new JsonRead();
        JsonWrite jW = new JsonWrite();
        /*JSONObject obj = jR.readFromFile("read.json");
        try
        {
            //json的object 利用 getJsonObject和getJsonArray两个函数得到所需对象
            //getString函数可以得到对像的value
            System.out.println(obj.getJSONObject("section").getJSONArray("signing").toString());
            System.out.println(obj.getJSONObject("section").getString("title"));
            System.out.println(obj.toString());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }*/
        Map<String, String> map = new HashMap<String, String>();
        map.put("2","热门");map.put("4","国内");map.put("8","国外");
        map.put("16","社会");map.put("32","军事");map.put("64", "健康");
        map.put("128","体育");map.put("256","娱乐");map.put("512","财经");
        map.put("1024","科技");map.put("2048","教育");map.put("4096","法律");

        BufferedReader br = FileUtil.Reader("D:\\datadd\\xiaoke\\CountTopEvent");
        //BufferedWriter bw = FileUtil.Writer("D:\\datadd\\xiaoke\\URLAndEvent.json");
        Calendar startCal = Statistics.calUtil("2014-01-01");
        Calendar endCal = Statistics.calUtil("2014-05-31");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        JSONObject jsonObject = new JSONObject();
        while (startCal.compareTo(endCal) <= 0)
        {
            String time = df.format(startCal.getTime());
            System.out.println(time);
            try
            {
                String s = br.readLine();
                //System.out.println(s);
                JSONObject jsonObject2 = new JSONObject();
                for (int j = 1; j <= 10; j++)
                {
                    s = br.readLine();
                    //System.out.println(s);
                    JSONObject jsonObject1 = new JSONObject();
                    if (s.length() > 1)
                    {
                        String[] ss = s.split(" !\\$\\$! ");
                        System.out.println(ss[0]);
                        if (ss.length == 1)
                        {
                            jsonObject1.put("title", ss[0]);
                            jsonObject1.put("content", "");
                        }
                        else
                        {
                            jsonObject1.put("title", ss[0]);
                            jsonObject1.put("content", ss[1]);
                        }
                    }
                    jsonObject2.put(j+"", jsonObject1);
                }
                jsonObject.put(time, jsonObject2);
                br.readLine();
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            startCal.add(Calendar.DATE, 1);
        }
        try
        {
            br.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        jW.writeToFile(jsonObject, "D:\\datadd\\xiaoke\\TopEvent.json");

/*        JSONArray jArray_out = new JSONArray();
        JsonMain test = new JsonMain();*/



    }
    public Map<String, String> ReadHotPeopleList(String file)
    {
        Map<String, String> map = new HashMap<String, String>();
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        String time, line;
        try
        {
            while ((time = br.readLine()) != null)
            {
                line = br.readLine();
                map.put(time, line);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return map;
    }
    public JSONObject ReadFileToJson(String file, Map<String, Integer> map, int pid)
    {
        JSONObject json = new JSONObject();
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String time, line;
            JSONArray jsonArray = new JSONArray();
            while ((time = br.readLine()) != null)
            {
                line = br.readLine();
                line = line.replaceAll("\\[", "");
                line = line.replaceAll("\\]", "");
                if (line.length() == 0)
                    continue;
                String [] strs = line.split(", ");
                for (int i = 0; i < strs.length; i ++)
                {
                    String []subs = strs[i].split("\t");
                    int id = map.get(subs[1]);
                    double sim = Double.parseDouble(subs[2]);
                    JSONObject jsonSub = jsonGenerate(id, pid, subs[1], sim);
                    jsonArray.put(jsonSub);
                }
            }
            json.put("人物关系", jsonArray);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return json;
    }
    public JSONObject MapToJson(Map<String, String> map_num, Map<String, String> map_rela, Map<String, Integer> map_people, String name, int pid)
    {
        JSONObject json = new JSONObject();
        JSONObject json2 = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (String sn: map_num.keySet())
        {
            JSONObject jsonSub = jsonGenerate(sn, map_num.get(sn), map_rela.get(sn), map_people, pid);
            try
            {
                json2.put(sn,jsonSub);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            json.put("name",name);
            json.put("id", pid);
            json.put("data", json2);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return json;
    }
    public JSONObject MapToJson(Map<String, String> map_num, Map<String, Integer> map_people, String name, int pid)
    {
        JSONObject json = new JSONObject();
        JSONObject json2 = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        List <String> a = new ArrayList<String>();
        a.addAll(map_num.keySet());
        Collections.sort(a);
        System.out.println(a.get(0));
        for (int j = 0; j < a.size(); j ++)
        {
            String sn = a.get(j);
            //JSONObject jsonSub = jsonGenerate_num(sn, map_num.get(sn));
            String num = map_num.get(sn);
            num = num.replaceAll("\\[", "");
            num = num.replaceAll("\\]", "");
            String []strs = num.split(", ");
            int count = 0;
            for (int i = 0; i < 31; i++)
            {
                count += Integer.parseInt(strs[i]);
            }
            try
            {
                json2.put(sn, count);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            json.put("name",name);
            json.put("id", pid);
            json.put("number", json2);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return json;
    }
    public Map<String, String> ReadFileToMap(String file)
    {
        Map<String, String> map = new HashMap<String, String>();
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line_date, line_cont;
            while ((line_date = br.readLine()) != null)
            {
                line_cont = br.readLine();
                map.put(line_date, line_cont);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return map;
    }
    public Map<String, Integer> ReadPeopleFileToMap(String file)
    {
        Map<String, Integer> map = new HashMap<String, Integer>();
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = br.readLine()) != null)
            {
                if (!map.containsKey(line))
                    map.put(line, map.size() + 1);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return map;
    }

    public JSONObject jsonGenerate(int id, int pid, String name, double sim)
    {
        JSONObject json = new JSONObject();
        try
        {
            json.put("id", id).put("pid", pid).put("name", name).put("similarity", sim);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return json;
    }
    public JSONObject jsonGenerate(String s, String num, String rela, Map<String, Integer> map_people, int pid)
    {
        JSONObject json1 = new JSONObject();
        JSONObject json2 = new JSONObject();
        try
        {
            JSONObject j1 = jsonGenerate_num(s, num);
            JSONArray j2 = jsonGenerate_rela(rela, map_people, pid);
            json1.put("number", j1).put("relation", j2);
            json2.put(s,json1);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return json1;
    }
    public JSONObject jsonGenerate_num(String s, String num)
    {
        JSONObject json1 = new JSONObject();
        JSONObject json2 = new JSONObject();
        DateFormat df = new SimpleDateFormat("yyyy-MM");
        int day = 0;
        try
        {
            Date date = df.parse(s);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            day = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        num = num.replaceAll("\\[", "");
        num = num.replaceAll("\\]", "");
        String []strs = num.split(", ");
        System.out.println(s);
        for (int i = 1; i <= day ; i++)
        {
            try
            {
                json1.put(i+"",strs[i-1]);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return json1;
    }
    public JSONArray jsonGenerate_rela( String rela, Map<String, Integer> map_people, int pid)
    {
        JSONArray jsonArray = new JSONArray();
        rela = rela.replaceAll("\\[", "");
        rela = rela.replaceAll("\\]", "");
        if (rela.length() == 0)
        {
            return jsonArray;
        }
        String [] strs = rela.split(", ");
        for (int i = 0; i < strs.length; i ++)
        {
            String []subs = strs[i].split("\t");
            int id = map_people.get(subs[1]);
            double sim = Double.parseDouble(subs[2]);
            JSONObject jsonSub = jsonGenerate(id, pid, subs[1], sim);
            jsonArray.put(jsonSub);
        }

        return jsonArray;
    }
    public JSONObject jsonGenerate()
    {
        JSONObject json1 = new JSONObject();
        JSONObject json2 = new JSONObject();
        JSONObject json3 = new JSONObject();
        JSONObject json4 = new JSONObject();
        JSONObject json5 = new JSONObject();
        JSONObject json6 = new JSONObject();
        JSONObject json7 = new JSONObject();
        JSONObject json8 = new JSONObject();
        JSONArray jsonA1 = new JSONArray();
        try
        {
            json1.put("name", "author-1").put("age", "35");
            json2.put("author", json1);
            json3.put("title", "book1").put("price", "$11");
            json2.put("book", json3);

            json4.put("name","author2").put("age", 40);
            json5.put("author",json4);
            json6.put("title", "book2").put("price", "$22");
            json5.put("book", json6);
            jsonA1.put(json2).put(json5);

            json7.put("title", "book").put("signing", jsonA1);

            json8.put("section", json7);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        return json8;
    }
}
