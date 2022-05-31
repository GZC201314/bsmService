package org.bsm;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @author GZC
 * @create 2022-05-30 16:50
 * @desc
 */
public class CASTest {

    static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(100, 1);

    @Test
    public void testCAS() throws InterruptedException {

        new Thread(() -> {
            //获取版本号
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName() + "\t第一次版本号:" + stamp);
            //暂停一会线程
            try {
                TimeUnit.SECONDS.sleep(1);
                atomicStampedReference.compareAndSet(100, 101, 1, 2);
                atomicStampedReference.compareAndSet(101, 100, 2, 3);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();

        new Thread(() -> {
            //获取版本号
            //暂停一会线程
            try {
                TimeUnit.SECONDS.sleep(3);
                System.out.println(atomicStampedReference.getStamp());
                System.out.println(atomicStampedReference.compareAndSet(100, 2022, 1, 2));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();
        TimeUnit.SECONDS.sleep(5);
    }

}
