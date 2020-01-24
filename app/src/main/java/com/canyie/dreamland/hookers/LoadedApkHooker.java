package com.canyie.dreamland.hookers;

import android.content.Context;
import android.util.Log;

import com.canyie.dreamland.core.Dreamland;
import com.swift.sandhook.SandHook;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;
import com.swift.sandhook.annotation.HookReflectClass;
import com.swift.sandhook.annotation.MethodParams;
import com.swift.sandhook.annotation.ThisObject;
import com.swift.sandhook.xposedcompat.XposedCompat;

import java.io.File;
import java.lang.reflect.Method;

import mirror.android.app.ActivityThread;

/**
 * Created by canyie on 2019/11/13.
 */
@SuppressWarnings("unused")
@HookReflectClass("android.app.LoadedApk")
public final class LoadedApkHooker {
    private static boolean classLoaderReady;
    @HookMethodBackup("getClassLoader")
    @MethodParams({})
    private static Method getClassLoaderBackup;

    @HookMethod("getClassLoader")
    @MethodParams({})
    public static ClassLoader getClassLoader(@ThisObject Object loadedApk) throws Throwable {
        ClassLoader classLoader = (ClassLoader) SandHook.callOriginByBackup(getClassLoaderBackup, loadedApk);
        try {
            if (!classLoaderReady) {
                classLoaderReady = true;
                Log.i(Dreamland.TAG, "AppClassLoader is already!");
                Dreamland d = Dreamland.getInstance();
                d.initClassLoader(Dreamland.class.getClassLoader());
                if (d.canLoadXposedModules()) {
                    d.loadXposedModules();
                }
            }
        } catch (Throwable e) {
            try {
                Log.e(Dreamland.TAG, "Can't prepare ClassLoader.", e);
            } catch (Throwable ignored) {
            }
        }
        return classLoader;
    }
}
