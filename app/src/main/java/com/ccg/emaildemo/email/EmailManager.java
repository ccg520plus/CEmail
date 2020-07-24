package com.ccg.emaildemo.email;

import com.ccg.emaillib.EmailClient;
import com.ccg.emaillib.EmailConfig;

public class EmailManager {

    private static EmailManager mInstance;

    private EmailClient mClient;

    private EmailManager() {

    }

    public EmailManager config(EmailConfig config){
        mClient = new EmailClient(config);
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
