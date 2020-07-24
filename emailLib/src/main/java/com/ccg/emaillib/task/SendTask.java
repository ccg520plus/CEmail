package com.ccg.emaillib.task;

import com.ccg.emaillib.EmailConfig;
import com.ccg.emaillib.send.EmailSender;

import javax.mail.MessagingException;

public class SendTask extends AbstractTask{

    private EmailSender mEmailSender;

    private String subTitle;
    private String content;
    private String addresses;


    public SendTask(EmailConfig emailConfig,
                    String subtitle,
                    String content,
                    String addresses,
                    OnThreadResultListener onThreadResultListener){
        this(emailConfig,onThreadResultListener);
        this.subTitle = subtitle;
        this.content = content;
        this.addresses = addresses;
    }

    private SendTask(EmailConfig emailConfig, OnThreadResultListener onThreadResultListener) {
        super(emailConfig, onThreadResultListener);
    }

    @Override
    public void runTask(EmailConfig emailConfig) {
        init(emailConfig);
        try {
            boolean sendSuccess = mEmailSender.send(subTitle, content, addresses, emailConfig.isDebug());
            if (sendSuccess){
                onThreadResult(OnThreadResultListener.SUCCESS,OnThreadResultListener.SEND,"");
            }else{
                onThreadResult(OnThreadResultListener.FAILED,OnThreadResultListener.SEND,new RuntimeException("content is empty."));
            }
        } catch (MessagingException e) {
            onThreadResult(OnThreadResultListener.FAILED,OnThreadResultListener.SEND,e);
        }
    }

    @Override
    public void stopTask() {
        if (mEmailSender != null){
            try {
                mEmailSender.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        onThreadResult(OnThreadResultListener.FAILED,OnThreadResultListener.SEND,new RuntimeException("send timeout."));
    }

    private void init(EmailConfig emailConfig){
        mEmailSender = new EmailSender(emailConfig);
    }
}
