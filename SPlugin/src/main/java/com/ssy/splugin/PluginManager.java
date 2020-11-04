package com.ssy.splugin;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PluginManager {
    public static volatile Resources mResources;
    public volatile static  Context mContext;
    public static List<PluginItem> pluginItemList;
    public static Object mPackageInfo;
    public static volatile AssetManager mAssetManager;
    public static volatile Resources.Theme mTheme;


    public static void init(Application application) {
        mContext =  application.getBaseContext();
        mPackageInfo = RefInvoke.getFieldObject(mContext, "mPackageInfo");
        mResources = mContext.getResources();
        AssetManager assetManager = mResources.getAssets();
        try {
            ArrayList<String> pluginPaths = new ArrayList<String>();
            String[] assetPaths = assetManager.list("");
            pluginItemList = new ArrayList<>();
            for (int i = 0; i < assetPaths.length; i++) {
                if (assetPaths[i].endsWith(".apk")) {
                    String apkName = assetPaths[i];
                    Utils.extractAssert(mContext, apkName);
                    String dexName = apkName.replace(".apk", ".dex");
                    mergeDex(apkName, dexName);
                    PluginItem pluginItem = generatePluginItem(apkName);
                    pluginItemList.add(pluginItem);
                    pluginPaths.add(pluginItem.pluginPath);
                }
            }
            loadPluginResource(pluginPaths);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.hookActivityManager(mContext);
        Utils.hookActivityThread();



    }


    private static PluginItem generatePluginItem(String apkName) {
        File file = mContext.getFileStreamPath(apkName);
        PluginItem pluginItem = new PluginItem();
        pluginItem.pluginPath = file.getAbsolutePath();
        pluginItem.packageInfo = DLUtils.getPackageInfo(mContext, pluginItem.pluginPath);
        return pluginItem;
    }

    private static void mergeDex(String apkPath, String dexPath) {
        try {
            File dexFile = mContext.getFileStreamPath(apkPath);
            File optDexFile = mContext.getFileStreamPath(dexPath);
            Utils.hookDexPath(mContext.getClassLoader(), dexFile, optDexFile);
        } catch (Exception e) {

        }
    }
     static void loadPluginResource(ArrayList<String> pluginPaths) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            Log.e("haha"," mContext.getPackageResourcePath():"+ mContext.getPackageResourcePath());


            for(String pluginPath: pluginPaths) {
                Log.e("haha"," pluginPath:"+pluginPath);
                addAssetPath.invoke(assetManager, pluginPath);
            }

            addAssetPath.invoke(assetManager, mContext.getPackageResourcePath());
            Resources newResources = new Resources(assetManager,
                    mContext.getResources().getDisplayMetrics(),
                    mContext.getResources().getConfiguration());



            RefInvoke.setFieldObject(mContext, "mResources", newResources);
            //这是最主要的需要替换的，如果不支持插件运行时更新，只留这一个就可以了
            RefInvoke.setFieldObject(mPackageInfo, "mResources", newResources);

            //需要清理mTheme对象，否则通过inflate方式加载资源会报错
            //如果是activity动态加载插件，则需要把activity的mTheme对象也设置为null
            RefInvoke.setFieldObject(mContext, "mTheme", null);

            mResources = newResources;

            mAssetManager = assetManager;
            mTheme = mResources.newTheme();
            mTheme.setTo(mContext.getTheme());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
