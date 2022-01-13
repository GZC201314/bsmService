package org.bsm;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.protocol.IMAPProtocol;
import org.bsm.entity.User;
import org.bsm.pagemodel.PageUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import javax.mail.*;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;

/**
 * @author GZC
 * @create 2021-11-03 16:10
 * @desc
 */
public class CopyValueTest {
    @Test
    public void copyValue() {
        User user = new User();
        user.setUserid(UUID.randomUUID().toString());
        user.setUsername("GZC");
        user.setCreatetime(LocalDateTime.now());
        PageUser pageUser = new PageUser();
        pageUser.setUsername("admin");
//        BeanUtils.copyProperties(pageUser, user);
        BeanUtils.copyProperties(pageUser, user, "userid", "createtime");
        System.out.println("复制后的用户信息是 :" + user);
    }

    @Test
    public void emailInfo() throws MessagingException {
        // 准备连接服务器的会话信息
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.host", "imap.163.com");
        props.setProperty("mail.imap.port", "143");

        // 创建Session实例对象
        Session session = Session.getInstance(props);

        // 创建IMAP协议的Store对象
        Store store = session.getStore("imap");

        // 连接邮件服务器
        store.connect("gzc201314@163.com", "abc123");
        // 获得收件箱
        Folder folder = store.getFolder("INBOX");
        IMAPFolder imapFolder = (IMAPFolder) folder;
        //javamail中使用id命令有校验checkOpened, 所以要去掉id方法中的checkOpened();
        imapFolder.doCommand(new IMAPFolder.ProtocolCommand() {
            public Object doCommand(IMAPProtocol p) throws com.sun.mail.iap.ProtocolException {
                p.id("FUTONG");
                return null;
            }
        });
        // 获得收件箱
        // 以读模式打开收件箱
        imapFolder.open(Folder.READ_ONLY);

        // 获得收件箱的邮件列表
        Message[] messages = imapFolder.getMessages();

        // 打印不同状态的邮件数量
        System.out.println("收件箱中共" + messages.length + "封邮件!");
        System.out.println("收件箱中共" + imapFolder.getUnreadMessageCount() + "封未读邮件!");
        System.out.println("收件箱中共" + imapFolder.getNewMessageCount() + "封新邮件!");
        System.out.println("收件箱中共" + imapFolder.getDeletedMessageCount() + "封已删除邮件!");

        System.out.println("------------------------开始解析邮件----------------------------------");


        int total = imapFolder.getMessageCount();
        System.out.println("-----------------您的邮箱共有邮件：" + total + " 封--------------");
        // 得到收件箱文件夹信息，获取邮件列表
        Message[] msgs = folder.getMessages();
        System.out.println("\t收件箱的总邮件数：" + msgs.length);
        for (int i = 0; i < total; i++) {
            Message a = msgs[i];
            //   获取邮箱邮件名字及时间

            System.out.println(a.getSubject() + "        " + a.getSentDate());


            System.out.println("==============");
//                System.out.println(a.getSubject() + "   接收时间：" + a.getReceivedDate().toLocaleString()+"  contentType()" +a.getContentType());
        }
        System.out.println("\t未读邮件数：" + imapFolder.getUnreadMessageCount());
        System.out.println("\t新邮件数：" + imapFolder.getNewMessageCount());
        System.out.println("----------------End------------------");


        // 关闭资源
        imapFolder.close(false);
        store.close();
    }
}
