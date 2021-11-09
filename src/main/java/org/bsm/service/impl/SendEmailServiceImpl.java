package org.bsm.service.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.bsm.service.ISendEmailService;
import org.bsm.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;

/**
 * @author GZC
 * @create 2021-11-02 23:19
 * @desc 发送邮件实现类
 */
@Component
@Service("sendEmailService")
public class SendEmailServiceImpl implements ISendEmailService {
    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;


    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean sendRegisterEmail(String emailaddress) {
        MimeMessage message;
        try {
            message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(emailaddress);
            helper.setSubject("BSM注册邮件");
            // 处理邮件模板
            String code = RandomStringUtils.randomNumeric(8);
            Context context = new Context();
            context.setVariable("code", code);
            String template = templateEngine.process("mail/registerEmailTemplate", context);
            helper.setText(template, true);
            javaMailSender.send(message);
            /*把验证码存到redis中,并设置有效期是2分钟*/
            redisUtil.set(emailaddress, code, 2 * 60);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
