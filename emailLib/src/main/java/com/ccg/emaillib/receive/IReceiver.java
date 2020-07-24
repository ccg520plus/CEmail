package com.ccg.emaillib.receive;

import javax.mail.Message;

public interface IReceiver {
    /**
     * 读取邮件
     * @param start
     * @param readCounts
     * @return
     * @throws Exception
     */
    Message[] receive(int start,int readCounts,String folderName) throws Exception;

    /**
     * 接收订单后，如果需要更新邮件状态，那么就需要调用这个方法
     * @throws Exception
     */
    void close() throws Exception;

    /**
     * 不在使用链接，则需要销毁掉。销毁掉后，需要重新登录才可以读取
     * @throws Exception
     */
    void destroy() throws Exception;


}
