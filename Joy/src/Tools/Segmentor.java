package Tools;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2014/10/31.
 * 读取文件，将其中的字符串用IKAnalyzer分词，存在新的文件中
 */
public class Segmentor
{
    private final static List<String> nowords = new ArrayList<String>(){{
        try{
            addAll(IOUtils.readLines(Segmentor.class.getResourceAsStream("/stopword.dic")));
        }catch(IOException e){
            System.out.println("Unabled to read stopword file");
            e.printStackTrace();
        }
    }};

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
                    if(StringUtils.isNumeric(StringUtils.remove(term,'.')))
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

    //@para dir date(文件名)
    //eg.2 OriginalData 2013-04
    //eg.1 EveryDayData 2013-04-04 2014-06-30
    public static void main(String[] args)
    {
        String flag = args[0];
        String dir = args[1];
        String start = args[2];

        try
        {
            if (flag.equals("1") )
            {
                String end = args[3];
                SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
                SimpleDateFormat sdf_m = new SimpleDateFormat( "yyyy-MM" );
                Date startDate = sdf.parse(start);
                Date endDate = sdf.parse(end);

                Calendar startCal=Calendar.getInstance();
                Calendar endCal = Calendar.getInstance();
                startCal.setTime(startDate);
                endCal.setTime(endDate);
                for(; startCal.compareTo(endCal) <= 0; startCal.add(Calendar.DATE, 1))
                {
                    start = sdf.format(startCal.getTime());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dir + File.separator + start)));
                    FileOutputStream writer = new FileOutputStream(dir + File.separator + "segment_" + start);
                    String temp;
                    while ((temp = reader.readLine()) != null)
                    {
                        String segment = IKAnalyzerSeg(temp);
                        writer.write(segment.getBytes());
                        writer.write("\n".getBytes());
                    }
                    System.out.println(start + "  end!");
                    reader.close();
                    writer.close();
                }
            }
            else
            if (flag.equals("2"))
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dir + File.separator + start)));
                FileOutputStream writer = new FileOutputStream(dir + File.separator + "segment_" + start);
                String temp;
                while ((temp = reader.readLine()) != null)
                {
                    String segment = IKAnalyzerSeg(temp);
                    writer.write(segment.getBytes());
                    writer.write("\n".getBytes());
                }
                System.out.println(start + "  end!");
                reader.close();
                writer.close();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
