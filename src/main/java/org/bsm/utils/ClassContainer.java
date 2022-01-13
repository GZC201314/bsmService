//package org.bsm.utils;
//
//import org.bsm.drive.ChildFirstClassLoader;
//
//import java.io.File;
//import java.net.MalformedURLException;
//import java.net.URL;
//
///**
// * @author GZC
// * @create 2022-01-11 18:41
// * @desc 用于存储需要ChildFirstClassLoader加载的jar包
// */
//public class ClassContainer {
//
//    private ChildFirstClassLoader childFirstClassLoader;
//
//    public ClassContainer() {
//    }
//
//    public ClassContainer(ClassLoader classLoader, String jarPath) {
//        if (jarPath == null || jarPath.length() == 0) {
//            return;
//        }
//        final URL[] urls = new URL[1];
//        try {
//            urls[0] = new File(jarPath).toURI().toURL();
//            this.childFirstClassLoader = new ChildFirstClassLoader(urls, classLoader);
//
//        } catch (MalformedURLException e) {
//
//        }
//    }
//
//    public Class<?> getClass(String name) throws ClassNotFoundException {
//        return childFirstClassLoader.loadClass(name);
//    }
//
//    public ClassLoader getClassLoader() {
//        return childFirstClassLoader;
//    }
//
//}
