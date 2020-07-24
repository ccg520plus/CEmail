package com.ccg.emaillib.task;

import com.ccg.emaillib.EmailConfig;
import com.ccg.emaillib.connect.IConnect;
import com.ccg.emaillib.entries.Email;
import com.ccg.emaillib.receive.IReceiverImpl;

import java.util.List;

import javax.mail.FetchProfile;

public class ReceiceTask extends AbstractTask {


    private IReceiverImpl mReceiver;


    private IConnect emailConnect;
    private int start;
    private int counts;
    private String folderName;
    private boolean isQuickDisplay = false;


    public ReceiceTask(EmailConfig emailConfig,
                       IConnect connect,
                       int start,
                       int counts,
                       String folderName,
                       boolean isQuickDisplay,
                       OnThreadResultListener onThreadResultListener){
        this(emailConfig,onThreadResultListener);
        this.emailConnect = connect;
        this.start = start;
        this.counts = counts;
        this.folderName = folderName;
        this.isQuickDisplay = isQuickDisplay;
    }

    private ReceiceTask(EmailConfig emailConfig,OnThreadResultListener onThreadResultListener) {
        super(emailConfig,onThreadResultListener);
    }

    @Override
    public void runTask(EmailConfig emailConfig) {
        //读取初始化
        initReceive(emailConfig);
        //开始读取
        try {
            List<Email> emails = null;
            //如果需要快速显示读取邮件
            if (isQuickDisplay){
                if (start == 0){
                    start = 1;
                }
                for (int i = 1 ; i <= counts ; i++){
                    emails = mReceiver.read(start,1,folderName);
                    start++;
                    onThreadResult(OnThreadResultListener.SUCCESS,
                            OnThreadResultListener.RECEIVE,
                            emails);
                    if (emails.size() == 0){
                        break;
                    }
                }
                closeFolder();
            }else{
                emails = mReceiver.read(start,counts,folderName);
                closeFolder();
                onThreadResult(OnThreadResultListener.SUCCESS,
                        OnThreadResultListener.RECEIVE,
                        emails);
            }
        } catch (Exception e) {
            onThreadResult(OnThreadResultListener.FAILED,
                    OnThreadResultListener.RECEIVE,
                    e);
        }
    }

    @Override
    public void stopTask() {
        closeFolder();
        onThreadResult(OnThreadResultListener.FAILED,
                OnThreadResultListener.RECEIVE,
                new RuntimeException("read timeout."));
    }


    private void initReceive(EmailConfig emailConfig){
        if (emailConnect == null){
            throw new RuntimeException("please login.");
        }
        FetchProfile fetchProfile = new FetchProfile();
        fetchProfile.add(FetchProfile.Item.ENVELOPE);
        fetchProfile.add(FetchProfile.Item.FLAGS);
        fetchProfile.add(FetchProfile.Item.CONTENT_INFO);
        mReceiver = new IReceiverImpl(emailConfig,fetchProfile,emailConnect);
    }

    private void closeFolder(){
        if (mReceiver != null){
            try {
                mReceiver.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
