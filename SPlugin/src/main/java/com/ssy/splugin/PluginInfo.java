package com.ssy.splugin;

import android.content.pm.PackageInfo;

public class PluginInfo {
    public PackageInfo packageInfo;
    public String pluginPath;

    @Override
    public String toString() {
        return "PluginInfo{" +
                "packageInfo=" + packageInfo +
                ", pluginPath='" + pluginPath + '\'' +
                '}';
    }
}
