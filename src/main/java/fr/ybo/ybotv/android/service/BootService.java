package fr.ybo.ybotv.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import fr.ybo.ybotv.android.YboTvApplication;

public class BootService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ((YboTvApplication)getApplication()).setRecurringAlarm();
        stopSelf();
        return START_STICKY;
    }
}
