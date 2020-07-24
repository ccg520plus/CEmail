package com.ccg.emaillib.task;

public interface OnThreadResultListener {

    /**
     * status状态值
     */
    int FAILED =1;
    int SUCCESS =2;
    /**
     * type值
     */
    int RECEIVE = 3;
    int SEND = 4;

    /**
     * 线程回调结果
     * @param status 线程结果状态
     * @param type 该类型所属类型
     * @param obj 回调的内容
     */
    void onThreadResult(int status,int type,Object obj);

}
