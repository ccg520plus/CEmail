package com.ccg.emaillib.interfaces;

import com.ccg.emaillib.entries.Email;

import java.util.List;

public interface OnEmailResultListener {

    void onReceiveResult(List<Email> emails);

    void onSendResult();

    void onGetFolders(List<String> folderNames);

    void onFailed(Exception e);

}
