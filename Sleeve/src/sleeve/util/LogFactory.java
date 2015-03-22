package sleeve.util;

import java.util.HashMap;
import java.util.Map;

public class LogFactory
{
	private static LogFactory instance = null;
	private Map<String, Log> logMap = new HashMap<String, Log>();

	private LogFactory()
	{
		String logs[] = ConfigFactory.getInstance().get("logs").split(",");
		for (String log : logs)
		{
			logMap.put(log, new Log(log));
		}
	}

	public static LogFactory getInstance()
	{
		if (instance == null)
		{
			instance = new LogFactory();
		}
		return instance;
	}

	public Log getLog(String logName)
	{
		return logMap.get(logName);
	}
}
