package com.ssy.emptyapplication;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ssy.splugin.PluginInfo;
import com.ssy.splugin.PluginManager;
import com.ssy.splugin.RefInvoke;
import com.ssy.splugin.SPlugin;
import com.ssy.splugin.SPluginClassLoader;
import com.ssy.splugin.Utils;

import java.io.File;
import java.io.IOException;

import dalvik.system.DexClassLoader;

public class App extends Application {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // PluginManager.init(this);
        SPlugin.hook(base);

        PluginInfo pluginInfo = SPlugin.install(base, "assert/plugin.apk");
        Log.d("haha", "plugin:" + pluginInfo.toString());


    }


}
