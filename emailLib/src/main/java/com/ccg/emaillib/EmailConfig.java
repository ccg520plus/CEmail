package com.ccg.emaillib;

public class EmailConfig {

    private Config config;
    private String username;
    private String password;

    private long readTimeout = 30 * 1000;
    private long connectTimeout = 30 * 1000;
    private boolean isDebug = false;
    private boolean isOnlyReceiveUnreadEmail = false;//只对imap协议有效

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public EmailConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    public EmailConfig setPassword(String password) {
        this.password = password;
        return this;
    }


    public EmailConfig setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public EmailConfig setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public EmailConfig setDebug(boolean debug) {
        isDebug = debug;
        return this;
    }

    public EmailConfig setOnlyReceiveUnreadEmail(boolean onlyReceiveUnreadEmail) {
        isOnlyReceiveUnreadEmail = onlyReceiveUnreadEmail;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public boolean isOnlyReceiveUnreadEmail() {
        return isOnlyReceiveUnreadEmail;
    }

}
