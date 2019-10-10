package lawdata



import java.io.{File, FileWriter}

import org.apache.spark.{SparkConf, SparkContext}

class LawAnalyse {
  val filePath = "/home/chen/桌面/data_log(new)"
  val outputPath = "/home/chen/桌面/law_outdata/result.txt"
  val conf = new SparkConf().setAppName("law-analyse")
  conf.setMaster("local")
  val sc = new SparkContext(conf)
  val input = sc.textFile(filePath)

  val areas = List("北京市","天津市", "上海市", "重庆市", "河北省", "河南省", "云南省", "辽宁省",
    "黑龙江省", "湖南省", "安徽省", "山东省", "新疆维吾尔", "江苏省", "浙江省", "江西省", "湖北省", "广西壮族"
      ,"甘肃省", "山西省","内蒙古","陕西省", "吉林省","福建省","贵州省","广东省","青海省","西藏"
    ,"四川省","宁夏回族","海南省");

  val startYear = 1949
  val endYear = 2017

  def test():Unit={
    var file = new File(outputPath)
    val writer = new FileWriter(file)

    var result = new Array[String](endYear-startYear+1)
    var resultCount = new Array[Int](endYear-startYear+1)

    for (year <- startYear to endYear){
      var str = "("+ year +")"
      var temp = input.filter(line => line.contains(str)).count()
      result(year - startYear) = year + ":" + temp
      resultCount(year - startYear) = temp.toInt
    }

    //存入一个文件
    var sum =0
    for (year <- startYear to endYear){
      var year_count = result(year - startYear)
      println(year_count)
      writer.write(year_count + "\n")
      sum += resultCount(year - startYear)
    }
    writer.write("SUM\t"+sum)
    println(sum)
    writer.close
  }
  def transFormTime():Unit={
    var rd = input.map(line => (line.split(' ')(0), line.split(' ')(4)))
    var count: Long = input.map(line => (line.split(' ')(0), line.split(' ')(4))).count()
    println(count)
    rd.repartition(1).saveAsTextFile("/home/chen/桌面/law_outdata/intermediate.txt")
  }
  def transFormArea():Unit={
    var rd = input.map(line => (line.split(' ')(2), 1))
    //var count: Long = input.map(line => (line.split(' ')(2), 1)).count()
    //println(count)
    rd.repartition(1).saveAsTextFile("/home/chen/桌面/law_outdata/intermediateAreaNew.txt")
  }
  def countAreaDistr():Unit={
    //var rd = input.map(line => (line.split(' ')(2), 1))
    var file = new File("/home/chen/桌面/law_outdata/resultAreaDistr.txt")
    val writer = new FileWriter(file)
    for (str <- areas){
      println(str)
      var count = input.filter(line => line.contains(str)).count()
      writer.write(str + " " + count +" \n")
    }
    writer.close
  }
}
