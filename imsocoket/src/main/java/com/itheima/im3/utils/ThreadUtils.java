package com.itheima.im3.utils;


import android.os.Handler;

/**
 * Created by Administrator on 2016/4/14 0014.
 */
public class ThreadUtils {
    public static void runInThread(Runnable r){
        new Thread(r).start();
    }
    private static Handler handler=new Handler();
    public static  void runUIThread(Runnable r){
        handler.post(r);
    }
}
