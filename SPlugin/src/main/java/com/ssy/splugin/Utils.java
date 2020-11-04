package com.ssy.splugin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

public class Utils {

    public static void extractAssert(Context context, String sourceName) {
        AssetManager assetManager = context.getAssets();
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = assetManager.open(sourceName);
            File extractFile = context.getFileStreamPath(sourceName);
            fos = new FileOutputStream(extractFile);
            byte[] bytes = new byte[1024];
            int count = 0;
            while ((count = is.read(bytes)) > 0) {
                fos.write(bytes, 0, count);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static File getPluginFromDataData(Context context, String sourceName) {
        File extractFile = context.getFileStreamPath(sourceName);
        return extractFile;
    }


    public static void hookActivityThread() {

        try {
            Class<?> ActivityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = ActivityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            Object sCurrentActivity = sCurrentActivityThreadField.get(null);
            Field mHField = ActivityThreadClass.getDeclaredField("mH");
            mHField.setAccessible(true);
            Object mH = mHField.get(sCurrentActivity);
            Field mCallbackField = Handler.class.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);
            MyCallack myCallack = new MyCallack();
            mCallbackField.set(mH, myCallack);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    public static void hookActivityManager(final Context context) {
        try {
            Class<?> activityManagerClass = Class.forName("android.app.ActivityManager");
            Class<?> iActivityManagerClass = Class.forName("android.app.IActivityManager");
            @SuppressLint("BlockedPrivateApi")
            Method getService = activityManagerClass.getDeclaredMethod("getService");
            getService.setAccessible(true);
            final Object IActivityManagerObject = getService.invoke(null);

            Object proxyIActivityTaskManager = Proxy.newProxyInstance(context.getClassLoader(), new Class[]{iActivityManagerClass}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if ("startActivity".equals(method.getName())) {
                        Intent intent = (Intent) args[2];
                        Intent proxyIntent = new Intent(context.getApplicationContext(), SubActivity.class);
                        proxyIntent.putExtra("targetIntent", intent);
                        args[2] = proxyIntent;
                    }
                    return method.invoke(IActivityManagerObject, args);
                }
            });
            Field iActivityManagerSingletonField = activityManagerClass.getDeclaredField("IActivityManagerSingleton");
            iActivityManagerSingletonField.setAccessible(true);
            Object IActivityManagerSingleton = iActivityManagerSingletonField.get(null);
            Class<?> SingletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = SingletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);

            mInstanceField.set(IActivityManagerSingleton, proxyIActivityTaskManager);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    static class MyCallack implements Handler.Callback {

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            try {
                Class<?> LaunchActivityItemClass = Class.forName("android.app.servertransaction.LaunchActivityItem");
                Object clientTransactionObj = msg.obj;

                Field mActivityCallbacksField = clientTransactionObj.getClass().getDeclaredField("mActivityCallbacks");
                mActivityCallbacksField.setAccessible(true);
                List mActivityCallbacks = (List) mActivityCallbacksField.get(clientTransactionObj);
                if (mActivityCallbacks.size() <= 0) {
                    return false;
                }
                Object mLaunchActivityItem = mActivityCallbacks.get(0);
                if (!LaunchActivityItemClass.isInstance(mLaunchActivityItem)) {
                    return false;
                }
                Field mIntnetField = LaunchActivityItemClass.getDeclaredField("mIntent");
                mIntnetField.setAccessible(true);
                Intent proxyIntent = (Intent) mIntnetField.get(mLaunchActivityItem);
                if (proxyIntent == null) {
                    return false;
                }
                Intent targetIntent = proxyIntent.getParcelableExtra("targetIntent");
                if (targetIntent != null) {
                    mIntnetField.set(mLaunchActivityItem, targetIntent);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


            return false;
        }
    }

    public static void hookDexPath(ClassLoader classLoader, File apkFile, File optDexFile) {

        try {
            Object pathListObject = RefInvoke.getFieldObject(DexClassLoader.class.getSuperclass(), classLoader, "pathList");
            Object[] dexElements = (Object[]) RefInvoke.getFieldObject(pathListObject.getClass(), pathListObject, "dexElements");
            Class<?> elementClass = dexElements.getClass().getComponentType();
            Object[] newElements = (Object[]) Array.newInstance(elementClass, dexElements.length + 1);
            DexFile dexFile = DexFile.loadDex(apkFile.getCanonicalPath(), optDexFile.getAbsolutePath(), 0);
            Object o = null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                // 构造插件Element(File file, boolean isDirectory, File zip, DexFile dexFile) 这个构造函数
                Constructor<?> constructor = elementClass.getConstructor(File.class, boolean.class, File.class, DexFile.class);
                constructor.setAccessible(true);
                o = constructor.newInstance(apkFile, false, apkFile, dexFile);
            } else {
                // 构造插件的 Element，构造函数参数：(DexFile dexFile, File file)
                Constructor<?> constructor = elementClass.getConstructor(DexFile.class, File.class);
                constructor.setAccessible(true);
                o = constructor.newInstance(dexFile, apkFile);
            }
            Object[] toAddElementArray = new Object[]{o};
            System.arraycopy(dexElements, 0, newElements, 0, dexElements.length);
            System.arraycopy(toAddElementArray, 0, newElements, dexElements.length, toAddElementArray.length);
            RefInvoke.setFieldObject(pathListObject.getClass(), pathListObject, "dexElements", newElements);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }


}
