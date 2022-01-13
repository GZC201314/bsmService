//package org.bsm.utils;
//
//import lombok.extern.slf4j.Slf4j;
//
///**
// * @author GZC
// * @create 2022-01-11 18:46
// * @desc 用于切换线程上下文类加载器
// */
//@Slf4j
//public class ThreadContextClassLoaderSwapper {
//
//    private static final ThreadLocal<ClassLoader> classLoader = new ThreadLocal<>();
//
//    // 替换线程上下文类加载器会指定的类加载器，并备份当前的线程上下文类加载器
//    public static void replace(ClassLoader newClassLoader) {
//        log.info("newClassLoader " + newClassLoader);
//        log.info("Thread.currentThread().getContextClassLoader() " + Thread.currentThread().getContextClassLoader());
//        classLoader.set(Thread.currentThread().getContextClassLoader());
//        Thread.currentThread().setContextClassLoader(newClassLoader);
//    }
//
//    // 还原线程上下文类加载器
//    public static void restore() {
//        if (classLoader.get() == null) {
//            return;
//        }
//        Thread.currentThread().setContextClassLoader(classLoader.get());
//        classLoader.set(null);
//    }
//}
//
