package com.itheima.im3.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.im3.R;
import com.itheima.im3.bean.QQBuddyList;
import com.itheima.im3.bean.QQMessage;
import com.itheima.im3.bean.QQMessageType;
import com.itheima.im3.core.QQConnection;
import com.itheima.im3.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.itcast.server.bean.QQBuddy;

/**
 * Created by Administrator on 2016/4/14 0014.
 */
public class ContacctActivity  extends AppCompatActivity{
    @InjectView(R.id.listview)
    ListView listView;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        MyApp.me.addOnQQMessageReceiveListener(listener);
        ButterKnife.inject(this);
        Intent intent=getIntent();
        QQBuddyList temp= (QQBuddyList) intent.getSerializableExtra("list");
        System.out.println(temp.buddyList.size());
        //布局listview
        list.clear();
        list.addAll(temp.buddyList);
        //解析xml-->集合
        setAdapterOrNitify();
        //创建Adapter
        //设置给控件
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApp.me.removeOnQQMessageReceiveListener(listener);
    }

    private List<QQBuddy> list=new ArrayList<>();
    private ArrayAdapter<QQBuddy> adapter=null;

    private QQConnection.OnQQMessageReceiverListener listener=new QQConnection.OnQQMessageReceiverListener() {
        @Override
        public void onReceive(QQMessage msg) {
            if (QQMessageType.MSG_TYPE_BUDDY_LIST.equals(msg.type)){
                QQBuddyList temp=new QQBuddyList();
                temp= (QQBuddyList) temp.fromXml(msg.content);
                list.clear();
                list.addAll(temp.buddyList);
                ThreadUtils.runInThread(new Runnable() {
                    @Override
                    public void run() {
                        setAdapterOrNitify();
                        //刷新listView
                    }
                });

            }

        }
    };

    private void setAdapterOrNitify() {
        if (list.size()<1){
            return;
        }
        if (adapter==null){
            // adapter=new ArrayAdapter<QQBuddy>(上下文,行视图,数据集合);
            adapter=new ArrayAdapter<QQBuddy>(this,0,list){
                //返回师徒 显示了指定下表的数据


                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolder holder=null;
                    if (convertView==null){
                        convertView=View.inflate(getBaseContext(),R.layout.item_buddy,null);
                        holder=new ViewHolder(convertView);
                        convertView.setTag(holder);

                    }else {
                        holder= (ViewHolder) convertView.getTag();
                    }
                    QQBuddy buddy=list.get(position);
                    holder.nick.setText(buddy.nick+"");
                    holder.account.setText(buddy.account+"@qq.com");
                    //那个是自己
                    if(MyApp.username.equals(buddy.account+"")){
                        holder.nick.setText("[自己]");
                        holder.nick.setTextColor(Color.parseColor("#c80000"));
                    }else {
                        holder.nick.setTextColor(Color.parseColor("#000000"));
                    }
                    return convertView;
                }
            };
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //好友
                    QQBuddy buddy=list.get(position);
                    if (MyApp.username.equals(buddy.account+"")){
                        Toast.makeText(getBaseContext(),"不能跟自己聊天",0).show();
                    }else {
                        //进入聊天
                        Intent intent=new Intent(getBaseContext(),ChatActivity.class);
                        intent.putExtra("account",buddy.account+"");
                        intent.putExtra("nick",buddy.nick+"");
                        startActivity(intent);
                    }
                }
            });
        }
    }

    static class ViewHolder{
        @InjectView(R.id.head)
        ImageView head;
        @InjectView(R.id.nick)
        TextView nick;
        @InjectView(R.id.account)
        TextView account;
        public ViewHolder(View view){
            ButterKnife.inject(this,view);
        }


    }
}
