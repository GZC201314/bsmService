package org.bsm.drive;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * @author GZC
 * @create 2022-01-07 19:07
 * @desc 自定义类加载器
 */
public class CustomerWebAppClassLoader extends URLClassLoader {
    public CustomerWebAppClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public CustomerWebAppClassLoader(URL[] urls) {
        super(urls);
    }

    public CustomerWebAppClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    /**
     * 将findClass()方法开放(findClass()方法本身是protected类型的，不能在外界直接访问)，直接调用以获取class，绕过双亲委派机制。
     *
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    public Class<?> findClass(final String name) throws ClassNotFoundException {
        return super.findClass(name);
    }
}
