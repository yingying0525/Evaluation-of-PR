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
 * extractPeople 从百度百科中把"词条标签"是人物的挑出来，如果这个页面是一个list页面，把里面的url提取出来
 * extractKeywordsElement 从百度百科中把keyword的content提取出来，这个函数现在可能没用了，作为存档吧
 */
public class HtmlParser
{

    public static void main(String[] args)
    {
        String FileDir = args[0];
        String DataDir = args[1];
        //extractKeywordsElement(FileDir, DataDir);
        extractPeople(FileDir, DataDir);
    }

    public static void extractKeywordsElement(String FileDir, String DataDir)
    {
        int countNoKey = 0;
        int countNoContent = 0;
        int countNoBaseInfo = 0;

        try
        {
            BufferedWriter bwV = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "verify", true)));
            BufferedWriter bwP = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "peopleName", true)));
            BufferedWriter bwN = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "NoPage", true)));
            BufferedWriter bwNew = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "NewPage", true)));
            BufferedWriter bwO = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "OtherInfo", true)));
            BufferedWriter bwKey = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "Keywords", true)));

            File fileDir = new File(DataDir);
            String []dir_sec = fileDir.list();
            int i;
            for (i = 0; i < 500; i ++)
            {
                String d = i + "";
                System.out.println(d);
                File fileDir_sec = new File(DataDir + File.separator + d);
                String[] htmls = fileDir_sec.list();
                for (String html : htmls)
                {
                    File file = new File(DataDir + File.separator + d + File.separator + html);

                    Document Doc = Jsoup.parse(file,"UTF-8");
                    //404 notfound
                    if (Doc.html().contains("<form action=\"http://verify.baidu.com/verify\">") && Doc.html().contains("<img src=\"http://verify.baidu.com/cgi-bin/genimg"))
                    {
                        bwV.write(html);
                        bwV.newLine();
                        continue;
                    }
                    //no such page
                    if (Doc.title().equals("百度百科_全球最大中文百科全书"))
                    {
                        bwN.write(html);
                        bwN.newLine();
                        continue;
                    }

                    Elements elements = Doc.getElementsByAttributeValue("name", "Keywords");
                    if (elements.size() > 0)
                    {
                        Element e = elements.get(0);
                        String keywords = e.attr("content");
                        //System.out.println(keywords);
                        //List page
                        if (keywords.endsWith("baike "))
                        {
                            Element list = Doc.getElementById("lemma-list");
                            if (list == null)
                            {
                                //System.out.println("break!!");
                                continue;
                            }
                            Element ul = list.getElementsByTag("ul").get(0);

                            Elements as = ul.getElementsByTag("a");
                            for (Element a : as)
                            {
                                String t = a.text();
                                String href = a.attr("href");
                                bwNew.write(html + "\t" + t + "\t" + href);
                                bwNew.newLine();
                            }
                            continue;
                        }
                        Element baseInfoWrap = Doc.getElementById("baseInfoWrapDom");
                        String baseInfo = "";
                        if (baseInfoWrap == null)
                        {
                            bwO.write(html + "\t" + "NoBaseInfo");
                            bwO.newLine();
                            countNoBaseInfo ++;
                        }
                        else
                        {
                            StringBuilder sb = new StringBuilder();
                            Elements baseInfoItems = baseInfoWrap.getElementsByClass("biTitle");
                            for (Element item : baseInfoItems)
                            {
                                sb.append(item.text() + ",");
                            }
                            baseInfo = sb.toString();
                        }

                        Element Con = Doc.getElementById("lemmaContent-0");
                        String content = "";
                        if (Con == null)
                        {
                            bwO.write(html + "\t" + "NoContent");
                            bwO.newLine();
                            countNoContent ++;
                        }
                        else
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
                            else
                            {
                                bwO.write(html + "\t" + "NoContent_headline");
                                bwO.newLine();
                                countNoContent ++;
                            }
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

                        bwKey.write(html + " !##! " + title + " !##! " + keywords + " !##! " + content + " !##! " + baseInfo);
                        bwKey.newLine();
                    }
                    else
                    {
                        bwO.write(html + "\t" + "NoKeywords");
                        bwO.newLine();
                        countNoKey ++;
                    }
                }
            }
            bwV.close();
            bwP.close();
            bwN.close();
            bwNew.close();
            bwO.close();
            bwKey.close();
            System.out.println("NoBaseInfo: " + countNoBaseInfo);
            System.out.println("NoContent: " + countNoContent);
            System.out.println("NoKeywords: " + countNoKey);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void extractPeople(String FileDir, String DataDir)
    {
        File fileDir = new File(DataDir);
        int i;
        BufferedWriter bwV = null, bwNo = null, bwP = null, bwNew = null, bwO = null;
        try
        {
            bwV = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "verify", true)));
            bwNo = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "NoPage", true)));
            bwP = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "PeoplePage", true)));
            bwNew = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "NewPage", true)));
            bwO = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDir + File.separator + "OtherPage", true)));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        for (i = 0; i < 5; i++)
        {
            String d = i + "";
            System.out.println(d);
            File fileDir_sec = new File(DataDir + File.separator + d);
            String[] htmls = fileDir_sec.list();
            for (String html : htmls)
            {
                File file = new File(DataDir + File.separator + d + File.separator + html);
                try
                {
                    Document Doc = Jsoup.parse(file, "UTF-8");
                    //404 notfound
                    if (Doc.html().contains("<form action=\"http://verify.baidu.com/verify\">") && Doc.html().contains("<img src=\"http://verify.baidu.com/cgi-bin/genimg"))
                    {
                        bwV.write(html);
                        bwV.newLine();
                        continue;
                    }
                    //no such page
                    if (Doc.title().equals("百度百科——全球最大中文百科全书"))
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
                        else
                        {
                            bwO.write(html + "\t" + title);
                            bwO.newLine();
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
            bwO.close();
            bwP.close();
            bwV.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
