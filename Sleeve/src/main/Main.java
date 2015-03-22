package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import sleeve.Factory;
import sleeve.Pipeline;
import sleeve.device.Buffer;
import sleeve.util.DBA;
import sleeve.util.DBAFactory;
import sleeve.util.OutputFactory;

public class Main
{
	public static void main(String[] args)
	{
		int threadNum = Integer.parseInt(args[0]);
		int dirNum = 500;
		int start = Integer.parseInt(args[1]);
		int end = Integer.parseInt(args[2]);
		List<String> dirs = new ArrayList<String>();
		String parentDir = args[3];
		// the directories to be used
		for (int i = 0; i < dirNum; ++i)
		{
			String dirName = parentDir + i + "/";
			dirs.add(dirName);
			if (!OutputFactory.exists(dirName))
			{
				OutputFactory.mkdir(dirName);
			}
		}
		
		// build and init the threads
		Buffer<Record> recordBuffer = new Buffer<Record>(0, 1000);
		Pipeline pipeline = new Pipeline("url pipeline");
		pipeline.addWorker(new Crawler("crawler", recordBuffer), threadNum);
		Factory.getInstance().loadPipeline(pipeline);
		
		// fetch the urls from database
		DBA dba = DBAFactory.getInstance().getDBA();
		Connection conn = null;
		long time = System.currentTimeMillis();
		try
		{
			conn = dba.getConnection();
		
			String sql = "select distinct(id), url from Baike where id between ? and ?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, start);
			pst.setInt(2, end);
			ResultSet rs = pst.executeQuery();
			while (rs.next())
			{
				Record record = new Record();
				record.setId(rs.getInt(1));
				record.setUrl(rs.getString(2));
				record.setDirName(dirs.get(record.getId()%dirNum));
				recordBuffer.push(record);
			}
			recordBuffer.setReadyToExhaust(true);
			rs.close();
			
			pst.close();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (SQLException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
					conn.close();
				
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		time = System.currentTimeMillis() - time;
		//System.out.println("cost " + time/1000.0 + " seconds.");
	}
}
