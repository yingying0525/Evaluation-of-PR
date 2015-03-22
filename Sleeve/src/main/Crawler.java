package main;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import sleeve.util.*;
import org.apache.http.client.ClientProtocolException;

import sleeve.device.Buffer;
import sleeve.device.Utility;
import sleeve.worker.Worker;

public class Crawler extends Worker<Record, Utility>
{
	private Log log = null;
	
	public Crawler(String workerId, Buffer<Record> source)
	{
		super(workerId, source);
		log = LogFactory.getInstance().getLog("http.log");
	}

	@Override
	public void move(List<Buffer<Record>> sources, List<Buffer<Utility>> targets)
			throws InterruptedException
	{
		Record record = sources.get(0).pop();
		int id = record.getId();
		String url = record.getUrl();
		String fileName = record.getDirName() + id;
		try
		{
			String html = HttpFactory.getInstance().getPageHtml(url);
			int retryNum = 3;
			int sleepTim = 1800*1000;
			int i = 0;
			for (i=0; i <retryNum;i++)
			{
				if (!(html.contains("<form action=\"http://verify.baidu.com/verify\">")&&html.contains("<img src=\"http://verify.baidu.com/cgi-bin/genimg")))
				{
					break;
				} 
				System.out.println("verify" + (i+1));
				Thread.sleep(sleepTim);
				if(i == (retryNum-1)) {
					System.out.println("it is a verify page!");
					return;
				}
				html = HttpFactory.getInstance().getPageHtml(url);
			}
			if (i == retryNum)
			{
				System.out.println( "page " + id + "  " + url + "  it is a verify page!");
                //modify by zehua
                DBA dba = DBAFactory.getInstance().getDBA();
                Connection conn = null;
                try
                {
                    conn = dba.getConnection();
                    String sql = "update Baike set status = -1 where id = ?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setInt(1, id);
                    int rs = pst.executeUpdate();

                    pst.close();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                //
				return;
			}
		
			BufferedWriter writer = OutputFactory.getBufferedWriter(fileName, 1024*1024);
			writer.write(html);
			writer.close();
			System.out.println("crawled url: " + url);
			Thread.sleep(3*1000);
		} catch (ClientProtocolException e)
		{
			log.exception(e);
		} catch (IOException e)
		{
			log.exception(e);
		}
	}

}
