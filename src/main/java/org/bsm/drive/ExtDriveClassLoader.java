package org.bsm.drive;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author GZC
 * @create 2022-01-11 18:36
 * @desc 先加载当前类加载器
 */
@Slf4j
public class ExtDriveClassLoader extends URLClassLoader {

    //属于本类加载器加载的jar包
    protected JarFile jarFile;

    //保存已经加载过的Class对象
    protected Map<String, Class> cacheClassMap = new HashMap<>();

    //保存本加载器加载的class字节码
    protected Map<String, byte[]> classBytesMap = new HashMap<>();


    public ExtDriveClassLoader(URL[] urls, ClassLoader parent) throws IOException {
        super(urls, parent);
        URL url = urls[0];
        String path = url.getPath();
        jarFile = new JarFile(path);
        init();
    }


    private void init() {
        //获取jar包每一个class
        Enumeration<JarEntry> entries = jarFile.entries();
        InputStream input = null;
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            //加载字节码文件
            if (name.endsWith(".class")) {
                //转换成全类名
                String className = name.replace(".class", "").replaceAll("/", ".");
                try {
                    input = jarFile.getInputStream(jarEntry);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int bufferSize = 4096;
                    byte[] buffer = new byte[bufferSize];
                    int bytesNumRead;
                    while ((bytesNumRead = input.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesNumRead);
                    }
                    byte[] classBytes = baos.toByteArray();
                    classBytesMap.put(className, classBytes);


                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        //将jar中的每一个class字节码进行class载入

        for (Map.Entry<String, byte[]> entry : classBytesMap.entrySet()) {
            String className = entry.getKey();
            Class<?> aClass = null;
            try {
                aClass = loadClass(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                log.warn("cannot load " + className);
                continue;
            }
            cacheClassMap.put(className, aClass);
        }
    }

    /**
     * Loads the class with the specified <a href="#name">binary name</a>.
     * This method searches for classes in the same manner as the {@link
     * #loadClass(String, boolean)} method.  It is invoked by the Java virtual
     * machine to resolve class references.  Invoking this method is equivalent
     * to invoking {@link #loadClass(String, boolean) <tt>loadClass(name,
     * false)</tt>}.
     *
     * @param name The <a href="#name">binary name</a> of the class
     * @return The resulting <tt>Class</tt> object
     * @throws ClassNotFoundException If the class was not found
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (cacheClassMap.get(name) != null) {
            return cacheClassMap.get(name);
        }
        /*如果本类加载器有类该，则先加载本类加载器中的类*/
        if (classBytesMap.get(name) == null) {
            try {
                return super.loadClass(name);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else {
            byte[] classBytes = classBytesMap.get(name);
            /*判断一个类是否被重复加载,findClass*/

            Class<?> aClass = findLoadedClass(name);
            if (aClass != null) {
                return aClass;
            }
            return defineClass(name, classBytes, 0, classBytes.length);
        }

    }
}

