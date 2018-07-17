package com.xiaoyu.modules.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author hongyu
 * @date 2018-07
 * @description
 */
@Component
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private static final String UTF8 = "utf-8";
    private static final boolean Is_Debug = false;
    private static final String Smtp_Port = "465";
    private static final String Smtp_Host = "smtp.qq.com";

    @PostConstruct
    public void test() {
        logger.info("fdafdfffffffffffffffffffffff");
        MailBuilder builder = new MailBuilder();
        builder.sender("1546428286@qq.com", "小往")
                .receiver("1546428286@qq.com", "Mr xiaoyu")
                .title("往往:注册通知.")
                .content("有新人注册了!<br/>名称:" + "xiao" + "<br/>来源:" + "123.212.23.12"
                        + "<br/>速速去了解一下拉"
                        + "<br/><a href='http://47.93.235.211/user/" + "1" + "'>这是个神奇的连接...</a>");
        try {
            this.sendEmail(builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Value("${mail.sender}")
    private String user;

    @Value("${mail.password}")
    private String password;

    private Properties properties() {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", Smtp_Host);
        props.setProperty("mail.smtp.auth", "true");
        // ssl
        props.setProperty("mail.smtp.port", Smtp_Port);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", Smtp_Port);
        return props;
    }

    public void sendEmail(MailBuilder mailBuilder) throws IOException {
        logger.info("go into sendEmail, params:sender={},receiver={}", mailBuilder.getSender(),
                mailBuilder.getReceiverList().get(0).getReceiver());
        Properties props = this.properties();
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
        session.setDebug(Is_Debug);
        // 创建邮件
        MimeMessage message = new MimeMessage(session);
        // 传输对象
        // 发件人：邮箱，昵称
        try {
            Transport transport = session.getTransport();
            message.setFrom(new InternetAddress(mailBuilder.getSender(), mailBuilder.getSenderName(), UTF8));
            // 收件人
            message.addRecipient(RecipientType.TO,
                    new InternetAddress(mailBuilder.getReceiverList().get(0).getReceiver(),
                            mailBuilder.getReceiverList().get(0).getReceiverName(), UTF8));
            // 标题
            message.setSubject(mailBuilder.getSubject(), UTF8);
            // 内容
            message.setContent(mailBuilder.getContent(), "text/html;charset=UTF-8");
            message.setSentDate(new Date());
            // 连接服务器
            transport.connect(user, password);
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            logger.error("send email failed:{}", e);
        }
    }

    public void sendEmails(MailBuilder mailBuilder) throws IOException {
        logger.info("go into sendEmail.");
        Properties props = this.properties();
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
        session.setDebug(Is_Debug);
        // 创建邮件
        MimeMessage message = new MimeMessage(session);
        // 传输对象
        // 发件人：邮箱，昵称
        try {
            Transport transport = session.getTransport();
            message.setFrom(new InternetAddress(mailBuilder.getSender(), mailBuilder.getSenderName(), UTF8));
            List<MailBuilder.Receiver> reList = mailBuilder.getReceiverList();
            List<Address> addrList = new ArrayList<>();
            for (MailBuilder.Receiver re : reList) {
                addrList.add(new InternetAddress(re.getReceiver(), re.getReceiverName(), UTF8));
            }
            // 收件人
            message.addRecipients(RecipientType.TO, addrList.toArray(new Address[0]));
            // 标题
            message.setSubject(mailBuilder.getSubject(), UTF8);
            // 内容
            message.setContent(mailBuilder.getContent(), "text/html;charset=UTF-8");
            message.setSentDate(new Date());
            // 连接服务器
            transport.connect(user, password);
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            logger.error("send email failed:{}", e);
        }
    }
}
