package com.itheima.im3.activity;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.im3.R;
import com.itheima.im3.bean.QQMessage;
import com.itheima.im3.bean.QQMessageType;
import com.itheima.im3.core.QQConnection;
import com.itheima.im3.utils.ThreadUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/4/14 0014.
 */
public class ChatActivity extends AppCompatActivity {
    private String toAccount = "";
    private String toNick = "";
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.chatlistview)
    ListView chatlistview;
    @InjectView(R.id.input)
    EditText input;


    private List<QQMessage> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.me.addOnQQMessageReceiveListener(listener);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        Intent intent=getIntent();
        toAccount=intent.getStringExtra("account");
        toNick=intent.getStringExtra("nick");
        title.setText("与"+toNick+"聊天中...");
        setAdapterOrNitify();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApp.me.removeOnQQMessageReceiveListener(listener);
    }

    private QQConnection.OnQQMessageReceiverListener listener = new QQConnection.OnQQMessageReceiverListener() {

        @Override
        public void onReceive(final QQMessage msg) {
            if (QQMessageType.MSG_TYPE_CHAT_P2P.equals(msg.type)) {
                ThreadUtils.runUIThread(new Runnable() {
                    @Override
                    public void run() {
                        list.add(msg);
                        setAdapterOrNitify();//设置 刷新  --》多种行视图

                    }
                });
            }

        }

    };
    @OnClick(R.id.send)
    public void send(View view){
        final String body=input.getText().toString().trim();
        if ("".equals(body)){
            Toast.makeText(getBaseContext(),"消息不以为空",0).show();
            return;
        }
        input.setText("");
        final QQMessage msg=new QQMessage();
        msg.type=QQMessageType.MSG_TYPE_CHAT_P2P;
        msg.content=body;
        msg.from=Long.parseLong(MyApp.username);
        msg.to=Long.parseLong(toAccount);
        list.add(msg);
        setAdapterOrNitify();

        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                //通道
                try {
                    MyApp.me.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }



    private ArrayAdapter<QQMessage> adapter=null;
    private void setAdapterOrNitify() {
        if (list.size()<1){
                return;
        }
        if (adapter==null){
            adapter=new ArrayAdapter<QQMessage>(this,0,list){
                //返回列表 行视图的种类
                @Override
                public int getViewTypeCount() {
                    return 2;
                }
                //根据position 返回类型 0发送 from=me 1 接收 from !=me


                @Override
                public int getItemViewType(int position) {
                    QQMessage msg=list.get(position);
                    long formId=Long.parseLong(MyApp.username);
                    if(formId==msg.from){
                        return 0;
                    }
                    return 1;
                }
                //返回行视图 显示指定下标的数据

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    int type=getItemViewType(position);
                    if (type==0){
                        ViewHolder holder1=null;
                        if (convertView==null){
                            convertView=View.inflate(getBaseContext(),R.layout.item_chat_send,null);
                            holder1=new ViewHolder(convertView);
                            convertView.setTag(holder1);
                        }else{
                            holder1= (ViewHolder) convertView.getTag();
                        }
                        QQMessage msg=list.get(position);
                        holder1.content.setText(msg.content);
                        holder1.time.setText(msg.sendTime);
                        return  convertView;
                    }else if (type==1){
                        ViewHolder holder2=null;
                        if (convertView==null){
                            convertView=View.inflate(getBaseContext(),R.layout.item_chat_receive,null);
                            holder2=new ViewHolder(convertView);
                            convertView.setTag(holder2);
                        }else {
                            holder2= (ViewHolder) convertView.getTag();
                        }
                       QQMessage msg=list.get(position);
                        holder2.content.setText(msg.content);
                        holder2.time.setText(msg.sendTime);
                        return  convertView;
                    }
                    return  convertView;
                }
            };
            chatlistview.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
        if (list.size()>=1){
            chatlistview.setSelection(list.size()-1);
        }
        //初始化

    }


    static class ViewHolder{
        @InjectView(R.id.time)
        TextView time;
        @InjectView(R.id.content)
        TextView content;
        @InjectView(R.id.head)
        ImageView head;

        public ViewHolder(View view){
            ButterKnife.inject(this,view);

        }
    }
}
