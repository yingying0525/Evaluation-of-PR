package sleeve.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class OutputFactory
{
	private OutputFactory()
	{
	}

	public static OutputStream getStream(String path) throws IOException
	{
		return new FileOutputStream(path);
	}

	public static BufferedOutputStream getBufferedStream(String path, int bufferSize) throws IOException
	{
		return new BufferedOutputStream(new FileOutputStream(path), bufferSize);
	}
	
	public static FileWriter getWriter(String path) throws IOException
	{
		return new FileWriter(path);
	}

	public static BufferedWriter getBufferedWriter(String path, int bufferSize) throws IOException
	{
		/*FileWriter fileWriter = new FileWriter(path);
		BufferedWriter writer = new BufferedWriter(fileWriter, bufferSize);
		return writer;*/
		return new BufferedWriter(new FileWriter(path), bufferSize);
	}

	public static boolean exists (String path)
	{
		File file = new File(path);
		return file.exists();
	}
	
	public static boolean isDirectory (String path)
	{
		File file = new File(path);
		return file.isDirectory();
	}
	
	public static boolean mkdir (String path)
	{
		File file = new File(path);
		boolean res = false;
		if (!file.exists())
		{
			file.mkdirs();
			res = true;
		}
		return res;
	}
	public static boolean createFile (String path) throws IOException
	{
		File file = new File(path);
		return file.createNewFile();
	}
	
	
}
