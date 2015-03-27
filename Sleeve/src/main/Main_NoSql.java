package main;

import sleeve.Factory;
import sleeve.Pipeline;
import sleeve.device.Buffer;
import sleeve.util.OutputFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2015/3/24.
 * 爬取网页 不需要使用数据库
 * 1 给数字范围，直接爬取
 * 2 file中写着url，读入后再爬取
 */

public class Main_NoSql
{
    public static void main(String[] args)
    {
        int threadNum = Integer.parseInt(args[0]);
        int dirNum = 500;
        int start = Integer.parseInt(args[1]);
        int end = Integer.parseInt(args[2]);
        List<String> dirs = new ArrayList<String>();
        String parentDir = args[3];
        String flag = args[4];
        // the directories to be used
        for (int i = 0; i < dirNum; ++i)
        {
            String dirName = parentDir + "/" + i + "/";
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

        long time = System.currentTimeMillis();
        try
        {
            if (flag.equals("1"))
            {
                int count = start;
                while (count <= end)
                {
                    Record record = new Record();
                    record.setId(count);
                    record.setUrl("http://baike.baidu.com/view/" + count + ".htm");
                    record.setDirName(dirs.get(record.getId() % dirNum));
                    recordBuffer.push(record);
                    count++;
                }
                recordBuffer.setReadyToExhaust(true);
            }
            else
            if (flag.equals("2"))
            {
                String file = args[5];
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line = "";
                while((line = br.readLine()) != null)
                {
                    String []strs = line.split("\t");
                    String url = "http://baike.baidu.com" + strs[2];
                    String []ss = strs[2].split("/");
                    int id = Integer.parseInt(ss[3].replace(".htm",""));
                    Record record = new Record();
                    record.setId(id);
                    record.setUrl(url);
                    record.setDirName(dirs.get(record.getId() % dirNum));
                    recordBuffer.push(record);
                }
                recordBuffer.setReadyToExhaust(true);
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        time = System.currentTimeMillis() - time;
        System.out.println("cost " + time/1000.0 + " seconds.");
    }
}
