//package org.bsm;
//
////import com.gitee.ApiException;
////import com.gitee.api.ActivityApi;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.insert.txt.context.SpringBootTest;
//
//import javax.mail.*;
//import java.sql.SQLException;
//import java.util.Properties;
//
//@SpringBootTest
//@Slf4j
//class BsmServiceApplicationTests {
//
////    @Autowired
////    private JavaMailSender jms;
////
////    @Value("${spring.mail.username}")
////    private String from;
////    @Autowired
////    private TemplateEngine templateEngine;
////    @Autowired
////    PagesServiceImpl pagesService;
////    @Autowired
////    AuthorizeServiceImpl authorizeService;
//
//    @Test
//    void contextLoads() throws SQLException, MessagingException {
//
//
//
////        List<Pages> pages = pagesService.list();
////        List<String> roles = new ArrayList<>(Arrays.asList("admin", "superAdmin", "guest", "user", "vip"));
////        for (String role :
////                roles) {
////            for (Pages page :
////                    pages) {
////                Authorize authorize = new Authorize(UUID.randomUUID().toString(), page.getPageid(), role);
////                authorizeService.save(authorize);
////            }
////        }
////        log.info("开始执行数据源加载测试类");
////        System.out.println(dataSource.getClass());
////        System.out.println(dataSource.getConnection());
////        log.info("结束执行数据源加载测试类");
////
////        log.info("开始执行redis加载测试");
////        Map<String, Object> map = new HashMap<>();
////        map.put("test1", "test1");
////        map.put("test2", "test2");
////        redisUtils.hmset("GZC", map);
////        log.info("开始执行email加载测试");
////        sendTemplateEmail("123456");
//    }
//
////    public void sendSimpleEmail() {
////        try {
////            SimpleMailMessage message = new SimpleMailMessage();
////            message.setFrom(from);
////            message.setTo("1739084007@qq.com"); // 接收地址
////            message.setSubject("一封简单的邮件"); // 标题
////            message.setText("使用Spring Boot发送简单邮件。"); // 内容
////            jms.send(message);
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
////
////    public String sendHtmlEmail() {
////        MimeMessage message = null;
////        try {
////            message = jms.createMimeMessage();
////            MimeMessageHelper helper = new MimeMessageHelper(message, true);
////            helper.setFrom(from);
////            helper.setTo("1739084007@qq.com"); // 接收地址
////            helper.setSubject("一封HTML格式的邮件"); // 标题
////            // 带HTML格式的内容
////            helper.setText("<p style='color:#42b983'>使用Spring Boot发送HTML格式邮件。</p>", true);
////            jms.send(message);
////            return "发送成功";
////        } catch (Exception e) {
////            e.printStackTrace();
////            return e.getMessage();
////        }
////    }
////
////    public String sendAttachmentsMail() {
////        MimeMessage message = null;
////        try {
////            message = jms.createMimeMessage();
////            MimeMessageHelper helper = new MimeMessageHelper(message, true);
////            helper.setFrom(from);
////            helper.setTo("1739084007@qq.com"); // 接收地址
////            helper.setSubject("一封带附件的邮件"); // 标题
////            helper.setText("详情参见附件内容！"); // 内容
////            // 传入附件
////            FileSystemResource file = new FileSystemResource(new File("src/main/resources/static/file/项目文档.docx"));
////            helper.addAttachment("项目文档.docx", file);
////            jms.send(message);
////            return "发送成功";
////        } catch (Exception e) {
////            e.printStackTrace();
////            return e.getMessage();
////        }
////    }
////
////    public String sendInlineMail() {
////        MimeMessage message = null;
////        try {
////            message = jms.createMimeMessage();
////            MimeMessageHelper helper = new MimeMessageHelper(message, true);
////            helper.setFrom(from);
////            helper.setTo("1739084007@qq.com"); // 接收地址
////            helper.setSubject("一封带静态资源的邮件"); // 标题
////            helper.setText("<html><body>博客图：<img src='cid:img'/></body></html>", true); // 内容
////            // 传入附件
////            FileSystemResource file = new FileSystemResource(new File("src/main/resources/static/img/sunshine.png"));
////            helper.addInline("img", file);
////            jms.send(message);
////            return "发送成功";
////        } catch (Exception e) {
////            e.printStackTrace();
////            return e.getMessage();
////        }
////    }
////
////    public String sendTemplateEmail(String code) {
////        MimeMessage message = null;
////        try {
////            message = jms.createMimeMessage();
////            MimeMessageHelper helper = new MimeMessageHelper(message, true);
////            helper.setFrom(from);
////            helper.setTo("1739084007@qq.com"); // 接收地址
////            helper.setSubject("邮件摸板测试"); // 标题
////            // 处理邮件模板
////            Context context = new Context();
////            context.setVariable("code", code);
////            String template = templateEngine.process("mail/registerEmailTemplate", context);
////            helper.setText(template, true);
////            jms.send(message);
////            return "发送成功";
////        } catch (Exception e) {
////            e.printStackTrace();
////            return e.getMessage();
////        }
////    }
//
//
//}
