package sleeve.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigFactory
{
	private static ConfigFactory instance = null;
	private String path = null;
	private Map<String, String> itemMap = null;

	public static ConfigFactory getInstance()
	{
		if (instance == null)
		{
			instance = new ConfigFactory();
		}
		return instance;
	}

	private ConfigFactory()
	{
		path = System.getProperty("user.home");
		itemMap = new HashMap<String, String>();
		try
		{
			this.load(InputFactory.getReader(path + "/sleeve.properties", 1024));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void load(BufferedReader reader) throws IOException
	{
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			line = line.trim();
			while (line.length() <= 1 || line.charAt(0) == '#')
			{
				// 如果该行是注释或者空行
				if ((line = reader.readLine()) == null)
				{
					// 再读取一行，如果是文件末尾则跳出循环
					break;
				}
				line = line.trim();
			}
			// 如果没有读到文件末尾，那么该行符合要求
			// 过滤掉注释
			if (line != null)
			{
				line = line.split("[#]")[0].trim();
				String[] keyValue = line.split("[=]");
				if (keyValue.length == 1)
				{
					itemMap.put(keyValue[0].trim(), "");
				}
				else
				{
					itemMap.put(keyValue[0].trim(), keyValue[1].trim().replace("${home}", System.getProperty("user.home")));
				}
			}
			else
			{
				break;
			}
		}
		reader.close();
	}

	public String get(String itemName)
	{
		return this.itemMap.get(itemName);
	}
}