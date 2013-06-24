package fr.ybo.ybotv.android.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.database.YboTvDatabase;
import fr.ybo.ybotv.android.exception.YboTvErreurReseau;
import fr.ybo.ybotv.android.modele.LastUpdate;
import fr.ybo.ybotv.android.util.TacheAvecGestionErreurReseau;
import fr.ybo.ybotv.android.util.TimeUnit;
import fr.ybo.ybotv.android.util.UpdateChannels;
import fr.ybo.ybotv.android.util.UpdateProgrammes;

import java.util.Date;

public class UpdateService extends Service  {

    public static final String ACTION_UPDATE = "fr.ybo.ybotv.android.action.UPDATE";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings("RefusedBequest")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_UPDATE.equals(intent.getAction())) {
            update();
        }
        return START_STICKY;
    }

    @SuppressWarnings("unchecked")
    private void update() {
        Log.d(YboTvApplication.TAG, "UpdateService.update");
        final YboTvDatabase database = ((YboTvApplication)getApplication()).getDatabase();
        LastUpdate lastUpdate = database.selectSingle(new LastUpdate());
        if (lastUpdate == null || mustUpdate(lastUpdate)) {
            new UpdateProgrammes(null, null, null, null, database){
                @SuppressWarnings("RefusedBequest")
                @Override
                protected void onPostExecute(Void result) {
                    stopSelf();
                }
            }.execute();
        } else {
            new TacheAvecGestionErreurReseau(null){
                @Override
                protected void myDoBackground() throws YboTvErreurReseau {
                    UpdateChannels.updateChannels(null, database, null, null, null);
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    stopSelf();
                }
            }.execute();
        }
    }

    private static final long DAYS_FOR_WIFI = TimeUnit.DAYS.toMillis(1);
    private static final long DAYS_FOR_GSM = TimeUnit.DAYS.toMillis(3);

    private boolean mustUpdate(LastUpdate lastUpdate) {
        Date date = new Date();

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);



        long timeSinceLastUpdate = date.getTime() - lastUpdate.getLastUpdate().getTime();
        if (connMgr.getActiveNetworkInfo() != null && connMgr.getActiveNetworkInfo().isConnected()) {

            long deltaToUpdate = DAYS_FOR_GSM;
            if (connMgr.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI) {
                deltaToUpdate = DAYS_FOR_WIFI;
            }
            return timeSinceLastUpdate > deltaToUpdate;
        } else {
            return false;
        }
    }
}
