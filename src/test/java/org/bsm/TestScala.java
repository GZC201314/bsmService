//package org.bsm;
//
//import lombok.extern.slf4j.Slf4j;
//import org.bsm.scala.LogHandle;
//import org.junit.jupiter.api.Test;
//import scala.Tuple2;
//
///**
// * @author GZC
// * @create 2022-01-17 15:59
// * @desc 集成scala的测试类
// */
//@Slf4j
//public class TestScala {
//    @Test
//    public void testScalaMenthod() {
//        Tuple2<String, Object>[] tuple2s = LogHandle.runSpark();
//        for (Tuple2<String, Object> tuple :
//                tuple2s) {
//            log.info("返回的数据是{}" + tuple._1());
//            log.info("返回的数据是{}" + tuple._2());
//        }
//    }
//}
