package word2vec;

import word2vec.domain.WordEntry;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

//import java.util.Arrays;
//import org.ansj.util.MatrixUtil;

public class Word2VEC
{

    //@para dir vectorFile word wordFileName
    //eg. 1 datadic vector_2013-04--2013-06 姚明 yaoming (一个文件)
    //eg. 2 datadic 2013-04 2013-05 姚明 yaoming (每月一个文件)
    public static void main(String[] args) throws IOException {
        String flag = args[0];
        if (flag.equals("1"))
        {
            String dir = args[1];
            String vectorFile = dir + File.separator + args[2];
            //String vectorFile = args[0];
            String []strs= args[2].split("_");
            String a = strs[1];
            //String [] strss = a.split("\\.");
            System.out.println(a);
            Word2VEC vec = new Word2VEC();
            //vec.loadJavaModel("vector_2013-04--2013-05.mod");
            vec.loadJavaModel(vectorFile);

            // System.out.println("中国" + "\t" +
            // Arrays.toString(vec.getWordVector("中国")));
            // ;
            // System.out.println("毛泽东" + "\t" +
            // Arrays.toString(vec.getWordVector("毛泽东")));
            // ;
            // System.out.println("足球" + "\t" +
            // Arrays.toString(vec.getWordVector("足球")));

            // Word2VEC vec2 = new Word2VEC();
            // vec2.loadGoogleModel("library/vectors.bin") ;
            //
            //

            //String str = "姚明";
            String str = args[3];
            String strName = args[4];

            String outFile = dir + File.separator + strName;

            //String outFile = str + "_" + strs[1];
            //String outFile = str;
            FileWriter fos = new FileWriter(new File(outFile), true);
            long start = System.currentTimeMillis();
            fos.write(a);
            fos.write("\n");
            fos.write(vec.distance(str).toString());
            fos.write("\n");

            fos.close();
            System.out.println(System.currentTimeMillis() - start);
            // System.out.println(vec2.distance(str));
            //
            //
            // //男人 国王 女人
            // System.out.println(vec.analogy("邓小平", "毛泽东思想", "毛泽东"));
            // System.out.println(vec2.analogy("毛泽东", "毛泽东思想", "邓小平"));
        }
        else
        if (flag.equals("2"))
        {
            String dir = args[1];
            String start = args[2];
            String end = args[3];
            String word = args[4];
            String fileName = args[5];
            DateFormat df = new SimpleDateFormat("yyyy-MM");
            Calendar startCal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();
            String outFile = dir + File.separator + fileName;
            FileWriter fos = new FileWriter(new File(outFile), true);
            try
            {
                startCal.setTime(df.parse(start));
                endCal.setTime(df.parse(end));
                startCal.set(Calendar.DATE, 1);
                endCal.set(Calendar.DATE, 1);
                for (; startCal.compareTo(endCal) <= 0; startCal.add(Calendar.MONTH, 1))
                {
                    String time = df.format(startCal.getTime());
                    String vectorFile = dir + File.separator + "vector_" + time + "--" + time;
                    Word2VEC vec = new Word2VEC();
                    vec.loadJavaModel(vectorFile);

                    fos.write(time);
                    fos.write("\n");
                    fos.write(vec.distance(word).toString());
                    fos.write("\n");
                }
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
            fos.close();
        }
    }

    private HashMap<String, float[]> wordMap = new HashMap<String, float[]>();
    //private HashMap<String, String> wordNat = new HashMap<String, String>();

    private int words;
    private int size;
    private int topNSize = 500;
    private int i;
    private int j;

    /**
     * 加载模型
     *
     * @param path
     *            模型的路径
     * @throws IOException
     */
    public void loadGoogleModel(String path) throws IOException {
        DataInputStream dis = null;
        BufferedInputStream bis = null;
        double len = 0;
        float vector = 0;
        try {
            bis = new BufferedInputStream(new FileInputStream(path));
            dis = new DataInputStream(bis);
            // //读取词数
            words = Integer.parseInt(readString(dis));
            // //大小
            size = Integer.parseInt(readString(dis));
            String word;
            float[] vectors = null;
            for (int i = 0; i < words; i++) {
                word = readString(dis);
                vectors = new float[size];
                len = 0;
                for (int j = 0; j < size; j++) {
                    vector = readFloat(dis);
                    len += vector * vector;
                    vectors[j] = (float) vector;
                }
                len = Math.sqrt(len);

                for (int j = 0; j < size; j++) {
                    vectors[j] /= len;
                }

                wordMap.put(word, vectors);
                dis.read();
            }
        } finally {
            bis.close();
            dis.close();
        }
    }

    /**
     * 加载模型
     *
     * @param path
     *            模型的路径
     * @throws IOException
     */
    public void loadJavaModel(String path) {
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
            words = dis.readInt();
            size = dis.readInt();
            System.out.println("words = " + words + "    size = "+ size);

            float vector = 0;
            String key = null;
            float[] value = null;
            for (i = 0; i < words; i++) {
                try
                {
                    double len = 0;
                    key = dis.readUTF();
                    value = new float[size];

                    for (j = 0; j < size; j++) {
                        vector = dis.readFloat();
                        len += vector * vector;
                        value[j] = vector;
                    }

                    len = Math.sqrt(len);
                    for (int j = 0; j < size; j++) {
                        value[j] /= len;
                    }
                    wordMap.put(key, value);
                }catch (EOFException eof)
                {
                    System.out.println("EOF Exception");
                }
            }

            System.out.println(wordMap.size());

        }catch (Exception e){
            System.out.println("words-i = " + i);
            System.out.println("size-j = " + j);
            e.printStackTrace();

        }
    }

    private static final int MAX_SIZE = 50;

    /**
     * 近义词
     *
     * @return
     */
    public TreeSet<WordEntry> analogy(String word0, String word1, String word2) {
        float[] wv0 = getWordVector(word0);
        float[] wv1 = getWordVector(word1);
        float[] wv2 = getWordVector(word2);

        if (wv1 == null || wv2 == null || wv0 == null) {
            return null;
        }
        float[] wordVector = new float[size];
        for (int i = 0; i < size; i++) {
            wordVector[i] = wv1[i] - wv0[i] + wv2[i];
        }
        float[] tempVector;
        String name;
        List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
        for (Entry<String, float[]> entry : wordMap.entrySet()) {
            name = entry.getKey();
            if (name.equals(word0) || name.equals(word1) || name.equals(word2)) {
                continue;
            }
            float dist = 0;
            tempVector = entry.getValue();
            for (int i = 0; i < wordVector.length; i++) {
                dist += wordVector[i] * tempVector[i];
            }
            insertTopN(name, dist, wordEntrys);
        }
        return new TreeSet<WordEntry>(wordEntrys);
    }

    private void insertTopN(String name, float score, List<WordEntry> wordsEntrys) {
        // TODO Auto-generated method stub
        if (wordsEntrys.size() < topNSize) {
            wordsEntrys.add(new WordEntry(name,score));
            return;
        }
        float min = Float.MAX_VALUE;
        int minOffe = 0;
        for (int i = 0; i < topNSize; i++) {
            WordEntry wordEntry = wordsEntrys.get(i);
            if (min > wordEntry.score) {
                min = wordEntry.score;
                minOffe = i;
            }
        }

        if (score > min) {
            wordsEntrys.set(minOffe, new WordEntry(name,score));
        }

    }
    //modified by zehua
    public float compareTwoWords(String query1, String query2)
    {
        float[] value1 = wordMap.get(query1);
        float[] value2 = wordMap.get(query2);
        if(value1 == null || value2 == null)
            return 0;
        float dist = 0;
        for (int i = 0; i < value2.length; i ++)
        {
            dist += value1[i] * value2[i];
        }
        return dist;
    }
    public Set<WordEntry> distance(String queryWord) {

        float[] center = wordMap.get(queryWord);
        if (center == null) {
            return Collections.emptySet();
        }

        int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
        TreeSet<WordEntry> result = new TreeSet<WordEntry>();

        double min = Float.MIN_VALUE;
        for (Map.Entry<String, float[]> entry : wordMap.entrySet()) {
            float[] vector = entry.getValue();
            float dist = 0;
            for (int i = 0; i < vector.length; i++) {
                dist += center[i] * vector[i];
            }

            if (dist > min) {
                result.add(new WordEntry(entry.getKey(), dist));
                if (resultSize < result.size()) {
                    result.pollLast();
                }
                min = result.last().score;
            }
        }
        result.pollFirst();

        return result;
    }

    public Set<WordEntry> distance(List<String> words) {

        float[] center = null;
        for (String word : words) {
            center = sum(center, wordMap.get(word));
        }

        if (center == null) {
            return Collections.emptySet();
        }

        int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
        TreeSet<WordEntry> result = new TreeSet<WordEntry>();

        double min = Float.MIN_VALUE;
        for (Map.Entry<String, float[]> entry : wordMap.entrySet()) {
            float[] vector = entry.getValue();
            float dist = 0;
            for (int i = 0; i < vector.length; i++) {
                dist += center[i] * vector[i];
            }

            if (dist > min) {
                result.add(new WordEntry(entry.getKey(), dist));
                if (resultSize < result.size()) {
                    result.pollLast();
                }
                min = result.last().score;
            }
        }
        result.pollFirst();

        return result;
    }

    private float[] sum(float[] center, float[] fs) {
        // TODO Auto-generated method stub

        if (center == null && fs == null) {
            return null;
        }

        if (fs == null) {
            return center;
        }

        if (center == null) {
            return fs;
        }

        for (int i = 0; i < fs.length; i++) {
            center[i] += fs[i];
        }

        return center;
    }

    /**
     * 得到词向量
     *
     * @param word
     * @return
     */
    public float[] getWordVector(String word) {
        return wordMap.get(word);
    }

    public static float readFloat(InputStream is) throws IOException {
        byte[] bytes = new byte[4];
        is.read(bytes);
        return getFloat(bytes);
    }

    /**
     * 读取一个float
     *
     * @param b
     * @return
     */
    public static float getFloat(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        return Float.intBitsToFloat(accum);
    }

    /**
     * 读取一个字符串
     *
     * @param dis
     * @return
     * @throws IOException
     */
    private static String readString(DataInputStream dis) throws IOException {
        // TODO Auto-generated method stub
        byte[] bytes = new byte[MAX_SIZE];
        byte b = dis.readByte();
        int i = -1;
        StringBuilder sb = new StringBuilder();
        while (b != 32 && b != 10) {
            i++;
            bytes[i] = b;
            b = dis.readByte();
            if (i == 49) {
                sb.append(new String(bytes));
                i = -1;
                bytes = new byte[MAX_SIZE];
            }
        }
        sb.append(new String(bytes, 0, i + 1));
        return sb.toString();
    }

    public int getTopNSize() {
        return topNSize;
    }

    public void setTopNSize(int topNSize) {
        this.topNSize = topNSize;
    }

    public HashMap<String, float[]> getWordMap() {
        return wordMap;
    }

    public int getWords() {
        return words;
    }

    public int getSize() {
        return size;
    }

}
