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
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();
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


                Class<?> clazz = null;
                try {
                    clazz = Class.forName("com.ssy.douyu.HaHaActivity");
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










}