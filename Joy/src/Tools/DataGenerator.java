package Tools;

import db.hbn.HSession;
import db.hbn.model.Event;
import db.hbn.Hbn;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2014/10/22.
 * 从数据库中读取新闻的title+content 存在文件中 一个月一个文件
 */
public class DataGenerator
{
    private List<Event> events;
    private final int BatchSize = 1000;
    private Session session;
    private  static String outPutFile = "";
    private static String inPutDir = "";

    private void getInstances(int countPre, int firstId){
        String sql = "from Event as obj where obj.day = " + countPre + "and obj.id > " + firstId;
        events = Hbn.getElementsFromDB(sql, 0, BatchSize, session);
        //System.out.println(events.size());
    }
    DataGenerator()
    {
        events = new ArrayList<Event>();
        session = HSession.getSession();
    }

    private final static Log log = LogFactory.getLog(DataGenerator.class);
    private final static IKAnalyzer analyzer = new IKAnalyzer();

    private final static List<String> nowords = new ArrayList<String>(){{//可用可不用
        try{
            addAll(IOUtils.readLines(DataGenerator.class.getResourceAsStream("/stopword.dic")));
        }catch(IOException e){
            log.error("Unabled to read stopword file", e);
        }
    }};

    /**
     * 关键字切分
     * @param sentence 要分词的句子
     * @return 返回分词结果
     */
    public static List<String> splitKeywords(String sentence) {

        List<String> keys = new ArrayList<String>();

        if(StringUtils.isNotBlank(sentence))  {
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
            }catch(IOException e){
                log.error("Unable to split keywords", e);
            }
        }

        return keys;
    }
    public void readData(Calendar start, Calendar end, String out)
    {
        try
        {
            File folder = new File(outPutFile);
            if(!folder.exists()){
                folder.mkdirs();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            FileOutputStream outPut = new FileOutputStream(new File(outPutFile + File.separator + out));
            for (;start.compareTo(end) <= 0; start.add(Calendar.DATE,1))
            {
                String date = sdf.format(start.getTime());
                File file = new File(inPutDir + File.separator + date);
                if (!file.exists())
                    continue;
                System.out.println(date);
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String temp = null;
                StringBuilder sb = new StringBuilder();
                while ((temp = br.readLine()) != null)
                    sb.append(temp);
                br.close();
                String totalStr = sb.toString();
                totalStr = totalStr.replaceAll("[\\p{Z}]", "");
                totalStr = totalStr.replaceAll("[\\p{P}]", "");
                totalStr = totalStr.toLowerCase();
                //List<Term> Terms = ToAnalysis.parse(totalStr);
                List<String> Terms = splitKeywords(totalStr);
                sb = new StringBuilder() ;
                for (String t: Terms)
                    sb.append(t + " ");
                outPut.write(sb.toString().getBytes());
                outPut.write("\n".getBytes());
            }
            outPut.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void readData(int countPre, int countPost, String out)
    {
        //System.out.println(countPre);
        //System.out.println(countPost);
        try
        {
            File folder = new File(outPutFile);
            if(!folder.exists()){
                folder.mkdirs();
            }
            FileOutputStream outPut = new FileOutputStream(new File(outPutFile + File.separator + out));
            //MyStaticValue.userLibrary = exdic;
            int firstId = 0;
            for (; countPre <= countPost; countPre ++)
            {
                //System.out.println(countPre);
                firstId = 0;
                long time = (long)(countPre);
                time = time * 86400000;
                Date dt = new Date(time);
                SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
                System.out.println(sdf.format(dt));
                do
                {
                    getInstances(countPre, firstId);
                    if (events.size() == 0)
                        break;

                    for(Event et: events)
                    {
                        String title = et.getTitle();
                        String content = et.getContent();
                        String totalStr = title + content;
                        totalStr = totalStr.replaceAll("[\\p{Z}]", "");
                        totalStr = totalStr.replaceAll("[\\p{P}]", "");
                        totalStr = totalStr.toLowerCase();
                        if(title.length() < 2 || content.length() < 10)
                            continue;
                        //List<Term> Terms = ToAnalysis.parse(totalStr);
                        //List<String> Terms = DataGenerator.splitKeywords(totalStr);

                        /*StringBuilder sb = new StringBuilder() ;
                        for (String t: Terms)
                            sb.append(t + " ");

                        outPut.write(sb.toString().getBytes());*/
                        outPut.write(totalStr.getBytes());
                        outPut.write("\n".getBytes());
                    }
                    firstId = events.get(events.size() - 1).getId();
                    //System.out.println(events.size());
                }while(events.size() == BatchSize);
            }
            outPut.close();
            session.clear();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //@para startDate endDate directory
    //eg. 2013-04-01 2013-09-30 datadic 1
    //or  2013-04-01 2013-09-30 /mnt/diskc/dataExtr 2 /mnt/diskc/HtmlMergedFiles
    public static void main(String[] args)
    {
        try
        {
            long ct = System.currentTimeMillis();
            DataGenerator dataGene = new DataGenerator();
            String start = args[0];
            String end = args[1];
            outPutFile = args[2];
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
            SimpleDateFormat sdf_m = new SimpleDateFormat( "yyyy-MM" );
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);

            Calendar startCal=Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();
            startCal.setTime(startDate);
            endCal.setTime(endDate);
            if (args[3].equals("1"))
                for (;startCal.compareTo(endCal) < 0;startCal.add(Calendar.MONTH, 1))
                {
                    int startDay = (int)(startCal.getTime().getTime() / 86400000) + 1;
                    int endDay = startCal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    Calendar tempCal=Calendar.getInstance();
                    tempCal.setTime(startCal.getTime());
                    String out = sdf_m.format(tempCal.getTime());
                    System.out.println(out);
                    dataGene.readData(startDay, startDay + endDay - 1, out);
                }
                /*for (;startCal.compareTo(endCal) <= 0;startCal.add(Calendar.DATE, 1))
                {
                    int startDay = (int)(startCal.getTime().getTime() / 86400000) + 1;
                    Calendar tempCal=Calendar.getInstance();
                    tempCal.setTime(startCal.getTime());
                    String out = sdf.format(tempCal.getTime());
                    //System.out.println(out);
                    dataGene.readData(startDay, startDay, out);
                }*/
            else
            if (args[3].equals("2"))
            {
                inPutDir = args[4];
                File inputFile = new File(inPutDir);
                if (inputFile.exists())
                    for (; startCal.compareTo(endCal) <= 0; startCal.add(Calendar.MONTH, 1))
                    {
                        Calendar tempStartCal = Calendar.getInstance();
                        tempStartCal.setTime(startCal.getTime());
                        Calendar tempEndCal = Calendar.getInstance();
                        tempEndCal.setTime(startCal.getTime());
                        int dayOfMonth = startCal.getActualMaximum(Calendar.DAY_OF_MONTH);
                        tempEndCal.add(Calendar.DATE, dayOfMonth - 1);

                        String out = sdf_m.format(tempEndCal.getTime());
                        System.out.println(out);
                        dataGene.readData(tempStartCal, tempEndCal, out);
                    }
                else
                    System.out.println("input dir error!!");

            }
            System.out.println("end!");
            System.out.printf("TIME %d\n", (System.currentTimeMillis() - ct));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
