//package org.bsm.drive;
//
//import java.io.IOException;
//import java.net.URL;
//import java.net.URLClassLoader;
//import java.util.ArrayList;
//import java.util.Enumeration;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * @author GZC
// * @create 2022-01-11 18:36
// * @desc 先加载当前类加载器
// */
//public class ChildFirstClassLoader extends URLClassLoader {
//
//    static {
//        ClassLoader.registerAsParallelCapable();
//    }
//
//    public ChildFirstClassLoader(URL[] urls, ClassLoader parent) {
//
////        Constructor<?> ctor = Class.forName(urls, true, parent)
////                .getDeclaredConstructor(new Class<?>[]{ClassLoader.class});
////        ClassLoader sys = (ClassLoader) ctor.newInstance(
////                new Object[]{parent});
//        super(urls, parent);
//    }
//
//    /**
//     * Finds and loads the class with the specified name from the URL search
//     * path. Any URLs referring to JAR files are loaded and opened as needed
//     * until the class is found.
//     *
//     * @param name the name of the class
//     * @return the resulting class
//     * @throws ClassNotFoundException if the class could not be found,
//     *                                or if the loader is closed.
//     * @throws NullPointerException   if {@code name} is {@code null}.
//     */
//    @Override
//    protected Class<?> findClass(String name) throws ClassNotFoundException {
//        return super.findClass(name);
//    }
//
//    /**
//     * 重写loadClass方法，部分类加载破坏双亲委派模型，（优先加载子类）。
//     *
//     * @param name
//     * @param resolve
//     * @return
//     * @throws ClassNotFoundException
//     */
//    @Override
//    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
//        synchronized (getClassLoadingLock(name)) {
//            // First, check if the class has already been loaded
//            Class<?> c = findLoadedClass(name);
//
//            if (c != null) {
//                if (resolve) {
//                    resolveClass(c);
//                }
//                return c;
//            }
//            try {
//                c = findClass(name);
//                if (c != null) {
//                    System.out.println("loaded from child, name=" + name);
//                    if (resolve) {
//                        resolveClass(c);
//                    }
//                    return c;
//                }
//            } catch (ClassNotFoundException e) {
//                // Ignore
//            }
//
//
//            try {
//                if (getParent() != null) {
//                    c = super.loadClass(name, resolve);
//                    if (c != null) {
//                        System.out.println("loaded from parent, name=" + name);
//                        if (resolve) {
//                            resolveClass(c);
//                        }
//                        return c;
//                    }
//                }
//            } catch (ClassNotFoundException e) {
//                // Ignore
//            }
//            try {
//                c = findSystemClass(name);
//                if (c != null) {
//                    System.out.println("loaded from system, name=" + name);
//                    if (resolve) {
//                        resolveClass(c);
//                    }
//                    return c;
//                }
//            } catch (ClassNotFoundException e) {
//                // Ignore
//            }
//            throw new ClassNotFoundException(name);
//        }
//    }
//
//    @Override
//    public URL getResource(String name) {
//        // first, try and find it via the URLClassloader
//        URL urlClassLoaderResource = findResource(name);
//
//        if (urlClassLoaderResource != null) {
//            return urlClassLoaderResource;
//        }
//        // delegate to super
//        return super.getResource(name);
//    }
//
//    @Override
//    public Enumeration<URL> getResources(String name) throws IOException {
//        // first get resources from URLClassloader
//        Enumeration<URL> urlClassLoaderResources = findResources(name);
//
//        final List<URL> result = new ArrayList<>();
//
//        while (urlClassLoaderResources.hasMoreElements()) {
//            result.add(urlClassLoaderResources.nextElement());
//        }
//
//        // get parent urls
//        Enumeration<URL> parentResources = getParent().getResources(name);
//
//        while (parentResources.hasMoreElements()) {
//            result.add(parentResources.nextElement());
//        }
//
//        return new Enumeration<URL>() {
//            Iterator<URL> iter = result.iterator();
//
//            @Override
//            public boolean hasMoreElements() {
//                return iter.hasNext();
//            }
//
//            @Override
//            public URL nextElement() {
//                return iter.next();
//            }
//        };
//    }
//
//
//}
//
