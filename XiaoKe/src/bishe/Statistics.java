package bishe;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hp on 2015/3/29.
 */
public class Statistics
{
    public static void main(String[] args)
    {
        String start = "2014-01-01";
        String end = "2014-05-31";
        String outFile = "D:\\datadd\\xiaoke\\CountSubTopic";
        //countUrlAndEvent(start, end, outFile);
        //countWebsite(start, end, outFile);
        //countTopEvent(start, end, outFile);
        //countSubTopic(start, end, outFile);
    }
    public static Calendar calUtil(String time)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try
        {
            date = df.parse(time);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        Calendar Cal = Calendar.getInstance();
        Cal.setTime(date);
        return Cal;
    }

    public static void countUrlAndEvent(String s, String en, String outFile)
    {
        Statement stmt = DBUtil.getStatement();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        BufferedWriter bw = FileUtil.Writer(outFile);
        String sql = "";
        Calendar startCal = calUtil(s);
        Calendar endCal = calUtil(en);
        try
        {
            while (startCal.compareTo(endCal) <= 0)
            {
                String time = df.format(startCal.getTime());
                System.out.println(time);

                sql = "SELECT count(*) FROM EventTeller.Event where pubtime like '%" + time + "%';";
                ResultSet result = stmt.executeQuery(sql);
                String countEvent = "";
                while (result.next())
                    countEvent = result.getString(1);

                String countUrl = "";
                sql = "SELECT count(*) FROM EventTeller.Url where crawltime like '%" + time + "%';";
                result = stmt.executeQuery(sql);
                while (result.next())
                    countUrl = result.getString(1);

                bw.write(countUrl + "\t" + countEvent);
                bw.newLine();
                startCal.add(Calendar.DATE, 1);
            }
            bw.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    public static void countWebsite(String s, String en, String outFile)
    {
        Statement stmt = DBUtil.getStatement();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        BufferedWriter bw = FileUtil.Writer(outFile);
        String sql = "";
        Calendar startCal = calUtil(s);
        Calendar endCal = calUtil(en);
        try
        {
            while (startCal.compareTo(endCal) <= 0)
            {
                String time = df.format(startCal.getTime());
                System.out.println(time);

                sql = "SELECT website, count(*) FROM EventTeller.Url where crawltime like '%" + time + "%' group by website;";
                ResultSet result = stmt.executeQuery(sql);
                String website = "";
                String count = "";
                String total = "";
                while (result.next())
                {
                    website = result.getString(1);
                    count = result.getString(2);
                    total = total + website + "\t" + count + " !##! ";
                }
                bw.write(total);
                bw.newLine();
                startCal.add(Calendar.DATE, 1);
            }
            bw.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void countTopEvent(String s, String en, String outFile)
    {
        Statement stmt = DBUtil.getStatement();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        BufferedWriter bw = FileUtil.Writer(outFile);
        String sql = "";
        Calendar startCal = calUtil(s);
        Calendar endCal = calUtil(en);
        try
        {
            while (startCal.compareTo(endCal) <= 0)
            {
                String time = df.format(startCal.getTime());
                System.out.println(time);
                bw.write(time);
                bw.newLine();
                sql = "SELECT title, content FROM EventTeller.Event where pubtime like '%" + time + "%' order by number desc limit 10;";
                ResultSet result = stmt.executeQuery(sql);
                String title = "";
                String content = "";
                String total = "";
                while (result.next())
                {
                    title = result.getString(1);
                    content = result.getString(2);
                    content = content.replace("!##!", " ");
                    total = title + " !$$! " + content;
                    bw.write(total);
                    bw.newLine();
                }
                bw.write("$$$$$$$$$$$$$$$$$$$$");
                bw.newLine();
                startCal.add(Calendar.DATE, 1);
            }
            bw.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void countSubTopic(String s, String en, String outFile)
    {
        Statement stmt = DBUtil.getStatement();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        BufferedWriter bw = FileUtil.Writer(outFile);
        String sql = "";
        Calendar startCal = calUtil(s);
        Calendar endCal = calUtil(en);
        try
        {
            while (startCal.compareTo(endCal) <= 0)
            {
                String time = df.format(startCal.getTime());
                System.out.println(time);
                sql = "SELECT topic, count(*) FROM EventTeller.Event where pubtime like '%" + time + "%' group by topic;";
                ResultSet result = stmt.executeQuery(sql);
                String topic = "";
                String count = "";
                String total = "";
                while (result.next())
                {
                    topic = result.getString(1);
                    count = result.getString(2);
                    total = total + topic + "\t" + count + " !##! ";
                }
                bw.write(total);
                bw.newLine();
                startCal.add(Calendar.DATE, 1);
            }
            bw.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


}
