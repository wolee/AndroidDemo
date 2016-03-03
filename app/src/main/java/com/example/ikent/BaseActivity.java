package com.example.ikent;

import android.support.v7.app.AppCompatActivity;

import java.util.HashSet;

/**
 * Created by lijianfeng on 16/2/26 下午2:01.
 */
public class BaseActivity extends AppCompatActivity {
    public static final String TAG = "BaseActivity";

    private static int sVisibleActivityCount = 0;

    @Override
    protected void onResume() {
        super.onResume();
        sVisibleActivityCount++;

        if(sVisibleActivityCount == 1) {
            for(IApplicationVisibileChangeListener l : mAppVisibleChangeListeners) {
                if(l != null) {
                    l.onApplicationVisbleChange(true);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sVisibleActivityCount--;


        if(sVisibleActivityCount <= 0 && mAppVisibleChangeListeners.size() > 0) {
            for(IApplicationVisibileChangeListener l : mAppVisibleChangeListeners) {
                if(l != null) {
                    l.onApplicationVisbleChange(false);
                }
            }
        }
    }


    public interface IApplicationVisibileChangeListener{
        void onApplicationVisbleChange(boolean visible);
    }

    private static HashSet<IApplicationVisibileChangeListener>
            mAppVisibleChangeListeners = new HashSet<IApplicationVisibileChangeListener>();

    public static void addApplicationVisibileChangeListener(IApplicationVisibileChangeListener l) {
        if(!mAppVisibleChangeListeners.contains(l)) {
            mAppVisibleChangeListeners.add(l);
        }
    }

    public static void removeApplicationVisibileChangeListener(IApplicationVisibileChangeListener l) {
        mAppVisibleChangeListeners.remove(l);
    }

    public static boolean isApplicationVisible(){
        return sVisibleActivityCount > 0;
    }
}
