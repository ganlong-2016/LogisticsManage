package com.drkj.logisticsmanage;

import android.app.Application;

/**
 * Created by ganlong on 2017/12/1.
 */

public class BaseApplication extends Application {

    private static BaseApplication instance;
    private String phoneNumber;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static BaseApplication getInstance(){
        return instance;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }
}
