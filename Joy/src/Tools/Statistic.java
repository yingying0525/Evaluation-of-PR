package Tools;

import db.hbn.HSession;
import db.hbn.Hbn;
import db.hbn.model.Event;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hp on 2014/12/24.
 *
 * 统计每个月每一天每个词出现的次数
 */
public class Statistic
{
    private List<Event> events;
    private final int BatchSize = 1000;
    private Session session;
    private  static String outPutFile = "";
    private static String inPutDir = "";
    public Set<String> people;

    private void getInstances(int countPre, int firstId){
        String sql = "from Event as obj where obj.day = " + countPre + "and obj.id > " + firstId;
        events = Hbn.getElementsFromDB(sql, 0, BatchSize, session);
        //System.out.println(events.size());
    }
    Statistic()
    {
        events = new ArrayList<Event>();
        session = HSession.getSession();
        people = ReadInSet("D:\\Evolution of People Relationship\\EPR\\Joy\\src\\people");
    }

    private final static Log log = LogFactory.getLog(Statistic.class);
    private final static IKAnalyzer analyzer = new IKAnalyzer();

    private final static List<String> nowords = new ArrayList<String>(){{//可用可不用
        try{
            addAll(IOUtils.readLines(Statistic.class.getResourceAsStream("/stopword.dic")));
        }catch(IOException e){
            log.error("Unabled to read stopword file", e);
        }
    }};

    public static void main(String[] args)
    {
        long ct = System.currentTimeMillis();
        Statistic statistic = new Statistic();
        String start = "2014-09-01"; //args[0];
        String end = "2014-09-30"; //args[1];
        outPutFile = "D:\\datadd\\"; //args[2];
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
        SimpleDateFormat sdf_m = new SimpleDateFormat( "yyyy-MM" );
        Date startDate = null;
        Date endDate = null;
        try
        {
            startDate = sdf.parse(start);
            endDate = sdf.parse(end);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        Calendar startCal=Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        startCal.setTime(startDate);
        endCal.setTime(endDate);
        for (;startCal.compareTo(endCal) < 0;startCal.add(Calendar.MONTH, 1))
        {
            int startDay = (int)(startCal.getTime().getTime() / 86400000) + 1;
            int endDay = startCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            Calendar tempCal=Calendar.getInstance();
            tempCal.setTime(startCal.getTime());
            String out = sdf_m.format(tempCal.getTime());
            System.out.println(out);
            statistic.read(startDay, startDay + endDay - 1, out);
        }
        System.out.println("end!");
        System.out.printf("TIME %d\n", (System.currentTimeMillis() - ct));
    }

    public void read(int countPre, int countPost, String out)
    {
        File folder = new File(outPutFile);
        if(!folder.exists()){
            folder.mkdirs();
        }
        BufferedWriter bw = null;
        FileOutputStream outPut = null;
        Map<String, List<Integer>> mapAll = new HashMap<String, List<Integer>>();
        try
        {
            outPut = new FileOutputStream(new File(outPutFile + File.separator + out));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        bw = new BufferedWriter(new OutputStreamWriter(outPut));

        int count = 0;
        for (int i = countPre; i <= countPost; i ++)
        {
            Map<String, Integer> map = readData(i);
            for (String s: map.keySet())
            {
                if (mapAll.containsKey(s))
                {
                    mapAll.get(s).set(count, map.get(s));
                }
                else
                {
                    List<Integer> string = new ArrayList<Integer>();
                    for (int j = 0; j < 31; j++)
                        string.add(0);
                    string.set(count, map.get(s));
                    mapAll.put(s, string);
                }
            }
            count ++;
        }

        try
        {
            for (String s :mapAll.keySet())
            {
                bw.write(s + "\t" + mapAll.get(s).toString());
                bw.newLine();
            }

            bw.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> readData(int count)
    {
        Map<String, Integer> map = new HashMap<String, Integer>();
        int firstId = 0;
        firstId = 0;
        long time = (long)(count);
        time = time * 86400000;
        Date dt = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
        System.out.println(sdf.format(dt));
        do
        {
            getInstances(count, firstId);
            if (events.size() == 0)
                break;
            for(Event et: events)
            {
                Set<String> tempPeople = new HashSet<String>();
                String title = et.getTitle();
                String content = et.getContent();
                int num = et.getNumber();
                String totalStr = title + content;
                totalStr = totalStr.replaceAll("[\\p{Z}]", "");
                totalStr = totalStr.replaceAll("[\\p{P}]", "");
                if(title.length() < 2 || content.length() < 10)
                    continue;
                String segment = IKAnalyzerSeg(totalStr);
                String []str = segment.split(" ");
                for(String s : str)
                    if (people.contains(s))
                        tempPeople.add(s);
                Iterator iterator = tempPeople.iterator();
                while(iterator.hasNext())
                {
                    String s = (String)iterator.next();
                    if (map.containsKey(s))
                        map.put(s, map.get(s) + num);
                    else
                        map.put(s, num);
                }
            }
            firstId = events.get(events.size() - 1).getId();
            //System.out.println(events.size());
        }while(events.size() == BatchSize);
        session.clear();
        return map;
    }
    public static String IKAnalyzerSeg(String sentence)
    {
        List<String> keys = new ArrayList<String>();

        if(StringUtils.isNotBlank(sentence))
        {
            StringReader reader = new StringReader(sentence);
            IKSegmenter ikseg = new IKSegmenter(reader, true);
            try{
                do{
                    Lexeme me = ikseg.next();
                    if(me == null)
                        break;
                    String term = me.getLexemeText();
                    if(StringUtils.isNumeric(StringUtils.remove(term, '.')))
                        continue;
                    if(nowords.contains(term.toLowerCase()))
                        continue;
                    keys.add(term);
                }while(true);
            }catch(IOException e)
            {
                System.out.println("Unable to split keywords");
                e.printStackTrace();
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String t: keys)
            sb.append(t + " ");
        return sb.toString();
    }
    public Set<String> ReadInSet(String file)
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
