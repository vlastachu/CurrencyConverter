package me.vlastachu.currencyconverter;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    private static Context ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
    }

    public static Context getContext(){
        return ctx;
    }
}
