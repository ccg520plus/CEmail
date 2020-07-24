package com.ccg.emaillib.receive;

import com.ccg.emaillib.EmailConfig;
import com.ccg.emaillib.connect.IConnect;
import com.ccg.emaillib.connect.IConnectImpl;
import com.ccg.emaillib.entries.Email;
import com.sun.mail.imap.IMAPFolder;

import java.util.LinkedList;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

public class IMAPReceiver implements IReceiver {

    private boolean isOnlyReceiveUnreadEmail = false;
    private FetchProfile mFetchProfile;
    private IConnect connect;

    private IMAPFolder mImapFolder;

    IMAPReceiver(boolean isOnlyReceiveUnreadEmail, FetchProfile fetchProfile,IConnect iConnect) {
        this.isOnlyReceiveUnreadEmail = isOnlyReceiveUnreadEmail;
        mFetchProfile = fetchProfile;
        connect = iConnect;
    }

    @Override
    public Message[] receive(int start, int readCounts,String folderName) throws Exception {
        Message[] messages = null;
        if (mImapFolder == null || !mImapFolder.isOpen()){
            boolean isSuccess = false;
            MessagingException connectException = null;
            boolean isForceReset = false;
            for (int i = 0; i < 3 ; i++){
                try {
                    mImapFolder = (IMAPFolder) connect
                            .connect(isForceReset)
                            .getFolder(folderName);
                    mImapFolder.open(Folder.READ_WRITE);
                    isSuccess = true;
                    System.out.println("read success["+(i+1)+"]");
                    break;
                }catch (MessagingException e){
                    isSuccess = false;
                    connectException = e;
                    isForceReset = true;
                    System.out.println("read failed["+(i+1)+"]");
                }
            }
            if (!isSuccess){
                throw connectException;
            }
        }


        if (isOnlyReceiveUnreadEmail){
            messages = getRealUnreadImapMessage(mImapFolder.getMessages(),start,readCounts);
        }else{
            int end = start + getRealReadEmailNum(mImapFolder.getMessageCount(),readCounts) - 1;
            messages = mImapFolder.getMessages(start,end);
        }
        if (mFetchProfile != null){
            mImapFolder.fetch(messages,mFetchProfile);
        }
        return messages;
    }

    @Override
    public void close() throws MessagingException {
        if (mImapFolder != null && mImapFolder.isOpen()){
            mImapFolder.close(true);
            mImapFolder = null;
        }
    }

    @Override
    public void destroy() throws Exception {
        if (connect != null){
            connect.disconnect();
        }
    }

    /**
     * imap协议下读取邮件，获取未读订单(最多10条)
     * @param messages
     * @return
     */
    private Message[] getRealUnreadImapMessage(Message[] messages,int start,int counts){
        if (messages == null || messages.length == 0){
            return new Message[0];
        }
        LinkedList<Message> realMessages = new LinkedList<>();
        int i = 0;
        int index = 0;
        System.out.println("EMAIL SIZE :"+messages.length);
        for (Message message : messages) {
            try {
                Flags flags = messages[i].getFlags();
                //如果未读,且当前所处位置大于等于要读取的起始位置，则取出来
                if (!flags.contains(Flags.Flag.SEEN) && index >= start) {
                    System.out.println("unread.");
                    realMessages.add(message);
                    i++;
                    if (i >= counts){
                        break;
                    }
                }else{
                    System.out.println("READ");
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            index++;
        }
        return realMessages.toArray(new Message[0]);
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
}
