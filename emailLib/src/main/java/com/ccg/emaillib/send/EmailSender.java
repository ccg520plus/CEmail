package com.ccg.emaillib.send;

import android.text.TextUtils;

import com.ccg.emaillib.Config;
import com.ccg.emaillib.EmailAuthenticator;
import com.ccg.emaillib.EmailConfig;
import com.ccg.emaillib.connect.IConnect;
import com.ccg.emaillib.connect.IPropertyImpl;

import java.util.Date;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    private EmailConfig mEmailConfig;

    private Session mSession;
    private Transport transport;

    public EmailSender(EmailConfig emailConfig) {
        if (emailConfig == null){
            throw new RuntimeException("email config is null.");
        }
        mEmailConfig = emailConfig;
    }

    private void init(boolean isDebug){
        Config config = mEmailConfig.getConfig();
        if (config == null){
            throw new RuntimeException("Please configure host and port for send mail.");
        }
        IPropertyImpl property = new IPropertyImpl(config.getHost(),config.getPort(),"smtp");
        EmailAuthenticator emailAuthenticator = new EmailAuthenticator(mEmailConfig.getUsername(),mEmailConfig.getPassword());
        mSession = Session.getInstance(property.getProperties(),emailAuthenticator);
        mSession.setDebug(isDebug);
    }

    /**
     * send email
     * @param subTitle
     * @param content
     * @param toAddresses such as: xxxx@163.com,aaaa@163.com
     * @param isDebug
     * @return
     * @throws MessagingException
     */
    public boolean send(String subTitle,String content,String toAddresses,boolean isDebug) throws MessagingException {
        init(isDebug);

        Message emailMessage = new MimeMessage(mSession);
        Address fromAddr = new InternetAddress(mEmailConfig.getUsername());
        emailMessage.setFrom(fromAddr);//设置发送地址
//        emailMessage.setRecipients(Message.RecipientType.TO, toAddr);//设置接收地址
        //设置邮箱消息的标题
        emailMessage.setSubject(subTitle);
        //设置邮件消息发送的时间
        emailMessage.setSentDate(new Date());
        if (!TextUtils.isEmpty(content)) {
            emailMessage.setContent(content,"text/html;charset=utf-8");//设置正文内容
            emailMessage.saveChanges();//保存并生成邮件对象
            transport = mSession.getTransport();
            transport.connect();
            transport.sendMessage(emailMessage,InternetAddress.parse(toAddresses));

            transport.close();
            transport = null;
            return true;
        } else {
            return false;
        }

    }

    public void close() throws MessagingException {
        if (transport != null){
            transport.close();
        }
    }



}
