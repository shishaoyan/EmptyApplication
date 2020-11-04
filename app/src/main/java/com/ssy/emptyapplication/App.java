package com.ssy.emptyapplication;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ssy.splugin.PluginManager;
import com.ssy.splugin.Utils;

import java.io.File;

public class App extends Application {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
            PluginManager.init(this);
    }


}
