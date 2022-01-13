//package org.bsm.drive;
//
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.util.StringUtils;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.net.JarURLConnection;
//import java.net.URL;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.jar.JarEntry;
//import java.util.jar.JarFile;
//
//@Slf4j
//public class CustomerWebAppClassLoader extends ClassLoader {
//
//    private ClassLoader jdkClassLoader;
//
//    private Map<String, Class<?>> classMap = new HashMap<>();
//
//    private String classPath = "";
//
//    public CustomerWebAppClassLoader(URL[] urls, ClassLoader jdkClassLoader) {
//        this.jdkClassLoader = jdkClassLoader;
//
//        if (urls.length > 0) {
//            classPath = urls[0].toString();
//            try {
//                JarFile jarFile = ((JarURLConnection) urls[0].openConnection()).getJarFile();
//
//                if (jarFile != null) {
//                    //得到该jar文件下面的类实体
//                    Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
//                    while (jarEntryEnumeration.hasMoreElements()) {
//                        JarEntry entry = jarEntryEnumeration.nextElement();
//                        String jarEntryName = entry.getName();
//                        //这里我们需要过滤不是class文件和不在basePack包名下的类
//                        if (jarEntryName.contains(".class")) {
//                            String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replace("/", ".");
//
//                            Class<?> aClass = this.getClass().getClassLoader().loadClass(className);
//                            classMap.put(className, aClass);
//                        }
//                    }
//                }
////                findClassesByJar("", jar, classNames);
//            } catch (IOException | ClassNotFoundException e) {
//                log.error(e.getMessage());
//                e.printStackTrace();
//            }
//        }
//        /*在这边加载驱动类*/
//    }
//
//    @SneakyThrows
//    @Override
//    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
//        /*先从自定义的jar包中加载class*/
//        Class result = null;
//        boolean contains = classMap.containsKey(name);
//        if (!contains) {
//            log.error("ClassNotFoundException");
//            throw new ClassNotFoundException();
//        }
//        result = classMap.get(name);
//        if (result != null) {
//            return result;
//        }
//
//        try {
//            //这里要使用 JDK 的类加载器加载 java.lang 包里面的类
//            result = jdkClassLoader.loadClass(name);
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        return result;
//    }
//
//    private byte[] getClassData(File tradeFile) throws IOException {
//
//        byte[] buffer = null;
//        try (FileInputStream fis = new FileInputStream(tradeFile); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
//            byte[] b = new byte[1024];
//            int n;
//            while ((n = fis.read(b)) != -1) {
//                bos.write(b, 0, n);
//            }
//            fis.close();
//            bos.close();
//            buffer = bos.toByteArray();
//        } catch (IOException e) {
//            log.error(e.getMessage());
//            e.printStackTrace();
//        }
//        return buffer;
//    }
//
//
//    private static void findClassesByJar(String pkgName, JarFile jar, Set<String> classes) {
//        String pkgDir = pkgName.replace(".", "/");
//        // 从此jar包 得到一个枚举类
//        Enumeration<JarEntry> entry = jar.entries();
//
//        JarEntry jarEntry;
//        String name, className;
//        Class<?> claze;
//        // 同样的进行循环迭代
//        while (entry.hasMoreElements()) {
//            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文
//            jarEntry = entry.nextElement();
//
//            name = jarEntry.getName();
//            // 如果是以/开头的
//            if (name.charAt(0) == '/') {
//                // 获取后面的字符串
//                name = name.substring(1);
//            }
//
//            if (jarEntry.isDirectory() || !name.startsWith(pkgDir) || !name.endsWith(".class")) {
//                continue;
//            }
//            //如果是一个.class文件 而且不是目录
//            // 去掉后面的".class" 获取真正的类名
//            className = name.substring(0, name.length() - 6);
//            if (StringUtils.hasText(className)) {
//                classes.add(className);
//            }
//        }
//    }
//
//}