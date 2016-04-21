package com.itheima.im3.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itheima.im3.R;
import com.itheima.im3.bean.QQBuddyList;
import com.itheima.im3.bean.QQMessage;
import com.itheima.im3.bean.QQMessageType;
import com.itheima.im3.core.QQConnection;
import com.itheima.im3.service.ImService;
import com.itheima.im3.utils.ThreadUtils;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/4/14 0014.
 */
public class LoginActivity extends Activity {
    private EditText account;
    private EditText pwd;



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        account= (EditText) findViewById(R.id.account);
        pwd= (EditText) findViewById(R.id.pwd);



        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {

                try {
                    //初始化消息通道
                    conn=new QQConnection();
                    conn.addOnQQMessageReceiveListener(listener);
                    conn.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        conn.removeOnQQMessageReceiveListener(listener);
    }

    private QQConnection.OnQQMessageReceiverListener listener=new QQConnection.OnQQMessageReceiverListener() {
        @Override
        public void onReceive(QQMessage msg) {
            System.out.println(msg.toXml());
            if (QQMessageType.MSG_TYPE_BUDDY_LIST.equals(msg.type)){
                final QQBuddyList list=new QQBuddyList();
                final QQBuddyList list2= (QQBuddyList) list.fromXml(msg.content);
                ThreadUtils.runInThread(new Runnable() {
                    @Override
                    public void run() {
                        MyApp.me=conn;
                        MyApp.username=username;
                        MyApp.account=username+"@qq.com";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(),"登录成功",0).show();

                            }
                        });

                        startService(new Intent(getBaseContext(),ImService.class));
                        Intent intent=new Intent(getBaseContext(),ContacctActivity.class);
                        intent.putExtra("list",list2);
                        startActivity(intent);
                        finish();
                    }
                });
            }else{

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(),"登录失败",0).show();
                    }
                });



            }
        }
    };

    QQConnection conn;
    String username;
    String password;


    public void login (View view){
        username=account.getText().toString().trim();
        password=pwd.getText().toString().trim();
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                QQMessage msg=new QQMessage();
                msg.type=QQMessageType.MSG_TYPE_LOGIN;
                msg.content=username+"#"+password;
                String xml=msg.toXml();
                if (conn!=null){
                    try {
                        conn.sendMessage(xml);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}
