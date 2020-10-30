package com.ssy.emptyapplication;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class App extends Application {

    private static final String apkName = "plugin.apk";
    private static final String dexName = "plugin.dex";


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();


    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Utils.extractAssert(base, apkName);
        File dexFile = getFileStreamPath(apkName);
        File optDexFile = getFileStreamPath(dexName);
        Utils.hookDexPath(getClassLoader(),dexFile,optDexFile);

//        Utils.hookActivityManager(base);
//        Utils.hookActivityThread();


    }






}
