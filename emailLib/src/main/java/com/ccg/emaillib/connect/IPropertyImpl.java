package com.ccg.emaillib.connect;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class IPropertyImpl implements IProperty {

    private long readTimeout = 30*1000;
    private long connectTimeout = 30*1000;
    private boolean isEnableSsl = true;
    private String protocol;// pop3/imap

    private Map<String,Object> propertiesMap;
    private int port;
    private String host;

    public IPropertyImpl(String host, int port, String protocol) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;

        if (propertiesMap == null){
            propertiesMap = new HashMap<>();
        }
    }

    public IPropertyImpl setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public IPropertyImpl setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public IPropertyImpl setProperties(Map<String, Object> propertiesMap) {
        this.propertiesMap = propertiesMap;
        return this;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setEnableSsl(boolean enableSsl) {
        isEnableSsl = enableSsl;
    }

    @Override
    public Properties getProperties() {
        Properties properties  = System.getProperties();
        if (propertiesMap != null && propertiesMap.size() > 0){
            for (Map.Entry<String,Object> entry : propertiesMap.entrySet()){
                if (entry != null){
                    properties.put(entry.getKey(),entry.getValue());
                }
            }
        }else{
            if ("smtp".equals(protocol)){
                IProperty property = new SMTPProperty(host,port,readTimeout);
                return property.getProperties();
            }
            if (isEnableSsl){
                properties.setProperty("mail."+protocol+".socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                properties.setProperty("mail."+protocol+".socketFactory.port",String.valueOf(port));
                properties.setProperty("mail."+protocol+".socketFactory.fallback", "false");
                properties.setProperty("mail."+protocol+".ssl.enable", "true");
            }
            properties.setProperty("mail."+protocol+".host", host);
            properties.setProperty("mail."+protocol+".port",String.valueOf(port));
            properties.setProperty("mail.store.protocol",protocol);
//            if ("imap".equals(protocol)){
//                properties.setProperty("mail.imap.starttls.enable", "true");
//            }
            properties.setProperty("mail."+protocol+".finalizecleanclose","true");
            properties.setProperty("mail."+protocol+".connectionpoolsize","3");//连接池最大可用连接数设置
            properties.setProperty("mail."+protocol+".connectiontimeout",String.valueOf(connectTimeout));
            properties.setProperty("mail."+protocol+".timeout",String.valueOf(readTimeout));
        }
        return properties;
    }



}
