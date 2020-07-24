package com.ccg.emaillib.connect;

import javax.mail.Store;

public interface IConnect {

    <T extends Store> T connect(boolean isForceReset) throws Exception;

    void disconnect() throws Exception;

}
