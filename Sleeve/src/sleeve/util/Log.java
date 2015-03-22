package sleeve.util;

import java.io.BufferedWriter;
import java.io.IOException;

public class Log
{
	private BufferedWriter writer = null;

	protected Log(String fileName)
	{
		try
		{
			// 将日志文件放在配置文件指定的位置
			writer = OutputFactory.getBufferedWriter(ConfigFactory.getInstance().get("log.dir") + fileName, 1024);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public synchronized void exception(String msg)
	{
		try
		{
			writer.write("[Exception:]" + msg + "\n");
			writer.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public synchronized void info(String msg)
	{
		try
		{
			writer.write("[INFO:]" + msg + "\n");
			writer.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public synchronized void exception(Exception e)
	{
		try
		{
			String msg = "[Exception Msg:]" + e.getMessage() + "\n";
			for (StackTraceElement element : e.getStackTrace())
			{
				msg += "[Exception Stk:]" + element.getClassName() + ":" + element.getMethodName() + ":"
						+ element.getLineNumber() + "\n";
			}
			writer.write(msg);
			writer.flush();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public synchronized void error(Error e)
	{
		try
		{
			String msg = "[Error Msg:]" + e.getMessage() + "\n";
			for (StackTraceElement element : e.getStackTrace())
			{
				msg += "[Error Stk:]" + element.getClassName() + ":" + element.getMethodName() + ":"
						+ element.getLineNumber() + "\n";
			}
			writer.write(msg);
			writer.flush();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
}
