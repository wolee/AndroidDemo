package com.example.ikent.demo.messenger.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.example.ikent.Daemon;

public class MyService extends Service {

    public static final int MSG_SUM = 0x110;
    private final HandlerThread mServiceHT;
    private final Messenger mMessenger;
    public MyService() {

        mServiceHT = new HandlerThread("service");
        mServiceHT.start();

        mMessenger = new Messenger(new Handler(mServiceHT.getLooper()) {
            @Override
            public void dispatchMessage(Message msgfromClient) {
                Message msgToClient = Message.obtain(msgfromClient);//返回给客户端的消息
                switch (msgfromClient.what)
                {
                    //msg 客户端传来的消息
                    case MSG_SUM:
                        msgToClient.what = MSG_SUM;
                        try
                        {
                            //模拟耗时
                            Thread.sleep(2000);
                            msgToClient.arg2 = msgfromClient.arg1 + msgfromClient.arg2;
                            msgfromClient.replyTo.send(msgToClient);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                super.dispatchMessage(msgfromClient);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
       return mMessenger.getBinder();
    }
}
