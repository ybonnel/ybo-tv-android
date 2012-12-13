package fr.ybo.ybotv.android.util;

import android.util.Log;
import fr.ybo.ybotv.android.YboTvApplication;

public class YboTvLog {

    public static boolean DEBUG = true;
    public static boolean TO_SYSOUT = false;

    public static void debug(String message) {
        if (DEBUG) {
            if (TO_SYSOUT) {
                System.out.println(message);
            } else {
                Log.d(YboTvApplication.TAG, message);
            }
        }
    }

    public static void info(String message) {
        if (TO_SYSOUT) {
            System.out.println(message);
        } else {
            Log.i(YboTvApplication.TAG, message);
        }
    }

    public static void error(String message, Throwable throwable) {
        if (TO_SYSOUT) {
            System.err.println(message);
            throwable.printStackTrace(System.err);
        } else {
            Log.e(YboTvApplication.TAG, message);
            Log.e(YboTvApplication.TAG, Log.getStackTraceString(throwable));
        }
    }

    public static void error(String message) {
        if (TO_SYSOUT) {
            System.err.println(message);
        } else {
            Log.e(YboTvApplication.TAG, message);
        }
    }




}
