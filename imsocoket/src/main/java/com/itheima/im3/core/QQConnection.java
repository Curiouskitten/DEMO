package com.itheima.im3.core;

import com.itheima.im3.bean.QQMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/14 0014.
 */
//1.链接到服务端 2.发送消息 3.接收消息 4.断开与服务器的连接
public class QQConnection extends Thread {
    public static final String HOST = "192.168.3.34";
    public static final int PORT = 5225;
    private Socket client;
    private DataInputStream reader = null;
    private DataOutputStream writer = null;

    //声明接口
    public static interface OnQQMessageReceiverListener {
        //抽象方法
        //定义参数
        public void onReceive(QQMessage msg);
    }

    private List<OnQQMessageReceiverListener> listeners = new ArrayList<OnQQMessageReceiverListener>();

    //添加方法 移除
    public void addOnQQMessageReceiveListener(OnQQMessageReceiverListener listener) {
        listeners.add(listener);
    }

    public void removeOnQQMessageReceiveListener(OnQQMessageReceiverListener listener) {
        listeners.remove(listener);
    }

    private boolean flag = true;

    @Override
    public void run() {
        super.run();
        while (flag) {

            try {
                String xml = reader.readUTF();
                QQMessage msg = new QQMessage();
                msg = (QQMessage) msg.fromXml(xml);
                if (msg != null) {
                    for (OnQQMessageReceiverListener listener : listeners
                            ) {
                        listener.onReceive(msg);

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void connect() throws IOException {
        if (client == null) {
            client = new Socket(HOST, PORT);
            reader = new DataInputStream(client.getInputStream());
            writer = new DataOutputStream(client.getOutputStream());
            flag = true;
            start();
        }

    }

    public void disconnect() {
        if (client != null) {
            flag = false;
            stop();
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {

                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void sendMessage(String xml) throws IOException {
        writer.writeUTF(xml);
        writer.flush();
    }
    public void sendMessage(QQMessage msg) throws IOException {
        writer.writeUTF(msg.toXml());
        writer.flush();
    }
}
