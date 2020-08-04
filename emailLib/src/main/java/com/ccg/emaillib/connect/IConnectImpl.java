package com.ccg.emaillib.connect;

import com.ccg.emaillib.Config;
import com.ccg.emaillib.EmailAuthenticator;
import com.ccg.emaillib.EmailConfig;
import com.ccg.emaillib.EmailRecource;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class IConnectImpl implements IConnect {


    private Store mStore;

    private EmailConfig mEmailConfig;

    private String currentEmailHost = "";
    private int currentEmailPort = 0;
    private String currentUsername = "";
    private String currentPassword = "";
    private int currentProtocolType = 0;

    private IPropertyImpl mProperty;

    public IConnectImpl(EmailConfig emailConfig) {
        if (emailConfig == null){
            throw new RuntimeException("Email config is null.");
        }
        mEmailConfig = emailConfig;
    }

    @Override
    public <T extends Store> T connect(boolean isForceReset) throws MessagingException {
        return getStore(isForceReset);
    }

    @Override
    public void disconnect() throws MessagingException {
        if (mStore != null){
            if (mStore.isConnected()){
                mStore.close();
            }
            mStore = null;
        }
    }

    private <T extends Store> T getStore(boolean isForceReset) throws MessagingException {
        if (isReset() || isForceReset){
            disconnect();
        }
        if (mStore == null || !mStore.isConnected()){
            System.out.println("re-login");
            Session session = null;
            Config config =  mEmailConfig.getConfig();
            if (config != null) {
                if (config.getProtocolType() == EmailRecource.POP3){
                    mProperty = new IPropertyImpl(config.getHost(),config.getPort(),"pop3");
                }else if (config.getProtocolType() == EmailRecource.IMAP){
                    mProperty = new IPropertyImpl(config.getHost(),config.getPort(),"imap");
                }
            }else{
                throw new RuntimeException("Please configure host and port for receiving mail.");
            }
            if (mProperty == null){
                throw new RuntimeException("receive email that only support pop3 and imap.");
            }
            mProperty.setConnectTimeout(mEmailConfig.getConnectTimeout())
                    .setReadTimeout(mEmailConfig.getReadTimeout())
                    .setEnableSsl(true);
            session = Session.getDefaultInstance(mProperty.getProperties(),null);

            if (session != null){
                session.setDebug(mEmailConfig.isDebug());

                if (config.getProtocolType() == EmailRecource.POP3){
                    mStore  = session.getStore("pop3");
                }else if (config.getProtocolType() == EmailRecource.IMAP){
                    mStore = session.getStore("imap");
                }
                mStore.connect(config.getHost(),
                        config.getPort(),
                        mEmailConfig.getUsername(),
                        mEmailConfig.getPassword());
            }

        }
        return (T) mStore;
    }

    private boolean isReset(){
        boolean isReset = false;
        Config config = mEmailConfig.getConfig();
        if (config == null){
            throw new RuntimeException("Please configure host and port for receiving mail.");
        }
        if (!currentEmailHost.equals(config.getHost())
                || currentEmailPort != config.getPort()
                || !currentUsername.equals(mEmailConfig.getUsername())
                || !currentPassword.equals(mEmailConfig.getPassword())
                || currentProtocolType != config.getProtocolType() ){
            currentEmailHost = config.getHost();
            currentEmailPort = config.getPort();
            currentUsername = mEmailConfig.getUsername();
            currentPassword = mEmailConfig.getPassword();
            currentProtocolType = config.getProtocolType();
            isReset = true;
        }
        return isReset;
    }

    public boolean isConnected(){
        if (mStore != null && mStore.isConnected()){
            return true;
        }
        return false;
    }

}
