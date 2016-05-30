package com.example.ikent;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class Daemon {

    public static final String TAG = "DaemonHandler";

	private static HandlerThread sHT;
	private static Handler sHandler;
	
	public synchronized static Handler handler() {
		if (sHT == null) {
			sHT = new HandlerThread("yycall-daemon");
			sHT.start();
		}
		
		if (sHandler == null) {
			sHandler = new CustomHandler(sHT.getLooper(), "handler");
		}
		
		return sHandler;
	}

    //底层1v1消息和群组消息的接收处理线程，包括写数据库
    private static HandlerThread sServiceHT;
    private static Handler sServicHandler;

    public synchronized static Handler serviceHandler() {
        if (sServiceHT == null) {
            sServiceHT = new HandlerThread("yycall-service");
            sServiceHT.start();
        }

        if (sServicHandler == null) {
            sServicHandler = new CustomHandler(sServiceHT.getLooper(), "serviceHandler");
        }

        return sServicHandler;
    }

    private static final class CustomHandler extends Handler {

        private String flag;
        private CustomHandler(Callback callback) {
            super(callback);
        }

        private CustomHandler(Looper looper, String flag) {
            this(looper);
            this.flag = flag;
        }

        private CustomHandler(Looper looper) {
            super(looper);
        }

        private CustomHandler(Looper looper, Callback callback) {
            super(looper, callback);
        }

        @Override
        public void dispatchMessage(Message msg) {
            long start = 0;
            start = SystemClock.elapsedRealtime();
            super.dispatchMessage(msg);
            long time = SystemClock.elapsedRealtime() - start;
            String log = msg.getCallback() + ":" + time + " ms, " + flag + " run task";
            Log.v("task", log);
        }
    }
}
