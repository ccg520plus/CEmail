package com.ccg.emaillib.task;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.ccg.emaillib.EmailConfig;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTask implements Runnable {
    //定时线程池
    private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    static {
        int coreSize = Runtime.getRuntime().availableProcessors() + 1;
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(coreSize, new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                System.out.println("new thread----timer");
                return new Thread(r,"receive thread");
            }
        });
    }
    //定时任务线程
    private Runnable mTimerRunnable ;
    private OnThreadResultListener onThreadResultListener;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ScheduledFuture<?> scheduledFuture;

    private EmailConfig mEmailConfig;

    AbstractTask(EmailConfig emailConfig,OnThreadResultListener onThreadResultListener) {
        mEmailConfig = emailConfig;
        this.onThreadResultListener = onThreadResultListener;
        mTimerRunnable = new Runnable() {
            @Override
            public void run() {
//                if (!isComplete){
                    System.out.println("timeout --> stop task");
                    stopTask();
//                }
            }
        };
    }


    public EmailConfig getEmailConfig(){
        return mEmailConfig;
    }

    @Override
    public void run() {
        startTimer();
        runTask(getEmailConfig());
        stopTimer();
    }

    private void startTimer(){
        if (scheduledThreadPoolExecutor != null && mTimerRunnable != null){
            System.out.println("start timer-----timer");
            scheduledFuture = scheduledThreadPoolExecutor.schedule(mTimerRunnable,
                    mEmailConfig.getConnectTimeout() + mEmailConfig.getReadTimeout() + 10 * 1000,
                    TimeUnit.MILLISECONDS);
        }
    }

    private void stopTimer(){
        if (scheduledFuture != null && !scheduledFuture.isCancelled()){
            System.out.println("cancel timer");
            scheduledFuture.cancel(true);
        }
        if (scheduledThreadPoolExecutor != null && mTimerRunnable != null){
            System.out.println("stop timer-----timer");
            scheduledThreadPoolExecutor.remove(mTimerRunnable);
        }
    }

    void onThreadResult(final int status, final int type, final Object object){
        if (onThreadResultListener != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onThreadResultListener.onThreadResult(status,type,object);
                }
            });
        }
    }

    public abstract void runTask(EmailConfig emailConfig);
    public abstract void stopTask();
}
