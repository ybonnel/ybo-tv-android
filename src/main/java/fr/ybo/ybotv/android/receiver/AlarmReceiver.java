package fr.ybo.ybotv.android.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import fr.ybo.ybotv.android.service.UpdateService;
import fr.ybo.ybotv.android.util.YboTvLog;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        YboTvLog.debug("AlarmeReceiver>onReceive");
        Intent intentService = new Intent(context, UpdateService.class);
        intentService.setAction(UpdateService.ACTION_UPDATE);
        context.startService(intentService);
    }
}
