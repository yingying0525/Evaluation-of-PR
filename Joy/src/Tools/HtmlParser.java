package Tools;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2014/11/4.
 * extractPeople 从百度百科中把"词条标签"是人物的挑出来，如果这个页面是一个list页面，把里面的url提取出来,把keyword的content提取出来
 */
public class HtmlParser
{

    public static void main(String[] args)
    {
        String FileDir = args[0];
        String DataDir = args[1];
        extractPeople(FileDir, DataDir);
    }

    public static void extractPeople(String FileDir, String DataDir)
    {
        int i;
        BufferedWriter bwV = null, bwNo = null, bwP = null, bwNew = null, bwW = null, bwKey = null;
        try
        {
            bwV = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "verify", true)));
            bwNo = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "NoPage", true)));
            bwP = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "PeoplePage", true)));
            bwNew = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "NewPage", true)));
            bwW = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "WordPage", true)));
            bwKey = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "Keywords", true)));

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        for (i = 0; i < 500; i++)
        {
            String d = i + "";
            System.out.println(d);
            File fileDir_sec = new File(DataDir + File.separator + d);
            String[] htmls = fileDir_sec.list();
            for (String html : htmls)
            {
                File file = new File(DataDir + File.separator + d + File.separator + html);
                //System.out.println(html);
                try
                {
                    Document Doc = Jsoup.parse(file, "UTF-8");
                    if (file.length() == 0)
                    {
                        bwNo.write(html);
                        bwNo.newLine();
                        continue;
                    }
                    //404 notfound
                    if (Doc.html().contains("<form action=\"http://verify.baidu.com/verify\">") && Doc.html().contains("<img src=\"http://verify.baidu.com/cgi-bin/genimg"))
                    {
                        bwV.write(html);
                        bwV.newLine();
                        continue;
                    }
                    //no such page
                    if (Doc.title().equals("百度百科——全球最大中文百科全书") || Doc.title().equals("百度百科_全球最大中文百科全书"))
                    {
                        bwNo.write(html);
                        bwNo.newLine();
                        continue;
                    }
                    Elements h1 = Doc.getElementsByClass("lemmaTitleH1");
                    String title;
                    if (h1.size() > 0)
                        title = h1.get(0).text();
                    else
                    {
                        title = Doc.title();
                        title = title.substring(0, title.length() - 5);
                    }

                    bwW.write(html + " !##! " + title);
                    bwW.newLine();

                    Element tags = Doc.getElementById("open-tag-item");
                    List<String> text = new ArrayList<String>();
                    if (tags != null)
                    {
                        Elements tagList = tags.getElementsByClass("taglist");
                        if (tagList.size() > 0)
                        {
                            for (int j = 0; j < tagList.size(); j++)
                            {
                                text.add(tagList.get(j).text());
                            }
                            if (text.contains("人物"))
                            {
                                bwP.write(html + " !##! " + title);
                                bwP.newLine();

                                Elements elements = Doc.getElementsByAttributeValue("name", "Keywords");
                                if (elements.size() > 0)
                                {
                                    Element e = elements.get(0);
                                    String keywords = e.attr("content");
                                    /*Element baseInfoWrap = Doc.getElementById("baseInfoWrapDom");
                                    String baseInfo = "";
                                    if (baseInfoWrap != null)
                                    {
                                        StringBuilder sb = new StringBuilder();
                                        Elements baseInfoItems = baseInfoWrap.getElementsByClass("biTitle");
                                        for (Element item : baseInfoItems)
                                        {
                                            sb.append(item.text() + ",");
                                        }
                                        baseInfo = sb.toString();
                                    }*/
                                    Element Con = Doc.getElementById("lemmaContent-0");
                                    String content = "";
                                    if (Con != null)
                                    {
                                        StringBuilder sb = new StringBuilder();
                                        Elements headlines = Con.getElementsByClass("headline-content");
                                        if (headlines.size() > 0)
                                        {
                                            for (Element head : headlines)
                                            {
                                                sb.append(head.text() + ",");
                                            }
                                            content = sb.toString();
                                        }
                                    }
                                    bwKey.write(html + " !##! " + title + " !##! " + keywords + " !##! " + content);
                                    bwKey.newLine();
                                }
                            }
                        }
                    }
                    else
                    {
                        Element list = Doc.getElementById("lemma-list");
                        if (list != null)
                        {
                            Element ul = list.getElementsByTag("ul").get(0);
                            Elements as = ul.getElementsByTag("a");
                            for (Element a : as)
                            {
                                String t = a.text();
                                String href = a.attr("href");
                                bwNew.write(html + "\t" + t + "\t" + href);
                                bwNew.newLine();
                            }
                        }
                    }

                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        try
        {
            bwNew.close();
            bwNo.close();
            bwP.close();
            bwV.close();
            bwKey.close();
            bwW.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
