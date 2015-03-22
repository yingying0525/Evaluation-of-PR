package main;

/*import sleeve.device.Buffer;
import sleeve.util.HttpFactory;
import cn.ruc.mblank.util.Const;*/
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by hp on 2015/1/4.
 * eg. db data image
 */
public class DownloadPicture
{
    public static void main(String[] args) throws Exception
    {
        String dbFileDir = args[0];
        String fileDir = args[1];
        String saveFileDir = args[2];
        //readIdFromDB();
        //readFile(dbFileDir, fileDir, saveFileDir);
    }

    public static void readIdFromDB()throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\tempdata\\tempPeopleNewAll")));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:\\tempdata\\dbNew")));
        String people = "王沪宁";
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://222.29.197.238:3306/EventTeller" ;
        String user = "dbdm";
        String password = "mysql@ET453";
        Class.forName(driver);
        Connection conn = DriverManager. getConnection(url, user, password);
        if(!conn.isClosed())
            System.out.println("Succeeded connecting to the Database!" );
        Statement statement = conn.createStatement();
        int num = 0;
        while ((people = br.readLine()) != null)
        {
            int count = 0;
            System.out.println(people);
            String sql = "select * from Baike where title = '" + people + "'";
            ResultSet rs = statement.executeQuery(sql);
            bw.write(people + "  ");
            while(rs.next())
            {
                count ++;
                bw.write(rs.getInt("id") + "  ");
            }
            rs.close();
            bw.newLine();
            if (count > 0)
                num ++;
        }
        System.out.println("num = " + num);
        conn.close();
        br.close();
        bw.close();
    }

    public static void readFile(String dbFileDir, String fileDir, String saveFileDir)throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dbFileDir)));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("logFile")));
        String in;
        String imgDir = saveFileDir;
        while ((in = br.readLine()) != null)
        {
            String dir = fileDir;
            String [] strs = in.split("  ");
            if (strs.length <= 1)
                continue;
            String people = strs[0];
            String line = strs[1];
            //people = "习近平";line = "27123";
            int id = Integer.parseInt(line);
            id = id % 500;
            dir = dir + File.separator + id + File.separator + line;
            File html = new File(dir);
            Document Doc = Jsoup.parse(html, "utf-8");
            Element page = Doc.getElementById("page");
            if (page == null)
            {
                bw.write(people + ": page no");
                bw.newLine();
                continue;
            }
            Elements posters = page.getElementsByClass("posterContent");
            if (posters.size() > 0)
            {
                String style = posters.get(0).attr("style");
                int start = style.indexOf("(");
                int end = style.indexOf(")");
                style = style.substring(start + 2, end-1);
                System.out.println("1 " + people + " !##! " + style);
                savePic(style, new File(imgDir + File.separator + "1_" + people + ".png"));
            }
            else
            {
                Element side = page.getElementById("side");
                if (side == null)
                {
                    bw.write(people + ": side no");
                    bw.newLine();
                    continue;
                }
                Elements asidepics = side.getElementsByClass("aside-pic");
                if (asidepics.size() > 0)
                {
                    Elements imgs = asidepics.get(0).getElementsByTag("img");
                    if (imgs.size() > 0)
                    {
                        String src = imgs.get(0).attr("src");
                        System.out.println("3 " + people + " !##! " + src);
                        savePic(src, new File(imgDir + File.separator + "3_" + people + ".png"));
                    }
                    else
                    {
                        bw.write(people + ": aside-pic image no");
                        bw.newLine();
                    }
                }
                else
                {
                    Element img = side.getElementById("left-card-image");
                    if (img != null)
                    {

                        String src = img.attr("src");
                        System.out.println("2 " + people + " !##! " + src);
                        savePic(src, new File(imgDir + File.separator + "2_" + people + ".png"));

                    }
                    else
                    {
                        bw.write(people + ": left-card-image image no");
                        bw.newLine();
                    }
                }
            }
        }
        br.close();
        bw.close();
    }

    public static void savePic(String urlstr, File outfile)throws Exception
    {
        //new一个URL对象
        URL url = new URL(urlstr);
        //打开链接
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置请求方式为"GET"
        conn.setRequestMethod("GET");
        //超时响应时间为5秒
        conn.setConnectTimeout(5 * 1000);
        //通过输入流获取图片数据
        InputStream inStream = conn.getInputStream();
        //得到图片的二进制数据，以二进制封装得到数据，具有通用性
        byte[] data = readInputStream(inStream);
        //new一个文件对象用来保存图片，默认保存当前工程根目录
        //File imageFile = new File("BeautyGirl.jpg");
        //创建输出流
        FileOutputStream outStream = new FileOutputStream(outfile);
        //写入数据
        outStream.write(data);
        //关闭输出流
        outStream.close();
    }
    public static byte[] readInputStream(InputStream inStream) throws Exception
    {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 )
        {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }
}
