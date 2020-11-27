package com.ssy.splugin;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

public class SPlugin {

    private static final String TAG = "SPlugin";

    public static void hook(Context base) {
        Object mPackageInfo = RefInvoke.getFieldObject(base, "mPackageInfo");
        SPluginClassLoader sPluginClassLoader = new SPluginClassLoader(base.getClassLoader().getParent(), base.getClassLoader());
        RefInvoke.setFieldObject(mPackageInfo, "mClassLoader", sPluginClassLoader);
        Thread.currentThread().setContextClassLoader(sPluginClassLoader);
    }

    public static PluginInfo install(Context context, String path) {
        PluginInfo pluginInfo = installPluginApk(context, path);
        addToHost(context, pluginInfo);
        return pluginInfo;
    }

    private static void addToHost(Context context, PluginInfo pluginInfo) {
        addDex(context, pluginInfo);
        addResource(pluginInfo);
    }

    private static void addResource(PluginInfo pluginInfo) {
    }

    private static void addDex(Context context, PluginInfo pluginInfo) {
        String apkPath = pluginInfo.pluginPath;
        String[] strs = apkPath.split("/");

        String dexPath = strs[strs.length-1].replace(".apk", ".dex");
        DexClassLoader dexClassLoader = new DexClassLoader(apkPath, dexPath, null, context.getClassLoader().getParent());
        SPluginClassLoader.addDexClassLoader(dexClassLoader);

    }

    private static PluginInfo installPluginApk(Context context, String path) {
        if (path.contains("assert/")) {
            path = path.replace("assert/", "");
            path = extractPluginFromApk(context, path);
        }
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.pluginPath = path;
        PackageManager packageManager = context.getPackageManager();

        pluginInfo.packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        return pluginInfo;
    }

    private static String extractPluginFromApk(Context context, String path) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        File outputFile = null;
        try {
            inputStream = context.getAssets().open(path);
            outputFile = context.getFileStreamPath(path);
            fileOutputStream = new FileOutputStream(outputFile);
            byte[] bytes = new byte[1024];
            int count = 0;
            while ((count = inputStream.read(bytes)) > 0) {
                fileOutputStream.write(bytes, 0, count);
            }
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                fileOutputStream.close();
                if (outputFile != null) {
                    return outputFile.getAbsolutePath();
                } else {
                    return "";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
