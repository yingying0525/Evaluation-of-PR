package Tools.JsonUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

/**
 * Created by hp on 2014/3/28.
 */
public class JsonWrite
{
	public void writeToFile(JSONObject obj, String fileName)
	{
		PrintWriter pW = null;
		try
		{
			pW = new PrintWriter(new BufferedWriter(new FileWriter(new File(
					fileName))));
			String prettyJson = jsonFormatter(obj.toString());
			pW.println(prettyJson);
			pW.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
    public void writeToFile(JSONArray obj, String fileName)
    {
        PrintWriter pW = null;
        try
        {
            pW = new PrintWriter(new BufferedWriter(new FileWriter(new File(
                    fileName))));
            String prettyJson = jsonFormatter(obj.toString());
            pW.println(prettyJson);
            pW.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
	public String jsonFormatter(String uglyJSONString)
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(uglyJSONString);
		String prettyJsonString = gson.toJson(je);
		return prettyJsonString;
	}

}
