package Tools;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 2015/1/13.
 */
public class AddBlankLineToRela
{
    public static void main(String[] args)
    {
        File inFiles = new File("D:\\tempdata\\expData\\tempRela");
        for (File file : inFiles.listFiles())
        {
            System.out.println(file.getName());
            Map<String, String> map = new HashMap<String, String>();
            Calendar cal = Calendar.getInstance();
            cal.set(2013,04,01);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date d = new Date();
            try
            {
                d = df.parse("2014-12-31");

            } catch (ParseException e)
            {
                e.printStackTrace();
            }
            while (cal.getTime().compareTo(d) <= 0)
            {
                DateFormat df_m = new SimpleDateFormat("yyyy-MM");
                String date = df_m.format(cal.getTime());
                String num = "[]";
                map.put(date, num);
                cal.add(Calendar.MONTH, 1);
            }
            try
            {
                BufferedReader Reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String date;
                while ((date = Reader.readLine()) != null)
                {
                    String num = Reader.readLine();
                    map.put(date, num);
                }
                Reader.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            try
            {
                BufferedWriter Writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(file)));
                for (String date: map.keySet())
                {
                    Writer.write(date);
                    Writer.newLine();
                    Writer.write(map.get(date));
                    Writer.newLine();
                }
                Writer.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }
}
