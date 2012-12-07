package fr.ybo.ybotv.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import fr.ybo.ybotv.android.service.BootService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, BootService.class));
    }
}
