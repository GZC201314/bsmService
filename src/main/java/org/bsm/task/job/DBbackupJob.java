package org.bsm.task.job;

import cn.hutool.core.codec.Base64;
import lombok.extern.slf4j.Slf4j;
import org.bsm.pagemodel.PageGiteeApiCaller;
import org.bsm.pagemodel.PageGiteeFile;
import org.bsm.service.IGiteeService;
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

import javax.annotation.Resource;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @Resource
    private IGiteeService giteeService;

    @Autowired
    RedisUtil redisUtil;

    @Override
    protected void executeInternal(JobExecutionContext context) {

        // 获取数据库备份文件夹下所有的备份文件
        PageGiteeApiCaller pageGiteeApiCaller = new PageGiteeApiCaller();
        pageGiteeApiCaller.setOwner("GZC201314");
        pageGiteeApiCaller.setSha("master");
        pageGiteeApiCaller.setRepo("tuchuang");
        pageGiteeApiCaller.setRecursive("1");
        // 获取仓库下所有的
        List<PageGiteeFile> filesByDir = giteeService.getFilesByDir(pageGiteeApiCaller);
        PageGiteeFile dbbackup = null;
        Optional<PageGiteeFile> first = filesByDir.stream().filter(dir -> "BSM/DBBackUp".equals(dir.getPath())).findFirst();
        if (first.isPresent()){
            dbbackup = first.get();
        }
        assert dbbackup != null;
        pageGiteeApiCaller.setSha(dbbackup.getSha());
        List<PageGiteeFile> filesDbbackup = giteeService.getFilesByDir(pageGiteeApiCaller);
        if (filesDbbackup.size() > 8) {
            for (int i = 7; i < filesDbbackup.size(); i++) {
                PageGiteeFile pageGiteeFile = filesDbbackup.get(i);
                pageGiteeApiCaller.setPath("BSM/DBBackUp/" + pageGiteeFile.getPath());
                pageGiteeApiCaller.setSha(pageGiteeFile.getSha());
                giteeService.deleteFile(pageGiteeApiCaller);
            }
        }


        String fileUrl = "";
        try {
            String filePath = (String) redisUtil.hget("bsm_config", "DB_BACKUP_DIR");
            String emailaddress = (String) redisUtil.hget("bsm_config", "DBA_EMAIL");
            /*获取定时任务的参数*/
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            log.info(context.getScheduler().getSchedulerInstanceId());
            log.info("taskname= {}", context.getJobDetail().getKey().getName());
            log.info("执行时间= {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            String containerName = (String) jobDataMap.get("containerName");
            String dbName = (String) jobDataMap.get("dbname");
            String username = (String) jobDataMap.get("username");//用户名
            String password = (String) jobDataMap.get("password");//密码
            String fileName = dbName + new java.util.Date().getTime() + ".sql";
            // linux执行定时任务的时候没有终端设备，TTY一词源于Teletypes,或teletypewriters。
            // 其实出现该错误和我们的一个习惯有关，一般来说我们启动容器后要与容器进行交互操作，
            // 这是，就要加上"-it"这个参数，而在定时任务中，如果让脚本在后台运行，就没有可交互的终端，
            // 这就会引发如题所示错误，解决办法就是去掉“-it”这个参数。
            String cmd = "docker exec -i " + containerName + " mysqldump -u" + username + " -p" + password + " " + dbName + " -r " + filePath + fileName;
            log.info(cmd);
            try {
                long now1 = new Date().getTime();
                Process process = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", cmd});

                int result = process.waitFor();
                long now = new Date().getTime();
                log.info("本次数据库备份花费时间是，{}毫秒", now - now1);

                if (result == 0) {
                    log.info("数据库备份成功！");

                    // 把备份文件从容器中移动出来
                    String cpCmd = "docker cp " + containerName + ":" + filePath + fileName + " " + filePath;
                    Process cpProcess = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", cpCmd});
                    int cpResult = cpProcess.waitFor();
                    File file = new File(filePath + fileName);
                    if (cpResult == 0 && file.exists()) {


                        PageGiteeApiCaller commitpageGiteeApiCaller = new PageGiteeApiCaller();
                        commitpageGiteeApiCaller.setOwner("GZC201314");
                        commitpageGiteeApiCaller.setPath("BSM/DBBackUp/" + fileName);
                        commitpageGiteeApiCaller.setRepo("tuchuang");
                        String fileBase64 = Base64.encode(Files.readAllBytes(file.toPath()));
                        commitpageGiteeApiCaller.setContent(fileBase64);
                        commitpageGiteeApiCaller.setMessage("数据库备份文件上传 " + LocalDateTime.now());
                        fileUrl = giteeService.addFile(commitpageGiteeApiCaller);
                        //删除备份文件,容器中的备份文件，服务器上的备份文件
                        // 1.删除容器中的备份文件
                        String rmCmd = "docker exec -i " + containerName + " rm " + filePath + fileName;
                        Process rmProcess = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", rmCmd});
                        boolean delResult = file.delete();
                        int rmResult = rmProcess.waitFor();
                        if (!(rmResult == 0 && delResult)) {
                            return;
                        }


                    }

                    // 给 DBA 发送备份成功的邮件
                    String enableEmail = (String) redisUtil.hget("bsm_config", "ENABLE_EMAIL");
                    if ("true".equals(enableEmail)) {
                        MimeMessage message;
                        try {
                            message = javaMailSender.createMimeMessage();
                            MimeMessageHelper helper = new MimeMessageHelper(message, true);
                            helper.setFrom(from);
                            helper.setTo(emailaddress);
                            helper.setSubject("BSM数据库备份成功邮件");
                            // 处理邮件模板
                            Context emailContext = new Context();
                            emailContext.setVariable("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                            emailContext.setVariable("fileurl", fileUrl);
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


                            javaMailSender.send(message);
                        } catch (Exception e) {
                            log.error(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                } else {
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
