package com.great.faintest;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;

import com.tencent.mmkv.MMKV;

import pang.AdE;
/**
 * Date：2025/9/25
 * Describe:
 * com.ak.impI.Core
 */
public class Core {

    public static long insAppTime = 0L; //installAppTime
    private static final MMKV mmkv = MMKV.defaultMMKV();
    public static Application mApp;


    // todo  入口 记得做差异化
    public static void a(Object ctx) {
        mApp = (Application) ctx;
        pE("test_d_load");
        inIf(mApp);
        AdE.a2();
    }
    public static void pE(String name, String value) {
        boolean canRetry;
        switch (name) {
            case "config_G":
            case "cf_fail":
            case "pop_fail":
            case "advertise_limit":
                canRetry = true;
                break;
            default:
                canRetry = false;
                break;
        }
        b.B.b(canRetry,name, "string",value);
    }
    public static void pE(String string) {
        pE(string, null);
    }


    public static void postAd(String string) {
        a.A.a(string);
    }


    public static long finishAllActivities() {
        c.C.c();
        return 0L;
    }


    public static String getStr(String key) {
        return mmkv.decodeString(key, "");
    }

    public static void saveC(String ke, String con) {
        mmkv.encode(ke, con);
    }

    public static int getInt(String key) {
        return mmkv.decodeInt(key, 0);
    }

    public static void saveInt(String key, int i) {
        mmkv.encode(key, i);
    }

    private static void inIf(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            insAppTime = pi.firstInstallTime;
        } catch (Exception ignored) {
        }
    }
}
