package org.bsm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sun.mail.imap.IMAPFolder;
import lombok.extern.slf4j.Slf4j;
import org.bsm.pagemodel.PageMessage;
import org.bsm.service.IWarnMessageService;
import org.bsm.utils.EmailUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2021-11-09
 */
@Slf4j
@Service
public class WarnMessageServiceImpl implements IWarnMessageService {

    /**
     * 获取告警邮件列表分页信息
     *
     * @param pageMessage 前台传参
     * @return JSONObject
     */
    @Override
    public JSONObject getWarnMessageList(PageMessage pageMessage) {
        JSONObject result = new JSONObject();
        // 准备连接服务器的会话信息
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.host", "imap.163.com");
        props.setProperty("mail.imap.port", "143");
        IMAPFolder imapFolder = null;
        // 创建Session实例对象
        Session session = Session.getInstance(props);

        // 创建IMAP协议的Store对象
        Store store = null;
        try {
            store = session.getStore("imap");
            store.connect("gzc201314@163.com", "xxxx");
            Folder folder = store.getFolder("INBOX");
            // 连接邮件服务器
            // 获得收件箱
            imapFolder = (IMAPFolder) folder;
            //javamail中使用id命令有校验checkOpened, 所以要去掉id方法中的checkOpened();
            imapFolder.doCommand(p -> {
                p.id("uuid");
                return null;
            });
            // 获得收件箱
            // 以读模式打开收件箱
            imapFolder.open(Folder.READ_ONLY);

            // 获得收件箱的邮件列表
            Message[] messages = imapFolder.getMessages();
            /*对发送时间降序排列*/
            Arrays.sort(messages, (o1, o2) -> {
                try {
                    return o2.getSentDate().compareTo(o1.getSentDate());
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                return 0;
            });

            // 打印不同状态的邮件数量
//            System.out.println("收件箱中共" + messages.length + "封邮件!");
//            System.out.println("收件箱中共" + imapFolder.getUnreadMessageCount() + "封未读邮件!");
//            System.out.println("收件箱中共" + imapFolder.getNewMessageCount() + "封新邮件!");
//            System.out.println("收件箱中共" + imapFolder.getDeletedMessageCount() + "封已删除邮件!");

            System.out.println("------------------------开始解析邮件----------------------------------");

            // 获取查询信息
            String subject = pageMessage.getSubject();
            Date starttime = pageMessage.getStarttime();
            Date endtime = pageMessage.getEndtime();
            boolean isSearch = (StringUtils.hasText(subject) || starttime != null || endtime != null);
            //获取分页信息
            Integer page = pageMessage.getPage().getPage();
            Integer pageSize = pageMessage.getPage().getPageSize();

            int total;
            List<PageMessage> messagesList = new ArrayList<>();
            // 搜索列表
            if (isSearch) {
                List<PageMessage> totalMessages = new ArrayList<>();
                //先搜索过滤
                for (Message message :
                        messages) {
                    if (starttime != null && endtime != null) {
                        if (message.getSubject().contains(subject) && message.getSentDate().after(starttime) && message.getSentDate().before(endtime)) {
                            PageMessage pageMessage1 = new PageMessage();
                            BeanUtils.copyProperties(message, pageMessage1);

                            MimeMessage msg = (MimeMessage) message;
                            StringBuffer content = new StringBuffer(30);
                            EmailUtil.getMailTextContent(msg, content);
                            pageMessage1.setContent(content.toString());
                            pageMessage1.setSentDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(message.getSentDate()));
                            totalMessages.add(pageMessage1);
                        }
                    } else {
                        if (message.getSubject().contains(subject)) {
                            PageMessage pageMessage1 = new PageMessage();
                            BeanUtils.copyProperties(message, pageMessage1);
                            MimeMessage msg = (MimeMessage) message;
                            StringBuffer content = new StringBuffer(30);
                            EmailUtil.getMailTextContent(msg, content);
                            pageMessage1.setContent(content.toString());
                            pageMessage1.setSentDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(message.getSentDate()));
                            totalMessages.add(pageMessage1);
                        }

                    }
                }
                int length = totalMessages.size();
                for (int i = page * pageSize; i < page * pageSize + pageSize; i++) {
                    if (i >= length) {
                        break;
                    }
                    messagesList.add(totalMessages.get(i));
                }
                total = totalMessages.size();


            } else {//不是搜索列表
                for (int i = page * pageSize; i < page * pageSize + pageSize; i++) {
                    if (i >= messages.length) {
                        break;
                    }
                    PageMessage pageMessage1 = new PageMessage();
                    BeanUtils.copyProperties(messages[i], pageMessage1);
                    MimeMessage msg = (MimeMessage) messages[i];
                    StringBuffer content = new StringBuffer(30);
                    EmailUtil.getMailTextContent(msg, content);
                    pageMessage1.setContent(content.toString());
                    pageMessage1.setSentDate(new SimpleDateFormat("HH:mm:ss").format(messages[i].getSentDate()));
                    messagesList.add(pageMessage1);
                }
                total = messages.length;
            }
            result.put("total", total);
            result.put("records", messagesList);

        } catch (MessagingException | IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (imapFolder != null) {

                try {
                    imapFolder.close(false);
                } catch (MessagingException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
            if (store != null) {

                try {
                    store.close();
                } catch (MessagingException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }


        return result;
    }
}
