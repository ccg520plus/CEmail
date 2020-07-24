package com.ccg.emaillib.connect;

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

            if (mEmailConfig.getProtocolType() == EmailRecource.POP3){
                mProperty = new IPropertyImpl(mEmailConfig.getHost(),mEmailConfig.getPort(),"pop3");
            }else if (mEmailConfig.getProtocolType() == EmailRecource.IMAP){
                mProperty = new IPropertyImpl(mEmailConfig.getHost(),mEmailConfig.getPort(),"imap");
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

                if (mEmailConfig.getProtocolType() == EmailRecource.POP3){
                    mStore  = session.getStore("pop3");
                }else if (mEmailConfig.getProtocolType() == EmailRecource.IMAP){
                    mStore = session.getStore("imap");
                }
                mStore.connect(mEmailConfig.getHost(),
                        mEmailConfig.getPort(),
                        mEmailConfig.getUsername(),
                        mEmailConfig.getPassword());
            }

        }
        return (T) mStore;
    }

    private boolean isReset(){
        boolean isReset = false;
        if (!currentEmailHost.equals(mEmailConfig.getHost()) || currentEmailPort != mEmailConfig.getPort()
                || !currentUsername.equals(mEmailConfig.getUsername())
                || !currentPassword.equals(mEmailConfig.getPassword())
                || currentProtocolType != mEmailConfig.getProtocolType() ){
            currentEmailHost = mEmailConfig.getHost();
            currentEmailPort = mEmailConfig.getPort();
            currentUsername = mEmailConfig.getUsername();
            currentPassword = mEmailConfig.getPassword();
            currentProtocolType = mEmailConfig.getProtocolType();
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
