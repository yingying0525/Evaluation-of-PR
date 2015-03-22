package main;

import sleeve.device.Utility;

public class Record extends Utility
{
	private int id = 0;
	private String url = null;
	private String dirName = null;

	public void setId(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}

	public void setDirName(String dirName)
	{
		this.dirName = dirName;
	}
	
	public String getDirName()
	{
		return dirName;
	}
	
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	public String getUrl()
	{
		return url;
	}

	
}
