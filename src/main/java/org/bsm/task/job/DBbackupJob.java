package org.bsm.task.job;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.bsm.utils.RedisUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author GZC
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
@Component
public class DBbackupJob extends QuartzJobBean {

    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    RedisUtil redisUtil;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bf = null;
        try {
            String filePath = (String) redisUtil.hget("bsm_config", "DB_BACKUP_DIR");
            String emailaddress = (String) redisUtil.hget("bsm_config", "DBA_EMAIL");
            /*获取定时任务的参数*/
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            log.info(context.getScheduler().getSchedulerInstanceId());
            log.info("taskname= {}", context.getJobDetail().getKey().getName());
            log.info("执行时间= {}", new Date());

            String containerName = (String) jobDataMap.get("containerName");
            String dbName = (String) jobDataMap.get("dbname");
            String username = (String) jobDataMap.get("username");//用户名
            String password = (String) jobDataMap.get("password");//密码
            File uploadDir = new File(filePath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String fileName = dbName + new java.util.Date().getTime() + ".sql";
            // linux执行定时任务的时候没有终端设备，TTY一词源于Teletypes,或teletypewriters。
            // 其实出现该错误和我们的一个习惯有关，一般来说我们启动容器后要与容器进行交互操作，
            // 这是，就要加上"-it"这个参数，而在定时任务中，如果让脚本在后台运行，就没有可交互的终端，
            // 这就会引发如题所示错误，解决办法就是去掉“-it”这个参数。
            String cmd = "docker exec -i " + containerName + " mysqldump -u" + username + " -p" + password + " " + dbName;
            log.info(cmd);
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", cmd});
                int result = process.waitFor();
                inputStream = process.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                bf = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();

                bf.lines().forEach(str ->{
                    sb.append(str).append("\n");
                });

                log.error(sb.toString());

                String path = filePath + dbName + "_" + new Date().getTime() + ".sql";
                //把备份的数据插入到文件中
                File file = FileUtil.writeBytes(sb.toString().getBytes(), path);

                if (result == 0 && file.exists()) {
                    log.info("数据库备份成功！");
                    // 给 DBA 发送备份成功的邮件
                    MimeMessage message;
                    try {
                        message = javaMailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(message, true);
                        helper.setFrom(from);
                        helper.setTo(emailaddress);
                        helper.setSubject("BSM数据库备份成功邮件");
                        // 处理邮件模板
                        Context emailContext = new Context();
                        emailContext.setVariable("datetime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        String template = templateEngine.process("mail/DBbackupTemplate", emailContext);

                        // 6，设置邮件内容，混合模式
                        MimeMultipart msgMultipart = new MimeMultipart("mixed");
                        message.setContent(msgMultipart);

                        // 7，设置消息正文
                        MimeBodyPart content = new MimeBodyPart();
                        msgMultipart.addBodyPart(content);

                        // 8，设置正文格式
                        MimeMultipart bodyMultipart = new MimeMultipart("related");
                        content.setContent(bodyMultipart);

                        // 9，设置正文内容
                        MimeBodyPart htmlPart = new MimeBodyPart();
                        bodyMultipart.addBodyPart(htmlPart);
                        htmlPart.setContent(template, "text/html;charset=UTF-8");

                        //设置备份文件
                        MimeBodyPart filePart = new MimeBodyPart();
                        FileDataSource dataSource = new FileDataSource(file);
                        DataHandler dataHandler = new DataHandler(dataSource);
                        // 文件处理
                        filePart.setDataHandler(dataHandler);
                        // 附件名称
                        filePart.setFileName(file.getName());
                        // 放入正文（有先后顺序，所以在正文后面）
                        msgMultipart.addBodyPart(filePart);


                        javaMailSender.send(message);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    log.error("数据库备份失败！");
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                log.error(e.getMessage());

            } finally {
                if (bf != null) {
                    bf.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }


        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
