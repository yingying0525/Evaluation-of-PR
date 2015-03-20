package word2vec;

import java.io.File;

/**
 * Created by hp on 2014/11/18.
 */
public class W2VComp
{
    public static void main(String[] args)
    {
        String dir = args[0];
        String vectorFile = dir + File.separator + args[1];
        //String vectorFile = args[0];
        String []strs= args[1].split("_");
        String a = strs[1];
        //String [] strss = a.split("\\.");
        System.out.println(a);
        Word2VEC vec = new Word2VEC();
        //vec.loadJavaModel("vector_2013-04--2013-05.mod");
        vec.loadJavaModel(vectorFile);
        String str1 = args[2];
        String str2 = args[3];

        float dist = vec.compareTwoWords(str1,str2);
        System.out.println(dist);
    }
}
