//package org.bsm;
//
//import org.beetl.core.Configuration;
//import org.beetl.core.GroupTemplate;
//import org.beetl.core.Template;
//import org.beetl.core.resource.ClasspathResourceLoader;
//import org.bsm.entity.User;
//import org.bsm.pagemodel.PageUser;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.BeanUtils;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.*;
//
///**
// * @author GZC
// * @create 2021-11-03 16:10
// * @desc
// */
////public class IbeetlTest {
////    @Test
////    public void ibeetlInsertTest() throws IOException {
////        ClasspathResourceLoader classpathResourceLoader = new ClasspathResourceLoader("templates/");
////        Configuration cfg = Configuration.defaultConfiguration();
////
////        GroupTemplate groupTemplate = new GroupTemplate(classpathResourceLoader, cfg);
////        Template template = groupTemplate.getTemplate("/mysql_insert.txt");
////        Map<String, String> map = new HashMap<>();
////        map.put("username","gzc");
////        map.put("age","23");
////        template.binding("tablename","tablename");
////        template.binding("keyValueMap",map);
////        String render = template.render();
////        System.out.println(render);
////    }
////    @Test
////    public void ibeetlUpdateTest() throws IOException {
////        ClasspathResourceLoader classpathResourceLoader = new ClasspathResourceLoader("templates/");
////        Configuration cfg = Configuration.defaultConfiguration();
////
////        GroupTemplate groupTemplate = new GroupTemplate(classpathResourceLoader, cfg);
////        Template template = groupTemplate.getTemplate("/insert.txt");
////        Map<String, String> map = new HashMap<>();
////        map.put("username","gzc");
////        map.put("age","23");
////        template.binding("tablename","tablename");
////        template.binding("keyValueMap",map);
////        String render = template.render();
////        System.out.println(render);
////    }
////}
