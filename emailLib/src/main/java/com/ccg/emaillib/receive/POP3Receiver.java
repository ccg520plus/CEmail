package com.ccg.emaillib.receive;

import com.ccg.emaillib.EmailConfig;
import com.ccg.emaillib.connect.IConnect;
import com.ccg.emaillib.connect.IConnectImpl;
import com.sun.mail.pop3.POP3Folder;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

public class POP3Receiver implements IReceiver{


    private FetchProfile mFetchProfile;
    private IConnect connect;

    private POP3Folder mPop3Folder;

    POP3Receiver(FetchProfile fetchProfile, IConnect iConnect) {
        this.mFetchProfile = fetchProfile;
        connect = iConnect;
    }

    @Override
    public Message[] receive(int start, int readCounts,String folderName) throws Exception {
        Message[] messages = null;
        if (mPop3Folder == null || !mPop3Folder.isOpen()){
            boolean isSuccess = false;
            MessagingException connectException = null;
            boolean isForceReset = false;
            for (int i = 0 ; i < 3 ; i++){
                try{
                    mPop3Folder = (POP3Folder) connect
                            .connect(isForceReset)
                            .getFolder(folderName);
                    mPop3Folder.open(Folder.READ_WRITE);
                    System.out.println("read success["+(i+1)+"]");
                    isSuccess = true;
                    break;
                }catch (MessagingException e){
                    connectException = e;
                    isSuccess =false;
                    isForceReset = true;
                    System.out.println("read failed["+(i+1)+"]");
                }
            }
            //如果三次都失败的话，那么就抛出异常
            if (!isSuccess){
                throw connectException;
            }
        }
        int end = start + getRealReadEmailNum(mPop3Folder.getMessageCount(),readCounts) - 1;
        messages = mPop3Folder.getMessages(start,end);
        if (mFetchProfile != null){
            mPop3Folder.fetch(messages,mFetchProfile);
        }
        return messages;
    }

    @Override
    public void close() throws MessagingException {
        if (!checkClose()){
            mPop3Folder.close(true);
            mPop3Folder = null;
        }
    }

    @Override
    public void destroy() throws Exception {
        if (connect != null){
            connect.disconnect();
        }
    }

    /**
     * 获取真实读取的邮件条数
     * @param realEmailNum
     * @return
     */
    private int getRealReadEmailNum(int realEmailNum,int counts){
        if (realEmailNum > counts){
            return counts;
        }else{
            return realEmailNum;
        }
    }

    private boolean checkClose(){
        return mPop3Folder == null || !mPop3Folder.isOpen();
    }




}
