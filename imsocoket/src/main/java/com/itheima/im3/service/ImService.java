package com.itheima.im3.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.itheima.im3.activity.MyApp;
import com.itheima.im3.bean.QQMessage;
import com.itheima.im3.bean.QQMessageType;
import com.itheima.im3.core.QQConnection;
import com.itheima.im3.utils.ThreadUtils;


/**
 * Created by Administrator on 2016/4/14 0014.
 */
public class ImService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //必须使用监听器
    private QQConnection.OnQQMessageReceiverListener listener = new QQConnection.OnQQMessageReceiverListener() {
        @Override
        public void onReceive(final QQMessage msg) {
            if (QQMessageType.MSG_TYPE_CHAT_P2P.equals(msg.type)) {
                ThreadUtils.runInThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), "好友消息:" + msg.content, 0).show();
                    }
                });
            }

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getBaseContext(),"IM后台服务打开",0).show();
        MyApp.me.addOnQQMessageReceiveListener(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApp.me.removeOnQQMessageReceiveListener(listener);
    }
}
