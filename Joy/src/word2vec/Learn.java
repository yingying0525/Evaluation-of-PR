package word2vec;

import love.cq.util.MapCount;
import word2vec.domain.HiddenNeuron;
import word2vec.domain.Neuron;
import word2vec.domain.WordNeuron;
import word2vec.utilIK.Haffman;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class Learn
{

    private Map<String, Neuron> wordMap = new HashMap<String, Neuron>();
    /**
     * 训练多少个特征
     */
    private int layerSize = 200;

    /**
     * 上下文窗口大小
     */
    private int window = 5;

    private double sample = 1e-3;
    private double alpha = 0.025;
    private double startingAlpha = alpha;

    public int EXP_TABLE_SIZE = 1000;

    private Boolean isCbow = false;

    private double[] expTable = new double[EXP_TABLE_SIZE];

    private int trainWordsCount = 0;

    private int MAX_EXP = 6;

    public Learn(Boolean isCbow, Integer layerSize, Integer window, Double alpha, Double sample) {
        createExpTable();
        if (isCbow != null) {
            this.isCbow = isCbow;
        }
        if (layerSize != null)
            this.layerSize = layerSize;
        if (window != null)
            this.window = window;
        if (alpha != null)
            this.alpha = alpha;
        if (sample != null)
            this.sample = sample;
    }

    public Learn() {
        createExpTable();
    }

    /**
     * trainModel
     * @throws IOException
     */
    private void trainModel(File file) throws IOException {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file)));
            String temp = null;
            long nextRandom = 5;
            int wordCount = 0;
            int lastWordCount = 0;
            int wordCountActual = 0;
            while ((temp = br.readLine()) != null) {
                if (wordCount - lastWordCount > 10000) {
                    System.out
                            .println("alpha:" + alpha + "\tProgress: "
                                    + (int) (wordCountActual / (double) (trainWordsCount + 1) * 100)
                                    + "%");
                    wordCountActual += wordCount - lastWordCount;
                    lastWordCount = wordCount;
                    alpha = startingAlpha * (1 - wordCountActual / (double) (trainWordsCount + 1));
                    if (alpha < startingAlpha * 0.0001) {
                        alpha = startingAlpha * 0.0001;
                    }
                }
                String[] strs = temp.split(" ");
                wordCount += strs.length;
                List<WordNeuron> sentence = new ArrayList<WordNeuron>();
                for (int i = 0; i < strs.length; i++) {
                    Neuron entry = wordMap.get(strs[i]);
                    if (entry == null) {
                        continue;
                    }
                    // The subsampling randomly discards frequent words while keeping the ranking same
                    if (sample > 0) {
                        double ran = (Math.sqrt(entry.freq / (sample * trainWordsCount)) + 1)
                                * (sample * trainWordsCount) / entry.freq;
                        nextRandom = nextRandom * 25214903917L + 11;
                        if (ran < (nextRandom & 0xFFFF) / (double) 65536) {
                            continue;
                        }
                    }
                    sentence.add((WordNeuron) entry);
                }

                for (int index = 0; index < sentence.size(); index++) {
                    nextRandom = nextRandom * 25214903917L + 11;
                    if (isCbow) {
                        cbowGram(index, sentence, (int) nextRandom % window);
                    } else {
                        skipGram(index, sentence, (int) nextRandom % window);
                    }
                }

            }
            System.out.println("Vocab size: " + wordMap.size());
            System.out.println("Words in train file: " + trainWordsCount);
            System.out.println("sucess train over!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * skip gram 模型训练
     * @param sentence
     */
    private void skipGram(int index, List<WordNeuron> sentence, int b) {
        // TODO Auto-generated method stub
        WordNeuron word = sentence.get(index);
        int a, c = 0;
        for (a = b; a < window * 2 + 1 - b; a++) {
            if (a == window) {
                continue;
            }
            c = index - window + a;
            if (c < 0 || c >= sentence.size()) {
                continue;
            }

            double[] neu1e = new double[layerSize];//误差项
            //HIERARCHICAL SOFTMAX
            List<Neuron> neurons = word.neurons;
            WordNeuron we = sentence.get(c);
            for (int i = 0; i < neurons.size(); i++) {
                HiddenNeuron out = (HiddenNeuron) neurons.get(i);
                double f = 0;
                // Propagate hidden -> output
                for (int j = 0; j < layerSize; j++) {
                    f += we.syn0[j] * out.syn1[j];
                }
                if (f <= -MAX_EXP || f >= MAX_EXP) {
                    continue;
                } else {
                    f = (f + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2);
                    f = expTable[(int) f];
                }
                // 'g' is the gradient multiplied by the learning rate
                double g = (1 - word.codeArr[i] - f) * alpha;
                // Propagate errors output -> hidden
                for (c = 0; c < layerSize; c++) {
                    neu1e[c] += g * out.syn1[c];
                }
                // Learn weights hidden -> output
                for (c = 0; c < layerSize; c++) {
                    out.syn1[c] += g * we.syn0[c];
                }
            }

            // Learn weights input -> hidden
            for (int j = 0; j < layerSize; j++) {
                we.syn0[j] += neu1e[j];
            }
        }

    }

    /**
     * 词袋模型
     * @param index
     * @param sentence
     * @param b
     */
    private void cbowGram(int index, List<WordNeuron> sentence, int b) {
        WordNeuron word = sentence.get(index);
        int a, c = 0;

        List<Neuron> neurons = word.neurons;
        double[] neu1e = new double[layerSize];//误差项
        double[] neu1 = new double[layerSize];//误差项
        WordNeuron last_word;

        for (a = b; a < window * 2 + 1 - b; a++)
            if (a != window) {
                c = index - window + a;
                if (c < 0)
                    continue;
                if (c >= sentence.size())
                    continue;
                last_word = sentence.get(c);
                if (last_word == null)
                    continue;
                for (c = 0; c < layerSize; c++)
                    neu1[c] += last_word.syn0[c];
            }

        //HIERARCHICAL SOFTMAX
        for (int d = 0; d < neurons.size(); d++) {
            HiddenNeuron out = (HiddenNeuron) neurons.get(d);
            double f = 0;
            // Propagate hidden -> output
            for (c = 0; c < layerSize; c++)
                f += neu1[c] * out.syn1[c];
            if (f <= -MAX_EXP)
                continue;
            else if (f >= MAX_EXP)
                continue;
            else
                f = expTable[(int) ((f + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2))];
            // 'g' is the gradient multiplied by the learning rate
            //            double g = (1 - word.codeArr[d] - f) * alpha;
            //              double g = f*(1-f)*( word.codeArr[i] - f) * alpha;
            double g = f * (1 - f) * (word.codeArr[d] - f) * alpha;
            //
            for (c = 0; c < layerSize; c++) {
                neu1e[c] += g * out.syn1[c];
            }
            // Learn weights hidden -> output
            for (c = 0; c < layerSize; c++) {
                out.syn1[c] += g * neu1[c];
            }
        }
        for (a = b; a < window * 2 + 1 - b; a++) {
            if (a != window) {
                c = index - window + a;
                if (c < 0)
                    continue;
                if (c >= sentence.size())
                    continue;
                last_word = sentence.get(c);
                if (last_word == null)
                    continue;
                for (c = 0; c < layerSize; c++)
                    last_word.syn0[c] += neu1e[c];
            }

        }
    }

    /**
     * 统计词频
     * @param file
     * @throws IOException
     */
    private void readVocab(File file) throws IOException {
        MapCount<String> mc = new MapCount<String>();
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file)));
            String temp = null;
            while ((temp = br.readLine()) != null) {
                String[] split = temp.split(" ");
                trainWordsCount += split.length;
                for (String string : split) {
                    mc.add(string);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        for (Entry<String, Integer> element : mc.get().entrySet()) {
            wordMap.put(element.getKey(), new WordNeuron(element.getKey(), element.getValue(),
                    layerSize));
        }
    }

    /**
     * Precompute the exp() table
     * f(x) = x / (x + 1)
     */
    private void createExpTable() {
        for (int i = 0; i < EXP_TABLE_SIZE; i++) {
            expTable[i] = Math.exp(((i / (double) EXP_TABLE_SIZE * 2 - 1) * MAX_EXP));
            expTable[i] = expTable[i] / (expTable[i] + 1);
        }
    }

    /**
     * 根据文件学习
     * @param file
     * @throws IOException
     */
    public void learnFile(File file) throws IOException {
        readVocab(file);
        new Haffman(layerSize).make(wordMap.values());

        //查找每个神经元
        for (Neuron neuron : wordMap.values()) {
            ((WordNeuron)neuron).makeNeurons() ;
        }

        trainModel(file);
    }

    /**
     * 保存模型
     */
    public void saveModel(File file) {
        // TODO Auto-generated method stub

        try {
            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(file)));
            dataOutputStream.writeInt(wordMap.size());
            dataOutputStream.writeInt(layerSize);
            double[] syn0 = null;
            for (Entry<String, Neuron> element : wordMap.entrySet()) {
                dataOutputStream.writeUTF(element.getKey());
                syn0 = ((WordNeuron) element.getValue()).syn0;
                for (double d : syn0) {
                    dataOutputStream.writeFloat(((Double) d).floatValue());
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int getLayerSize() {
        return layerSize;
    }

    public void setLayerSize(int layerSize) {
        this.layerSize = layerSize;
    }

    public int getWindow() {
        return window;
    }

    public void setWindow(int window) {
        this.window = window;
    }

    public double getSample() {
        return sample;
    }

    public void setSample(double sample) {
        this.sample = sample;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
        this.startingAlpha = alpha;
    }

    public Boolean getIsCbow() {
        return isCbow;
    }

    public void setIsCbow(Boolean isCbow) {
        this.isCbow = isCbow;
    }
    
    //@para start end directory
    //eg. 1 2013-06-01 2013-07-31 datadic (一个文件)
    //eg. 2 filename datadic (训练一个文件成为一个文件)
    //eg. 3 datadic (一个文件夹中的所有文件)
    public static void main(String[] args) throws IOException
    {
        String flag = args[0];

        if (flag.equals("1"))
        {
            String start = args[1];
            String end = args[2];
            String dir = args[3];
            String dataFile = dir + File.separator + "data_" + start + "--" + end;
            if (!((new File(dataFile)).exists()))
            {
                FileOutputStream fos = new FileOutputStream(new File(dataFile));
                DateFormat df = new SimpleDateFormat("yyyy-MM");
                try
                {
                    Date startDate = df.parse(start);
                    Date endDate = df.parse(end);
                    Calendar startCal = Calendar.getInstance();
                    Calendar endCal = Calendar.getInstance();
                    startCal.setTime(startDate);
                    endCal.setTime(endDate);
                    startCal.set(Calendar.DATE, 1);
                    endCal.set(Calendar.DATE, 1);
                    for (; startCal.compareTo(endCal) <= 0; startCal.add(Calendar.MONTH, 1))
                    {
                        String inputFile = "segment_" + df.format(startCal.getTime());
                        System.out.println("Moving " + inputFile);
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(new FileInputStream(new File(dir + File.separator + inputFile))));
                        String temp = null;
                        while ((temp = br.readLine()) != null)
                        {
                            fos.write(temp.getBytes());
                            fos.write("\n".getBytes());
                        }
                        br.close();
                    }
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
                fos.close();
            }
            System.out.println("Start Learning...");
            Learn learn = new Learn();
            long startTime = System.currentTimeMillis();
            learn.learnFile(new File(dataFile));
            System.out.println("use time " + (System.currentTimeMillis() - startTime));
            learn.saveModel(new File(dir + File.separator + "vector_" + start + "--" + end));
        }
        else
        if (flag.equals("2"))
        {
            String fileName = args[1];
            String dir = args[2];

            String dataFile = dir + File.separator + "segment_" + fileName;
            if (!((new File(dataFile)).exists()))
            {
                System.out.println(dataFile + " do not exist!");
            }
            else
            {
                System.out.println("Start Learning...");
                Learn learn = new Learn();
                long startTime = System.currentTimeMillis();
                learn.learnFile(new File(dataFile));
                System.out.println("use time " + (System.currentTimeMillis() - startTime));
                learn.saveModel(new File(dir + File.separator + "vector_" + fileName));
            }
        }
        else
        if (flag.equals("3"))
        {
            String dir = args[1];
            File fileDir = new File(dir);
            String []files = fileDir.list();
            for (String file : files)
            {
                System.out.println("Start Learning...");
                Learn learn = new Learn();
                long startTime = System.currentTimeMillis();
                learn.learnFile(new File(dir + File.separator + file));
                String temp = (file.split("_"))[1];
                System.out.println("use time " + (System.currentTimeMillis() - startTime));
                learn.saveModel(new File(dir + File.separator + "vector_" + temp));
            }
        }
    }
}
