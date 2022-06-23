package org.bsm.task.job;

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
import java.io.File;
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
            if (!uploadDir.exists()){
                uploadDir.mkdirs();
            }

            String fileName = dbName + new java.util.Date().getTime() + ".sql";
            String cmd = "sudo docker exec -it "+containerName+" mysqldump -u" + username + "  -p" + password +" "+ dbName + " -r "
                    + filePath + fileName;
            try {
                Process process = Runtime.getRuntime().exec(cmd);
                int result = process.waitFor();
                if(result ==0){
                    log.info("数据库备份成功！");
                    File file = new File(filePath + fileName);
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
                }else {
                    log.error("数据库备份失败！");
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                log.error(e.getMessage());

            }


        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
