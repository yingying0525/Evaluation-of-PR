package Tools;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by hp on 2015/1/9.
 * 功能：把从网上载下来的图片进行切割
 * cutPic_1 和 cutPic_2 提供两种图片大小
 * 需要注意图片是png还是jpg的，try catch解决
 */
public class CutPicture
{
    public static void main(String[] args)
    {
        Map<String, Integer> map = ReadPeopleFileToMap("D:\\EventTeller\\EventTellerCode\\people");
        String inDir = "D:\\tempdata\\imageSec";
        String outDir = "D:\\tempdata\\imageTemp\\";
        File file = new File(inDir);
        for (File f: file.listFiles())
        {
            String name = f.getName();
            System.out.println(name);
            String []str = name.split("_");
            String a = str[1];
            String outName = (a.split("\\."))[0];
            int num = map.get(outName);
            File outFile = new File(outDir + num + ".png");
            if (str[0].equals("1"))
            {
                try
                {
                    cutPic_1(f, outFile);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                try
                {
                    cutPic_2(f, outFile);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void savePic(File infile, File outfile)throws Exception
    {
        FileInputStream is = null;
        // 读取图片文件
        is = new FileInputStream(infile);
        //得到图片的二进制数据，以二进制封装得到数据，具有通用性
        byte[] data = readInputStream(is);
        //new一个文件对象用来保存图片，默认保存当前工程根目录
        //File imageFile = new File("BeautyGirl.jpg");
        //创建输出流
        FileOutputStream outStream = new FileOutputStream(outfile);
        //写入数据
        outStream.write(data);
        //关闭输出流
        outStream.close();
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception
    {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 )
        {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    public static void cutPic_1(File srcfile, File outfile)throws Exception
    {
        int x = 400;
        int y = 0;
        int width = 350;
        int height = 350;
        cut_1(srcfile, outfile, x, y, width, height);
    }
    public static void cutPic_2(File srcfile, File outfile)throws Exception
    {
        int x = 0;
        int y = 0;
        int width = 200;
        int height = 200;
        cut_2(srcfile, outfile, x, y, width, height);
    }
    public static int cut(String urlstr, File outfile) throws IOException
    {
        int x = 300;
        int y = 0;
        int width = 500;
        int height = 450;

        URL url = new URL(urlstr);
        //打开链接
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置请求方式为"GET"
        conn.setRequestMethod("GET");
        //超时响应时间为5秒
        conn.setConnectTimeout(5 * 1000);
        //通过输入流获取图片数据
        InputStream inStream = conn.getInputStream();

        FileInputStream is = null;
        ImageInputStream iis = null;
        try {
			/*
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 声称能够解码指定格式。
			 * 参数：formatName - 包含非正式格式名称 .（例如 "jpeg" 或 "tiff"）等 。
			 */
            Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("png");
            ImageReader reader = it.next();
            // 获取图片流
            iis = ImageIO.createImageInputStream(inStream);

			/*
			 * <p>iis:读取源.true:只向前搜索 </p>.将它标记为 ‘只向前搜索’。
			 * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
			 */
            reader.setInput(iis, true);

			/*
			 * <p>描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O
			 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件 将从其 ImageReader 实现的
			 * getDefaultReadParam 方法中返回 ImageReadParam 的实例。
			 */
            ImageReadParam param = reader.getDefaultReadParam();

			/*
			 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
			 * 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。
			 */
            Rectangle rect = new Rectangle(x, y, width, height);

            // 提供一个 BufferedImage，将其用作解码像素数据的目标。
            param.setSourceRegion(rect);

			/*
			 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的
			 * BufferedImage 返回。
			 */

            BufferedImage bi = reader.read(0, param);

            // 保存新图片
            ImageIO.write(bi, "png", outfile);
            return 1;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
        finally {
            if (is != null) {
                is.close();
            }
            if (iis != null) {
                iis.close();
            }
        }

    }
    /**
     * 图片剪切工具方法
     *
     * @param srcfile 源图片
     * @param outfile 剪切之后的图片
     * @param x 剪切顶点 X 坐标
     * @param y 剪切顶点 Y 坐标
     * @param width 剪切区域宽度
     * @param height 剪切区域高度
     *
     * @throws java.io.IOException
     * @author cevencheng
     */
    public static void cut_1(File srcfile, File outfile, int x, int y, int width, int height)
    {
        FileInputStream is = null;
        ImageInputStream iis = null;
        try
        {
            // 读取图片文件
            is = new FileInputStream(srcfile);

			/*
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 声称能够解码指定格式。
			 * 参数：formatName - 包含非正式格式名称 .（例如 "jpeg" 或 "tiff"）等 。
			 */
            Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpg");
            ImageReader reader = it.next();
            // 获取图片流
            iis = ImageIO.createImageInputStream(is);

			/*
			 * <p>iis:读取源.true:只向前搜索 </p>.将它标记为 ‘只向前搜索’。
			 * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
			 */
            reader.setInput(iis, true);

			/*
			 * <p>描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O
			 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件 将从其 ImageReader 实现的
			 * getDefaultReadParam 方法中返回 ImageReadParam 的实例。
			 */
            ImageReadParam param = reader.getDefaultReadParam();

			/*
			 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
			 * 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。
			 */
            Rectangle rect = new Rectangle(x, y, width, height);

            // 提供一个 BufferedImage，将其用作解码像素数据的目标。
            param.setSourceRegion(rect);

			/*
			 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的
			 * BufferedImage 返回。
			 */
            BufferedImage bi = reader.read(0, param);
            //reader.read(0);

            // 保存新图片
            ImageIO.write(bi, "jpg", outfile);
        } catch (Exception e)
        {
            is = null;
            iis = null;
            try
            {
                // 读取图片文件
                is = new FileInputStream(srcfile);

			/*
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 声称能够解码指定格式。
			 * 参数：formatName - 包含非正式格式名称 .（例如 "jpeg" 或 "tiff"）等 。
			 */
                Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("png");
                ImageReader reader = it.next();
                // 获取图片流
                iis = ImageIO.createImageInputStream(is);

			/*
			 * <p>iis:读取源.true:只向前搜索 </p>.将它标记为 ‘只向前搜索’。
			 * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
			 */
                reader.setInput(iis, true);

			/*
			 * <p>描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O
			 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件 将从其 ImageReader 实现的
			 * getDefaultReadParam 方法中返回 ImageReadParam 的实例。
			 */
                ImageReadParam param = reader.getDefaultReadParam();

			/*
			 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
			 * 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。
			 */
                Rectangle rect = new Rectangle(x, y, width, height);

                // 提供一个 BufferedImage，将其用作解码像素数据的目标。
                param.setSourceRegion(rect);

			/*
			 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的
			 * BufferedImage 返回。
			 */

                BufferedImage bi = reader.read(0, param);

                // 保存新图片
                ImageIO.write(bi, "png", outfile);
            } catch (Exception ee)
            {
            }
        /*finally {
            if (is != null) {
                is.close();
            }
            if (iis != null) {
                iis.close();
            }
        }*/
        }
    }
    public static void cut_2(File srcfile, File outfile, int x, int y, int width, int height)
    {
        FileInputStream is = null;
        ImageInputStream iis = null;
        try
        {
            // 读取图片文件
            is = new FileInputStream(srcfile);

			/*
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 声称能够解码指定格式。
			 * 参数：formatName - 包含非正式格式名称 .（例如 "jpeg" 或 "tiff"）等 。
			 */
            Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpg");
            ImageReader reader = it.next();
            // 获取图片流
            iis = ImageIO.createImageInputStream(is);

			/*
			 * <p>iis:读取源.true:只向前搜索 </p>.将它标记为 ‘只向前搜索’。
			 * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
			 */
            reader.setInput(iis, true);

			/*
			 * <p>描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O
			 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件 将从其 ImageReader 实现的
			 * getDefaultReadParam 方法中返回 ImageReadParam 的实例。
			 */
            ImageReadParam param = reader.getDefaultReadParam();

			/*
			 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
			 * 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。
			 */
            Rectangle rect = new Rectangle(x, y, width, height);

            // 提供一个 BufferedImage，将其用作解码像素数据的目标。
            param.setSourceRegion(rect);

			/*
			 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的
			 * BufferedImage 返回。
			 */
            BufferedImage bi = reader.read(0);
            int h = reader.getHeight(0);
            int w = reader.getWidth(0);
            if (h < w)
            {
                bi = bi.getSubimage(x,y,h,h);
            }
            else
            {
                bi = bi.getSubimage(x,y,w,w);
            }

            //reader.read(0);

            // 保存新图片
            ImageIO.write(bi, "jpg", outfile);
        } catch (Exception e)
        {
            is = null;
            iis = null;
            try
            {
                // 读取图片文件
                is = new FileInputStream(srcfile);

			/*
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 声称能够解码指定格式。
			 * 参数：formatName - 包含非正式格式名称 .（例如 "jpeg" 或 "tiff"）等 。
			 */
                Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("png");
                ImageReader reader = it.next();
                // 获取图片流
                iis = ImageIO.createImageInputStream(is);

			/*
			 * <p>iis:读取源.true:只向前搜索 </p>.将它标记为 ‘只向前搜索’。
			 * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
			 */
                reader.setInput(iis, true);

			/*
			 * <p>描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O
			 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件 将从其 ImageReader 实现的
			 * getDefaultReadParam 方法中返回 ImageReadParam 的实例。
			 */
                ImageReadParam param = reader.getDefaultReadParam();

			/*
			 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
			 * 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。
			 */
                Rectangle rect = new Rectangle(x, y, width, height);

                // 提供一个 BufferedImage，将其用作解码像素数据的目标。
                param.setSourceRegion(rect);

			/*
			 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的
			 * BufferedImage 返回。
			 */

                BufferedImage bi = reader.read(0);
                int h = reader.getHeight(0);
                int w = reader.getWidth(0);
                if (h < w)
                {
                    bi = bi.getSubimage(x,y,h,h);
                }
                else
                {
                    bi = bi.getSubimage(x,y,w,w);
                }

                // 保存新图片
                ImageIO.write(bi, "png", outfile);
            } catch (Exception ee)
            {
            }
        /*finally {
            if (is != null) {
                is.close();
            }
            if (iis != null) {
                iis.close();
            }
        }*/
        }
    }
    public static Map<String, Integer> ReadPeopleFileToMap(String file)
    {
        Map<String, Integer> map = new HashMap<String, Integer>();
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = br.readLine()) != null)
            {
                if (!map.containsKey(line))
                    map.put(line, map.size() + 1);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return map;
    }
}
