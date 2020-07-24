package com.ccg.emaillib.connect;

import java.util.Properties;

class SMTPProperty implements IProperty {

    private int port;
    private String host;
    private long readTimeout = 30 * 1000;

    SMTPProperty(String host, int port, long readTimeout) {
        this.host = host;
        this.port = port;
        this.readTimeout = readTimeout;
    }

    @Override
    public Properties getProperties() {
        Properties props = System.getProperties();
        //端口号不等于25
        if (port != 25){
            //SSL加密
            if (port == 465){
                props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.setProperty("mail.smtp.socketFactory.port",String.valueOf(port));
                props.setProperty("mail.smtp.ssl.enable", "true");//是否开启ssl
                props.setProperty("mail.smtp.ssl.trust",host);
            }else if (port == 587){//TLS加密
                props.put("mail.smtp.starttls.enable","true");
            }
        }

        props.setProperty("mail.smtps.timeout",String.valueOf(readTimeout));
        props.setProperty("mail.transport.protocol","smtp");
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", String.valueOf(port));
        props.setProperty("mail.smtp.auth", "true");
        return props;
    }
}
