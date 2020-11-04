package com.ssy.splugin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class DLUtils {
    public static PackageInfo getPackageInfo(Context context, String apkPath) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo pkgInfo = null;
        pkgInfo = packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        return pkgInfo;

    }
}
