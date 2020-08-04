package com.ccg.emaillib.receive;

import com.ccg.emaillib.Config;
import com.ccg.emaillib.EmailConfig;
import com.ccg.emaillib.EmailRecource;
import com.ccg.emaillib.connect.IConnect;
import com.ccg.emaillib.entries.Email;
import com.ccg.emaillib.parse.ParseEmail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.FetchProfile;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class IReceiverImpl implements IReceiver{

    private EmailConfig mEmailConfig;
    private FetchProfile mFetchProfile;
    private IConnect connect;

    private IReceiver mReceiver;

    public IReceiverImpl(EmailConfig emailConfig, FetchProfile fetchProfile,IConnect iConnect) {
        mEmailConfig = emailConfig;
        mFetchProfile = fetchProfile;
        connect = iConnect;

        init();
    }

    private void init(){
        Config config = mEmailConfig.getConfig();
        if (config == null){
            throw new RuntimeException("Please configure host and port for receiving mail.");
        }
        if (config.getProtocolType() == EmailRecource.IMAP){
            mReceiver = new IMAPReceiver(mEmailConfig.isOnlyReceiveUnreadEmail(),mFetchProfile,connect);
        }else if (config.getProtocolType() == EmailRecource.POP3){
            mReceiver = new POP3Receiver(mFetchProfile,connect);
        }
    }

    /**
     * 读取邮件
     * @param start
     * @param counts
     * @return
     * @throws Exception
     */
    public List<Email> read(int start, int counts,String folderName) throws Exception {
        if (start == 0){
            start = 1;
        }
        Message[] messages = receive(start,counts,folderName);
        System.out.println("email size:"+messages.length);
        return parseMessages(messages);
    }

    private List<Email> parseMessages(Message... messages) throws Exception {
        List<Email> emails = new ArrayList<>();
        Email email;
        ParseEmail parseEmail;
        String from;
        for (Message message : messages) {
            if (message != null){
                email = new Email();
                parseEmail = new ParseEmail((MimeMessage) message);
                parseEmail.setDateformate("yyyy-MM-dd  HH:mm:ss");

                //如果邮件内容解析失败，且失败3次，那么会直接抛出异常
                boolean isParseSuccess = false;
                Exception exception = null;
                for (int i = 0 ; i < 3 ; i++){
                    try{
                        //解析邮件内容
                        parseEmail.getEmailContent(message);
                        isParseSuccess = true;
                        break;
                    }catch (MessagingException | IOException e){
                        exception = e;
                        isParseSuccess = false;
                    }
                }
                if (!isParseSuccess){
                    throw exception;
                }

                email.setContent(parseEmail.getBodyText());
                email.setHtmlContent(parseEmail.getHtmlText());
                email.setDateTime(parseEmail.getSendDate());
                from = parseEmail.getFrom();
                email.setFrom(from.substring(from.lastIndexOf("<") + 1, from.lastIndexOf(">")));
                email.setTitle(parseEmail.getSubject());
                emails.add(email);
            }
        }
        return emails;
    }

    @Override
    public Message[] receive(int start, int readCounts, String folderName) throws Exception {
        return mReceiver.receive(start,readCounts,folderName);
    }

    @Override
    public void close() throws Exception {
        mReceiver.close();
    }

    @Override
    public void destroy() throws Exception {
        mReceiver.destroy();
    }

}
