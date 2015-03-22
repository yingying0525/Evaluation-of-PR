package sleeve.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class InputFactory
{

	private InputFactory()
	{
	}

	public static InputStream getStream(String path) throws IOException
	{
		return new FileInputStream(path);
	}

	public static BufferedInputStream getBufferedStream(String path, int bufferSize) throws IOException
	{
		return new BufferedInputStream(new FileInputStream(path), bufferSize);
	}

	public static BufferedReader getReader(String path, int bufferSize) throws IOException
	{
		FileReader fileReader = new FileReader(path);
		BufferedReader reader = new BufferedReader(fileReader, bufferSize);
		return reader;
	}

	public static ArrayList<BufferedReader> getReaders(ArrayList<String> paths, int bufferSize) throws IOException
	{
		ArrayList<BufferedReader> readers = new ArrayList<BufferedReader>();
		for (String path : paths)
		{
			BufferedReader reader = null;
			FileReader fileReader = new FileReader(path);
			reader = new BufferedReader(fileReader, bufferSize);
			readers.add(reader);
		}
		return readers;
	}

	public static BufferedReader getBigReader(String path, int bufferSize) throws FileNotFoundException
	{
		File file = new File(path);
		FileInputStream fileInputStream = new FileInputStream(file);
		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
		InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream);
		return new BufferedReader(inputStreamReader, bufferSize);
	}
}
