package example

import org.apache.spark.{SparkConf, SparkContext}

object WordCount1 {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("wordcount")
    val sc = new  SparkContext(conf)
    sc.setLogLevel("WARN")
    //sc.setLogLevel("DEBUG")
    //sc.setLogLevel("ERROR")
    //sc.setLogLevel("INFO")

    val  input = sc.textFile("/home/chen/桌面/司法大数据/jieba_result2/jieba_txt")


    //val lines = input.flatMap(line => line.split(" "))
    //val lines = input.map(word=>(word.split(" ")(1),1)).reduceByKey((x,y)=>x+y).sortByKey()

    //val count = input.map(word=>(word.split(" ")(0),word.split(" ")(1).toInt)).reduceByKey{case(x, y) => x+y}.sortBy(x => x._2, false)
    //val count = input.map(word=>(word.split(" ")(0), 1)).reduceByKey{case(x, y) => x+y}.sortBy(x => x._2, false)
    val count = input.map(word=>(word.split(" ")(1), 1)).reduceByKey{case(x, y) => x+y}.sortBy(x => x._2, false)
//    val count = input.map(line=>{
//      var array = line.split(" ")
//      var item = array(0).toString()
//      //println(array(0) + "\t" + array(1))
//    })

    val output = count.repartition(1).saveAsTextFile("/home/chen/桌面/司法大数据/output/out_3");

  }

//  def count(): Unit = {
//
//    System.out.print("wordCount!\n");
//    val conf = new SparkConf().setAppName("wordCount")
//    val sc = new SparkContext(conf)
//
//    val textFile = sc.textFile("/home/chen/桌面/data.txt")
//    val counts = textFile.flatMap(line => line.split(" "))
//      .map(word => (word, 1))
//      .reduceByKey(_ + _)
//    counts.saveAsTextFile("/home/chen/桌面/...")
//  }
}
