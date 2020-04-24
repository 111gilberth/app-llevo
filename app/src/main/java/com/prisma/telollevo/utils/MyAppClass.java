package com.prisma.telollevo.utils;

import android.content.Context;

import com.orm.SugarApp;

//import com.onesignal.OneSignal;

public class MyAppClass extends SugarApp {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
      mContext = this;

    }

    public static Context getContext(){
        return mContext;
    }
}
