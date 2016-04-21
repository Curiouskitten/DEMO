package com.itheima.im3.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.itheima.im3.R;
import com.itheima.im3.utils.ThreadUtils;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
            //布局ImageView
        //等待3000
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //检查更新
                    //初始
                    Thread.sleep(3000);
                    startActivity(new Intent(getBaseContext(),LoginActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
