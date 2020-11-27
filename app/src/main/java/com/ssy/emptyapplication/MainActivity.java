package com.ssy.emptyapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ssy.splugin.PluginManager;
import com.ssy.splugin.RefInvoke;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();

    Button button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.btn_jump);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Class clazz = null;
                try {
                    ClassLoader classLoader =getApplicationContext().getClassLoader();
                    clazz = classLoader.loadClass("com.ssy.douyu.HaHaActivity");
                   Method getHahaMethod = clazz.getDeclaredMethod("getHaha");
                    getHahaMethod.setAccessible(true);
                   String haha = (String) getHahaMethod.invoke(null);
                    Log.e("haha","clazz:"+clazz.getName());
                    Log.e("haha","haha:"+haha);
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, clazz);
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }


//               // String activityName = PluginManager.pluginItemList.get(0).packageInfo.packageName + ".HaHaActivity";
//                String activityName = "com.ssy.douyu.HaHaActivity";
//
//                try {
//                    Intent intent = new Intent();
//                    intent.setClass(MainActivity.this, Class.forName(activityName));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }

            }
        });
    }


}