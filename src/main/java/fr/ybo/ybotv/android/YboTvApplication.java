package fr.ybo.ybotv.android;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import fr.ybo.ybotv.android.activity.CeSoirActivity;
import fr.ybo.ybotv.android.activity.NowActivity;
import fr.ybo.ybotv.android.activity.ParChaineActivity;
import fr.ybo.ybotv.android.activity.ProgrammeGridActivity;
import fr.ybo.ybotv.android.database.YboTvDatabase;
import fr.ybo.ybotv.android.receiver.AlarmReceiver;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

@ReportsCrashes(formKey = "dDBLNm5ZR2dLWFhyQTV0dDMtTDdFZVE6MQ")
public class YboTvApplication extends Application {


    public static final String TAG = "YboTv";

    private final static Set<String> EMULATORS_PRDODUCT = new HashSet<String>() {{
        add("full_x86");
        add("google_sdk");
        add("sdk");
    }};

    private YboTvDatabase database;

    public static boolean isEmulator() {
        Log.d(YboTvApplication.TAG, "BuildProduct : " + Build.PRODUCT);
        return EMULATORS_PRDODUCT.contains(Build.PRODUCT);
    }

    public boolean isSnapshot() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            return (info.versionName.endsWith("SNAPSHOT"));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isTablet() {
        boolean xlarge = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    @Override
    public void onCreate() {
        if (!isEmulator() && !isSnapshot()) {
            ACRA.init(this);
        }
        super.onCreate();
        database = new YboTvDatabase(this);
        /*StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build());*/
    }

    public YboTvDatabase getDatabase() {
        return database;
    }

    private static enum SCREEN {
        NOW(NowActivity.class),
        CE_SOIR(CeSoirActivity.class),
        PAR_CHAINE(ParChaineActivity.class),
        GRID(ProgrammeGridActivity.class);

        private Class<? extends Activity> activity;

        public static SCREEN formString(String value) {
            if (value == null) {
                return null;
            }
            for (SCREEN oneValue : values()) {
                if (oneValue.name().equals(value)) {
                    return oneValue;
                }
            }
            return null;
        }

        private SCREEN(Class<? extends Activity> activity) {
            this.activity = activity;
        }

        public Class<? extends Activity> getActivity() {
            return activity;
        }
    }

    public Class<? extends Activity> getDefaultActivity() {
        SharedPreferences prefs = getDefaultPreferences();
        SCREEN defaultScreen = SCREEN.formString(prefs.getString("YboTv_defaultScreen", SCREEN.NOW.name()));
        if (defaultScreen == SCREEN.GRID && Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return SCREEN.NOW.getActivity();
        }
        return defaultScreen == null ? SCREEN.NOW.getActivity() : defaultScreen.getActivity();
    }

    public SharedPreferences getDefaultPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    private String versionInPref = null;

    public String getVersionInPref() {
        if (versionInPref == null) {
            SharedPreferences prefs = getDefaultPreferences();
            versionInPref = prefs.getString("ybo-tv.version", "0");
        }
        return versionInPref;
    }

    public void updateVersionInPref(String currentVersion) {
        SharedPreferences prefs = getDefaultPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ybo-tv.version", currentVersion);
        editor.commit();
        versionInPref = currentVersion;
    }

    public void setRecurringAlarm() {
        Log.d(YboTvApplication.TAG, "setRecurringAlarm");
        Calendar updateTime = Calendar.getInstance();
        Intent alarm = new Intent(this, AlarmReceiver.class);
        PendingIntent recurringAlarm = PendingIntent.getBroadcast(this, 0, alarm, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), AlarmManager.INTERVAL_HOUR * 3, recurringAlarm);
    }

}
