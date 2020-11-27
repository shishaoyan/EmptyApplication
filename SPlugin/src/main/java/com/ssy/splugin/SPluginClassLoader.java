package com.ssy.splugin;

import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class SPluginClassLoader extends PathClassLoader {

    static List<DexClassLoader> dexClassLoaders;
    ClassLoader originPathClassLoader;

    public SPluginClassLoader(ClassLoader parent,ClassLoader origin) {
        super("","",parent);
        dexClassLoaders = new ArrayList<>();
        originPathClassLoader = origin;
    }



    public static void addDexClassLoader(DexClassLoader dexClassLoader) {
        dexClassLoaders.add(dexClassLoader);
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }


    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class clazz = null;
        try {
            clazz = originPathClassLoader.loadClass(name);
        } catch (ClassNotFoundException e) {

        }
        if (clazz != null) {
            return clazz;
        }
        for (DexClassLoader dexClassLoader : dexClassLoaders) {
            try {
                clazz = dexClassLoader.loadClass(name);
            } catch (ClassNotFoundException e) {

            }
        }
        if (clazz != null) {
            return clazz;
        } else {
            throw new ClassNotFoundException(name + " in " + this);
        }

    }
}
