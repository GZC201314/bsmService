//package org.bsm.scala
//
//import org.apache.spark.rdd.RDD
//import org.apache.spark.{SparkConf, SparkContext}
//
///**
// * @author GZC
// * @create 2022-01-17 11:07
// * @desc Spark处理日志
// */
//object LogHandle {
//
//  def sayHello(x: String): Unit = {
//    println("Hello," + x);
//  }
//
//  def runSpark(): Array[(String, Int)] = {
//    /* 1.建立和Sapark框架的连接*/
//
//    val sparkConf = new SparkConf().setMaster("local").setAppName("WordCount");
//    val context = new SparkContext(sparkConf);
//
//    /* 2.执行业务操作*/
//
//    //读取文件
//    val lines: RDD[String] = context.textFile("logs/bsm-info-2022-01-06.0.log")
//    //split 行数据
//
//    val clearData: RDD[String] = lines.filter((it: String) => {
//      it.contains("org.bsm.controller")
//    })
//
//    val controllerOne: RDD[(String, Int)] = clearData.map((line: String) => {
//      val strings: Array[String] = line.split(" ")
//      val key: String = strings(5).replace("org.bsm.controller.", "")
//      (key, 1)
//    })
//
//    val wordGroup: RDD[(String, Iterable[(String, Int)])] = controllerOne.groupBy(t => t._1)
//
//    val wordToCount: RDD[(String, Int)] = wordGroup.map {
//      case (str, list) => {
//        list.reduce(
//          (t1, t2) => {
//            (t1._1, t1._2 + t2._2)
//          }
//        )
//      }
//    }
//    val tuples: Array[(String, Int)] = wordToCount.collect()
//    /* 3.关闭连接*/
//    context.stop();
//    tuples
//  }
//
//
//}
