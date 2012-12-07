package fr.ybo.ybotv.android.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.service.UpdateService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(YboTvApplication.TAG, "AlarmeReceiver>onReceive");
        Intent intentService = new Intent(context, UpdateService.class);
        intentService.setAction(UpdateService.ACTION_UPDATE);
        context.startService(intentService);
    }

}
