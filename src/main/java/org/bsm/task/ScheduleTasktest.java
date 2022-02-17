//package org.bsm.task;
//
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
///**
// * @author GZC
// * @create 2022-02-16 14:25
// * @desc 任务异步调度测试类
// */
//@Component
//@EnableAsync
//public class ScheduleTasktest {
//
//
//    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    @Async("myAsync")
//    @Scheduled(fixedRate = 2000)
//    public void testScheduleTask() {
//        try {
//            Thread.sleep(6000);
//            System.out.println("Spring1自带的线程池" + Thread.currentThread().getName() + "-" + sdf.format(new Date()));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Async("myAsync")
//    @Scheduled(cron = "*/2 * * * * ?")
//    public void testAsyn() {
//        try {
//            Thread.sleep(1000);
//            System.out.println("Spring2自带的线程池" + Thread.currentThread().getName() + "-" + sdf.format(new Date()));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//
//}
