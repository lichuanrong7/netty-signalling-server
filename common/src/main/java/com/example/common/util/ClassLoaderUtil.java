package com.example.common.util;

public class ClassLoaderUtil {

    public static void showLoader4Class(Class clazz){
        ClassLoader loader = clazz.getClassLoader();
        showLoaderTree(loader);
    }

    public static void showLoaderTree(ClassLoader loader){
        while (loader!=null){
            Logger.info(loader.toString());
            loader = loader.getParent();
        }
    }
}
