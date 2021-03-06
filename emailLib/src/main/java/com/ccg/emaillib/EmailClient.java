package com.ccg.emaillib;

import androidx.annotation.NonNull;

import com.ccg.emaillib.connect.IConnect;
import com.ccg.emaillib.connect.IConnectImpl;
import com.ccg.emaillib.entries.Email;
import com.ccg.emaillib.interfaces.OnEmailResultListener;
import com.ccg.emaillib.receive.IReceiverImpl;
import com.ccg.emaillib.task.AbstractTask;
import com.ccg.emaillib.task.OnThreadResultListener;
import com.ccg.emaillib.task.ReceiceTask;
import com.ccg.emaillib.task.SendTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.mail.FetchProfile;
import javax.mail.Folder;

public class EmailClient implements OnThreadResultListener {

    private EmailConfig mEmailConfig;
    private IConnect emailConnect;
    private Config mReceiveConfig;
    private Config mSendConfig;

    private AbstractTask mTask;
    //接收邮件回调
    private OnEmailResultListener onEmailResultListener;


    public EmailClient(){
        mEmailConfig = new EmailConfig();
    }

    public EmailClient setReceiveConfig(Config mReceiveConfig) {
        this.mReceiveConfig = mReceiveConfig;
        return this;
    }

    public EmailClient setSendConfig(Config mSendConfig) {
        this.mSendConfig = mSendConfig;
        return this;
    }

    public EmailClient imap(String host,int port){
        if (mReceiveConfig == null){
            mReceiveConfig = new Config();
        }
        mReceiveConfig.setHost(host);
        mReceiveConfig.setPort(port);
        mReceiveConfig.setProtocolType(EmailRecource.IMAP);
        return this;
    }

    public EmailClient pop3(String host,int port){
        if (mReceiveConfig == null){
            mReceiveConfig = new Config();
        }
        mReceiveConfig.setHost(host);
        mReceiveConfig.setPort(port);
        mReceiveConfig.setProtocolType(EmailRecource.POP3);
        return this;
    }

    public EmailClient smtp(String host,int port){
        if (mSendConfig == null){
            mSendConfig = new Config();
        }
        mSendConfig.setHost(host);
        mSendConfig.setPort(port);
        mSendConfig.setProtocolType(EmailRecource.SMTP);
        return this;
    }

    public EmailClient setUsername(String username){
        if (mEmailConfig == null){
            mEmailConfig = new EmailConfig();
        }
        mEmailConfig.setUsername(username);
        return this;
    }

    public EmailClient setPassword(String password){
        if (mEmailConfig == null){
            mEmailConfig = new EmailConfig();
        }
        mEmailConfig.setPassword(password);
        return this;
    }

    public EmailClient setReadTimeout(long readTimeout) {
        if (mEmailConfig == null){
            mEmailConfig = new EmailConfig();
        }
        mEmailConfig.setReadTimeout(readTimeout);
        return this;
    }

    public EmailClient setConnectTimeout(long connectTimeout) {
        if (mEmailConfig == null){
            mEmailConfig = new EmailConfig();
        }
        mEmailConfig.setConnectTimeout(connectTimeout);
        return this;
    }

    public EmailClient setDebug(boolean debug) {
        if (mEmailConfig == null){
            mEmailConfig = new EmailConfig();
        }
        mEmailConfig.setDebug(debug);
        return this;
    }

    public EmailClient setOnlyReceiveUnreadEmail(boolean onlyReceiveUnreadEmail) {
        if (mEmailConfig == null){
            mEmailConfig = new EmailConfig();
        }
        mEmailConfig.setOnlyReceiveUnreadEmail(onlyReceiveUnreadEmail);
        return this;
    }

    public EmailClient setEmailConfig(EmailConfig emailConfig){
        mEmailConfig = emailConfig;
        return this;
    }

    public EmailClient setConfig(Config config){
        if (mEmailConfig == null){
            mEmailConfig = new EmailConfig();
        }
        mEmailConfig.setConfig(config);
        return this;
    }



    /**
     * 登录认证
     * @return
     */
    public boolean loginAuth(){
        setConfig(mReceiveConfig);
        if (emailConnect == null){
            emailConnect = new IConnectImpl(mEmailConfig);
        }
        try {
            emailConnect.connect(false);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 异步读取邮件
     * @param start 邮件的读取开始位置
     * @param counts 要读取的最大数量
     * @param folderName 要读取的邮件文件夹名称
     * @param onEmailResultListener
     */
    public void readAsync(int start, int counts,String folderName, OnEmailResultListener onEmailResultListener){
        this.onEmailResultListener = onEmailResultListener;
        setConfig(mReceiveConfig);
        mTask = new ReceiceTask(mEmailConfig,emailConnect,start,counts,folderName,false,this);
        execute(mTask);
    }

    /**
     * 异步持续快速读取邮件
     * 让每读取解析完一条邮件立马回调
     * @param start 邮件的读取开始位置
     * @param counts 要读取的最大数量
     * @param folderName 要读取的邮件文件夹名称
     * @param onEmailResultListener
     */
    public void readingAsync(int start, int counts,String folderName, OnEmailResultListener onEmailResultListener){
        this.onEmailResultListener = onEmailResultListener;
        setConfig(mReceiveConfig);
        mTask = new ReceiceTask(mEmailConfig,emailConnect,start,counts,folderName,true,this);
        execute(mTask);
    }

    /**
     * 异步发送邮件
     * @param subTitle 邮件主题
     * @param content 邮件内容
     * @param addresses 收信地址，格式：xxxx@163.com,aaaa@163.com
     * @param onEmailResultListener
     */
    public void sendAsync(String subTitle,String content,String addresses,OnEmailResultListener onEmailResultListener){
        this.onEmailResultListener = onEmailResultListener;
        setConfig(mSendConfig);
        mTask = new SendTask(mEmailConfig,subTitle,content,addresses,this);
        execute(mTask);
    }

    /**
     * 单线程读取邮件
     * @param start 读取起始点
     * @param counts 读取长度
     * @return
     * @throws Exception
     */
    public List<Email> read(int start,int counts,String folderName) throws Exception {
        FetchProfile fetchProfile = new FetchProfile();
        fetchProfile.add(FetchProfile.Item.ENVELOPE);
        fetchProfile.add(FetchProfile.Item.FLAGS);
        fetchProfile.add(FetchProfile.Item.CONTENT_INFO);
        setConfig(mReceiveConfig);
        IReceiverImpl iReceiver = new IReceiverImpl(mEmailConfig,fetchProfile,emailConnect);
        return iReceiver.read(start, counts,folderName);
    }

    /**
     * 单线程读取
     * 此方法请在线程中调用
     * @return 账户所有文件夹列表名称
     */
    public List<String> getFolders(){
        if (emailConnect == null){
            throw new RuntimeException("please login email.");
        }
        List<String> folders = new ArrayList<>();
        try {
            Folder[] folderList = emailConnect.connect(false)
                    .getDefaultFolder()
                    .list();

            if (folderList != null){
                for (Folder folder : folderList) {
                    folders.add(folder.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return folders;
    }

    /**
     *关闭任务
     */
    public void close(){
        if (mTask != null){
            mTask.stopTask();
        }
    }

    public void destroy(){
        onEmailResultListener = null;
        close();
        execute(new Runnable() {
            @Override
            public void run() {
                if (emailConnect != null){
                    try {
                        emailConnect.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private static ThreadPoolExecutor mTaskPoolExecutor;

    static {
        int corePoolSize = Runtime.getRuntime().availableProcessors() + 1;
        mTaskPoolExecutor = new ThreadPoolExecutor(corePoolSize,
                corePoolSize * 2 + 1,
                60,
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        return new Thread(r,"task thread");
                    }
                });
    }

    /**
     * 提交线程任务
     * @param runnable
     */
    private void execute(Runnable runnable){
        if (mTaskPoolExecutor != null){
            mTaskPoolExecutor.execute(runnable);
        }
    }

    /**
     * 接收邮件线程或发送邮件线程回调
     * @param status 线程结果状态
     * @param type 该类型所属类型
     * @param obj 回调的内容
     */
    @Override
    public void onThreadResult(int status, int type, Object obj) {
        if (status == OnThreadResultListener.SUCCESS){
            success(type,obj);
        }else if (status == OnThreadResultListener.FAILED){
            failed(type,obj);
        }
    }

    private void success(int type,Object obj){
        if (type == OnThreadResultListener.RECEIVE){//接收邮件回调
            if (onEmailResultListener != null){
                onEmailResultListener.onReceiveResult((List<Email>) obj);
            }
        }else if (type == OnThreadResultListener.SEND){//发送邮件回调
            if (onEmailResultListener != null){
                onEmailResultListener.onSendResult();
            }
        }
    }

    private void failed(int type,Object obj){
//        if (type == OnThreadResultListener.RECEIVE){//接收邮件回调
            if (onEmailResultListener != null){
                onEmailResultListener.onFailed((Exception) obj);
            }
//        }else if (type == OnThreadResultListener.SEND){//发送邮件回调
//
//        }
    }
}
