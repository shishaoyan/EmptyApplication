package com.ssy.douyu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.ssy.splugin.PluginManager;

public class HaHaActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ha_ha);
        Log.e("haha","activity_login"+R.layout.activity_login);
        Log.e("haha","activity_login"+R.layout.activity_ha_ha);
        Log.e("haha","activity_main"+R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("haha","onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("haha","onStop");
    }

    @Override
    public Resources getResources() {
        return PluginManager.mResources;
    }

    @Override
    public Resources.Theme getTheme() {

        return PluginManager.mTheme;
    }

    @Override
    public AssetManager getAssets() {
        return PluginManager.mAssetManager;
    }
}