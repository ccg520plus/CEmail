package com.ccg.emaildemo.email;

import com.ccg.emaillib.EmailClient;
import com.ccg.emaillib.EmailConfig;

public class EmailManager {

    private static EmailManager mInstance;

    private EmailClient mClient;

    private EmailManager() {
        mClient = new EmailClient();
    }

    public EmailManager config(EmailConfig config){
        if (mClient == null){
            mClient = new EmailClient();
        }
        mClient.setEmailConfig(config);
        return this;
    }

    public EmailClient getClient(){
        return mClient;
    }

    public static EmailManager getInstance(){
        if (mInstance == null){
            synchronized (EmailManager.class){
                if (mInstance == null){
                    mInstance = new EmailManager();
                }
            }
        }
        return mInstance;
    }

}
