package lawdata;

import java.io.*;
import java.util.*;

/**
 * TF-IDF与余弦相似性的应用（一）：自动提取关键词
 */
public class TFIDF {
    public String allCountFilePath = "/home/chen/桌面/司法大数据/output/out_3/pageCountNew" ;
    //public String singleCountDirFilePath = "/home/chen/桌面/司法大数据/test";
    public String singleCountDirFilePath = "/home/chen/桌面/司法大数据/jieba_result2/jieba_txt";

    public String outputDirFileFilePath = "/home/chen/桌面/司法大数据/output/out_3/IF-IDF";
    public static Set<String> forbiddenWords;
    /**
     * 读取单个文件
     * 读取文本格式每行为（key, value）形式的文本文件,并存入一个 Map<String, String>中
     * 如果文本中有（, value）（key, ）这种形式则会过滤不会存入return的map中
     * @param inputPath 传入文件路径（文本格式每行为（key, value）形式）
     * @return Map<String, String>
     */
    public static Map<String, String> readFromSingleFile(String inputPath){
        try {
            Map<String, String> map = new HashMap<String, String>();
            // 获取该文件的输入流
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputPath));
            // The first way of reading the file
            System.out.println("按行读取文件！");
            String line = bufferedReader.readLine();
            while (line != null) {
                //适应与每行的格式为 “(key,value)”
                line = line.replace('(', ' ');
                line = line.replace(')', ' ');
                //line.trim();//去掉首尾空格
                line = line.replaceAll(" ", "");
                String[] wordList = line.split(",");
                if (wordList.length == 2) {
                    //过滤掉字符
                    if (!wordList[0].equals("") && !wordList[1].equals("")) {
                        //System.out.println(wordList[0] + " " + wordList[1]);
                        map.put(wordList[0], wordList[1]);
                    }
                }

                //适应与每行的格式为 “key value”
//                line.trim();//去掉首尾空格
//                String[] wordList = line.split(" ");
//                if (wordList.length == 2) {
//                    //过滤掉字符
//                    if (!wordList[0].equals("") && !wordList[1].equals("")) {
//                        //System.out.println(wordList[0] + " " + wordList[1]);
//                        map.put(wordList[0], wordList[1]);
//                    }
//                }

                //读取下一行
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            return map;
        }catch (Exception e){
            System.out.println("读取文件异常！");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取单个文件
     * 读取文本格式每行为"key value"形式的文本文件,并存入一个 Map<String, String>中
     * @param inputPath 传入文件路径（文本格式每行为（key, value）形式）
     * @return Map<String, String>
     */
    public static Map<String, String> readFromSingleFile_2(String inputPath){
        try {
            Map<String, String> map = new HashMap<String, String>();
            // 获取该文件的输入流
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputPath));
            // The first way of reading the file
            System.out.println("按行读取文件！");
            String line = bufferedReader.readLine();
            while (line != null) {
                //适应与每行的格式为 “(key,value)”
//                line = line.replace('(', ' ');
//                line = line.replace(')', ' ');
//                //line.trim();//去掉首尾空格
//                line = line.replaceAll(" ", "");
//                String[] wordList = line.split(",");
//                if (wordList.length == 2) {
//                    //过滤掉字符
//                    if (!wordList[0].equals("") && !wordList[1].equals("")) {
//                        //System.out.println(wordList[0] + " " + wordList[1]);
//                        map.put(wordList[0], wordList[1]);
//                    }
//                }

                //适应与每行的格式为 “key value”
//                line.trim();//去掉首尾空格
//                String[] wordList = line.split(" ");
//                if (wordList.length == 2) {
//                    //过滤掉字符
//                    if (!wordList[0].equals("") && !wordList[1].equals("")) {
//                        //System.out.println(wordList[0] + " " + wordList[1]);
//                        map.put(wordList[0], wordList[1]);
//                    }
//                }

                //适应每行的格式为 “property key value”
                String[] wordList = line.split(" ");
                if (wordList.length == 3) {
                    //停用词表，过滤掉字符
                    if (!forbiddenWords.contains(wordList[0])) {
                        //System.out.println(wordList[0] + " " + wordList[1]);
                        map.put(wordList[1], wordList[2]);
                    }
                }
                //读取下一行
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            return map;
        }catch (Exception e){
            System.out.println("读取文件异常！");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取某个文件夹下的所有文件,允许文件夹嵌套
     * @param filepath 传入文件的路径（或文件夹的路径）
     * @return Map<String, String>
     */
    public static Map<String, String> readfile(String filepath){
        try {
            File file = new File(filepath);
            if (!file.isDirectory()) {
                //单个文件
                System.out.println("文件");
                System.out.println("path=" + file.getPath());
                System.out.println("absolutepath=" + file.getAbsolutePath());
                System.out.println("name=" + file.getName());
                return readFromSingleFile(filepath);
            } else if (file.isDirectory()) {
                //文件夹
                System.out.println("文件夹");
                String[] filelist = file.list();
                //收集文件夹下面的所有文件/文件夹
                Map<String,String> mapSet = new HashMap<String, String>();
                for (int i = 0; i < filelist.length; i++) {
                    String deepPath = filepath + "/" + filelist[i];
                    File readfile = new File(deepPath);
                    if (!readfile.isDirectory()) {
                        //是一个文件
                        System.out.println("path=" + readfile.getPath());
                        System.out.println("absolutepath="
                                + readfile.getAbsolutePath());
                        System.out.println("name=" + readfile.getName());
                        Map map = readFromSingleFile(readfile.getPath());
                        mapSet.putAll(map);
                    } else if (readfile.isDirectory()) {
                        //文件夹嵌套文件夹,递归读取
                        Map map = readfile(filepath + "/" + filelist[i]);
                        //将map添加到mapSet中
                        mapSet.putAll(map);
                    }
                }
                return mapSet;
            }
        } catch (Exception e) {
            System.out.println("readfile() Exception:" + e.getMessage());
        }
        return null;
    }

    /**
     * 将(key,value)对写入到文件中
     * @param outputPath 输入文件路径
     * @param result   map对象结果集
     * @param <K> K, V 抽象类型必须实现toString()(因为要写入到文件)
     * @param <V>
     */
    public static <K, V> void writeToFile(String outputPath,Map<K, V> result) {
        BufferedWriter fileWriter = null;
        try {
            fileWriter = new BufferedWriter(new FileWriter(outputPath));

            Set<K> weightList = result.keySet();
            for (K key: weightList
                    ) {
                V value = result.get(key);
                fileWriter.write(key + " " +value +"\n");
            }
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    /**
     * 将map按照 value值进行排序 （升序/降序）
     * @param asceed true：升序 false：降序
     * @param map
     * @param <K>
     * @param <V> 一定要是实现过compare（）的抽象类型
     * @return
     * srouce: http://www.cnblogs.com/liu-qing/p/3983496.html
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(boolean asceed,Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                if(asceed){
                    return (o1.getValue()).compareTo(o2.getValue());
                }else {
                    return (o2.getValue()).compareTo(o1.getValue());
                }
            }
        });
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static void init(){
        forbiddenWords = new HashSet<String>();
        String[] temp = {
//                n 名词
                "nr", // 人名
                "nr1", // 汉语姓氏
                "nr2", // 汉语名字
                "nrj",// 日语人名
                "nrf", //音译人名
                "ns",// 地名
                "nsf",// 音译地名
                "nt",// 机构团体名
                "nz",// 其它专名
//                "nl",// 名词性惯用语
//                "ng",// 名词性语素
                "t",// 时间词
                "tg",// 时间词性语素
                "s",// 处所词
                "f",// 方位词
//                "v",// 动词
//                "vd",// 副动词
//                "vn",// 名动词
                "vshi",// 动词“是”
        "vyou",// 动词“有”
//        "vf",// 趋向动词
//        "vx",// 形式动词
//        "vi",// 不及物动词（内动词）
//        "vl",// 动词性惯用语
//        "vg",// 动词性语素
//        "a",// 形容词
//        "ad", //副形词
//        "an", //名形词
//        "ag", //形容词性语素
//        "al", //形容词性惯用语
//        "b", //区别词
//        "bl", //区别词性惯用语
//        "z",// 状态词
        "r",// 代词
        "rr",// 人称代词
        "rz",// 指示代词
        "rzt",// 时间指示代词
        "rzs",// 处所指示代词
        "rzv",// 谓词性指示代词
        "ry",// 疑问代词
        "ryt",// 时间疑问代词
        "rys",// 处所疑问代词
        "ryv",// 谓词性疑问代词
        "rg",// 代词性语素
        "m",// 数词
        "mq",// 数量词
        "q",// 量词
        "qv",// 动量词
        "qt",// 时量词
//        "d",// 副词
        "p",// 介词
        "pba",// 介词“把”
        "pbei",// 介词“被”
        "c",// 连词
        "cc",// 并列连词
        "u",// 助词
        "uzhe",// 着
        "ule",// 了 喽
        "uguo",// 过
        "ude1",// 的 底
        "ude2",// 地
        "ude3",// 得
        "usuo",// 所
        "udeng",// 等 等等 云云
        "uyy",// 一样 一般 似的 般
        "udh",// 的话
        "uls",// 来讲 来说 而言 说来
        "uzhi",// 之
        "ulian",// 连 （“连小学生都会”）
        "e",// 叹词
        "y",// 语气词(delete yg)
        "o",// 拟声词
        "h",// 前缀
        "k",// 后缀
        "x",// 字符串
        "xx",// 非语素字
        "xu",// 网址URL
        "w",// 标点符号
        "wkz",// 左括号，全角：（ 〔 ［ ｛ 《 【 〖 〈 半角：( [ { <
        "wky",// 右括号，全角：） 〕 ］ ｝ 》 】 〗 〉 半角： ) ] { >
         "wyz",// 左引号，全角：“ ‘ 『
            "wyy",// 右引号，全角：” ’ 』
            "wj",// 句号，全角：。
            "ww",// 问号，全角：？ 半角：?
            "wt",// 叹号，全角：！ 半角：!
            "wd",// 逗号，全角：， 半角：,
            "wf",// 分号，全角：； 半角： ;
            "wn",// 顿号，全角：、
            "wm",// 冒号，全角：： 半角： :
            "ws",// 省略号，全角：…… …
            "wp",// 破折号，全角：—— －－ ——－ 半角：--- ----
            "wb",// 百分号千分号，全角：％ ‰ 半角：%
            "wh",// 单位符号
        };

        for (String str:temp
             ) {
            forbiddenWords.add(str.trim());
        }
    }
    /**
     * TF-IDF与余弦相似性的应用（一）：自动提取关键词
     * http://www.ruanyifeng.com/blog/2013/03/tf-idf.html
     * @param args
     */
    public static void main(String[] args){
        TFIDF tf_idf = new TFIDF();
        init();
        //在整个文档库中包含某个词的文档数,(key, value)
        Map<String, String> allPair = readFromSingleFile(tf_idf.allCountFilePath);

        File file = new File(tf_idf.singleCountDirFilePath);
        if (!file.isDirectory()){
            System.out.println("singleCountDirFilePath不是文件夹！");
        }else{
            String[] filelist = file.list();
            int fileCounts = filelist.length;
            //遍历读取每一个文档
            for (int i = 0; i < filelist.length; i++) {
                //输入文件的路径
                String deepPath = tf_idf.singleCountDirFilePath + "/" + filelist[i];
                //输出文件保存的路径
                String resultFilePath = tf_idf.outputDirFileFilePath + "/" + filelist[i];
                //单篇文档中某个词出现的次数，(key, value)
                Map<String, String>singlePagePair = readFromSingleFile_2(deepPath);
                //结果集
                Map<String, Double> resultMap = new LinkedHashMap<>();

                int allWordCount = singlePagePair.size();
                Set<String> weightList = singlePagePair.keySet();
                System.out.println("weightList size:" +weightList.size() );
                for (String key: weightList
                        ) {
                    //考虑到文章有长短之分，为了便于不同文章的比较，进行"词频"标准化(归一化)
                    //词频(TF) = 某个词出现次数/文章总词数
                    double TF;
                    int singleWordCount = Integer.valueOf(singlePagePair.get(key));
                    TF = singleWordCount/(double)allWordCount;

                    //逆文档频率(IDF) = log(文档总数/(包含该词的文档数+1))
                    double IDF;
                    int tempCounter;
                    //判断该词是否在总统计中出现过，按理说肯定出现了至少value为1
                    if (allPair.get(key) != null){
                        //包含该词的文档数
                        tempCounter = Integer.valueOf(allPair.get(key));
                        //System.out.println("tempCounter:"+tempCounter+"\tfileCounts:"+fileCounts);
                        IDF = Math.log(fileCounts/(double)(1 + tempCounter));

                    }else {
                        IDF = 0;
                    }
                    double TF_IDF = TF*IDF;
                    //System.out.println("TF:" +TF +"\tIDF:" + IDF + "\tTF-IDF：" + TF_IDF);
                    resultMap.put(key, TF_IDF);
                }
                //对结果进行排序
                resultMap = sortByValue(false,resultMap);
                //将结果写入文件
                writeToFile(resultFilePath, resultMap);
            }
        }
    }

}
