package com.ssy.emptyapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {
    private TextView tvContent;
    AssetManager mAssetManager;
    Resources mResources;
    Resources.Theme mTheme;
    private static final String apkName = "plugin.apk";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvContent = findViewById(R.id.tv_content);

        findViewById(R.id.btn_load_plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        findViewById(R.id.btn_jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File dexFile = getFileStreamPath(apkName);
                loadResource(dexFile.getAbsolutePath());
                Class<?> clazz = null;
                try {
                    clazz = Class.forName("com.ssy.douyu.LoginActivity");
//                    clazz = Class.forName("com.ssy.emptyapplication.SecondActivity");
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, clazz);
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    @Override
    public Resources getResources() {
        if (mResources == null) {
            return super.getResources();
        } else {
            return mResources;
        }
    }

    @Override
    public AssetManager getAssets() {
        if (mAssetManager == null) {
            return super.getAssets();
        } else {
            return mAssetManager;
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

    }

    @Override
    public Resources.Theme getTheme() {
        if (mTheme == null) {
            return super.getTheme();
        } else {
            return mTheme;
        }

    }

    void loadResource(String dexPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssertPathMethod = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssertPathMethod.setAccessible(true);
            addAssertPathMethod.invoke(assetManager, dexPath);
            mAssetManager = assetManager;
            mResources = new Resources(mAssetManager, super.getResources().getDisplayMetrics(), super.getResources().getConfiguration());
            mTheme = mResources.newTheme();
            mTheme.setTo(super.getTheme());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

}