package bishe;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hp on 2015/3/24.
 * 一些简单的处理数据库的工具，不想写Hibernate，总是忘记connection怎么建立，写成工具直接用~~
 */
public class DBUtil
{
    public static void main(String[] args)
    {
        Statement stmt = getStatement();
        int start = 1, end = 8887430, batch = 20000;
        Set<String> set = new HashSet<String>();
        long time = System.currentTimeMillis();
        for(; start <= end; start = start + batch)
        {
            if (start + batch - 1 < end)
                set.addAll(readFromDB(stmt, start, start + batch - 1));
            else
                set.addAll(readFromDB(stmt, start, end));
        }
        //SetMapUtil.WriteOutSet("D:\\datadd\\name", set);
        time = System.currentTimeMillis() - time;
        System.out.println("cost " + time/1000.0 + " seconds.");
    }
    public static Statement getStatement()
    {
        Connection conn = null;
        Statement stmt = null;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://222.29.197.238:3306/EventTeller", "dbdm", "mysql@ET453");
            System.out.println("conn = " + conn);
            if (!conn.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            stmt = conn.createStatement();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return stmt;
    }
    public static void insertToDB()
    {
        Connection conn = null;
        Statement stmt = null, stmt2 = null;
        ResultSet rs = null, rs2 = null ;
        String sql, sql2;
        Set<Integer> set = new HashSet<Integer>();
        try{
            Class. forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://222.29.197.238:3306/EventTeller", "dbdm", "mysql@ET453");
            System. out.println("conn = " + conn);
            if(!conn.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            //String sql ="insert into customer values(5, 'Zehua', 'Wang');";
            stmt = conn.createStatement();
            stmt2 = conn.createStatement();
            //rs = stmt.executeQuery(sql);
            //stmt.executeUpdate(sql);
            //sql= "select * from Event2";
            String inFile = "D:\\tempdata\\topIdInfo";
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
            String line;
            /*Calendar cal = Calendar.getInstance();
            DateFormat df = new SimpleDateFormat("yyyy-MM");
            Date date = df.parse("2013-06");
            cal.setTime(date);
            cal.set(Calendar.DATE, 1);*/
            int t = 0;
            while((line = br.readLine()) != null)
            {
                String []strs = line.split("\t");
                //String time = df.format(cal.getTime());
                /*sql = "insert into test.`" + time + "` SELECT test.Event.id,title,pubtime,content,source,imgs,number,day,topic,url  FROM test.Event, EventTeller.Url " +
                        "where test.Event.id = EventTeller.Url.id and pubtime like '%" + time +"%' order by number desc limit 2000";*/
                //stmt = conn.createStatement(); //yige statement shi keyi chongfu shiyong de
                //rs = stmt.executeQuery(sql);

                /*int b = stmt.executeUpdate(sql);
                System.out.println(time);
                cal.add(Calendar.MONTH, 1);
                t ++;*/

                sql = "truncate table test.`" + strs[0] + "`";
                int b = stmt.executeUpdate(sql);
                sql = "alter table test.`" + strs[0] + "` add column url varchar(300) default null";
                b = stmt.executeUpdate(sql);
                sql = "insert into test.`" + strs[0] + "` SELECT test.Event.id,title,pubtime,content,source,imgs,number,day,topic,url  FROM test.Event, EventTeller.Url " +
                        "where test.Event.id = EventTeller.Url.id and content like '%" + strs[1] +"%'";
                b = stmt.executeUpdate(sql);
                System.out.println(strs[0]);
            }
            br.close();
            /*int id = 0;
            String first;
            String last;
            while(rs.next())
            {
                id = rs.getInt( "id");
                // 输出结果
                System. out.println(id);
            }*/
            /*sql= "select * from customer where id = 1" ;
            rs2 = stmt.executeQuery(sql);

            while(rs.next())
            {
                id = rs.getInt( "id");
                first = rs.getString( "firstname");
                // 输出结果
                System. out.println(id + "\t" + first);
            }*/
        } catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                //rs2.close();
                //rs.close();
                stmt.close();
                conn.close();

            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public static Set<String> readFromDB(Statement stmt, int start, int end)
    {
        ResultSet rs = null;
        Set<String> set = new HashSet<String>();
        String sql = "select title from EventTeller.Baike where category like '%人物%' and id >= " + start + " and id <= " + end + ";";

        try
        {
            rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                String name = rs.getString("title");
                set.add(name);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        try
        {
            rs.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return set;
    }
}
