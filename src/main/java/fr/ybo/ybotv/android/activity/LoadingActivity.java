package fr.ybo.ybotv.android.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.database.YboTvDatabase;
import fr.ybo.ybotv.android.exception.YboTvErreurReseau;
import fr.ybo.ybotv.android.modele.LastUpdate;
import fr.ybo.ybotv.android.util.TacheAvecGestionErreurReseau;
import fr.ybo.ybotv.android.util.TimeUnit;
import fr.ybo.ybotv.android.util.UpdateChannels;
import fr.ybo.ybotv.android.util.UpdateProgrammes;

import java.util.Date;

@SuppressWarnings("unchecked")
public class LoadingActivity extends ActionBarActivity {

    private TextView messageLoading;
    private ProgressBar loadingBar;

    private Handler handler = new Handler();


    private boolean mustUpdate(LastUpdate lastUpdate) {
        Date date = new Date();

        long timeSinceLastUpdate = date.getTime() - lastUpdate.getLastUpdate().getTime();
        long fiveDays = TimeUnit.DAYS.toMillis(5);

        return (timeSinceLastUpdate > fiveDays);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        YboTvDatabase database = ((YboTvApplication) getApplication()).getDatabase();
        LastUpdate lastUpdate = database.selectSingle(new LastUpdate());

        messageLoading = (TextView) findViewById(R.id.messageLoading);
        loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
        String currentVersion = "";
        try {
            PackageInfo _info = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersion = _info.versionName;
        } catch (PackageManager.NameNotFoundException ignore) {
        }

        ((TextView) findViewById(R.id.loading_version)).setText(getString(R.string.version, currentVersion));
        getSupportActionBar().setTitle(R.string.loading);

        if (lastUpdate == null || mustUpdate(lastUpdate)) {
            showDialog(R.id.dialog_loading);
        } else {
            findViewById(R.id.firstLoading).setVisibility(View.GONE);
            new TacheAvecGestionErreurReseau(this){
                @Override
                protected void myDoBackground() throws YboTvErreurReseau {
                    UpdateChannels.updateChannels(LoadingActivity.this, ((YboTvApplication) getApplication()).getDatabase(), handler, messageLoading, loadingBar);
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    finish();
                    startActivity(new Intent(LoadingActivity.this, ((YboTvApplication) getApplication()).getDefaultActivity()));
                    ((YboTvApplication) getApplication()).setRecurringAlarm();
                }
            }.execute();

        }

    }


    @SuppressWarnings("unchecked")
    private void loadDatas() {
        messageLoading.setText(R.string.getChannels);
        new UpdateProgrammes(this, handler, loadingBar, messageLoading, ((YboTvApplication) getApplication()).getDatabase()) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                finish();
                if (!hasErreurReseau()) {
                    ((YboTvApplication) getApplication()).setRecurringAlarm();
                    //startActivity(new Intent(LoadingActivity.this, ((YboTvApplication) getApplication()).getDefaultActivity()));
                    LoadingActivity.this.finish();
                }
            }
        }.execute();
    }


    // Verrue pour faire marcher en 1.6.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((!(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT)
                && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // On ne fait rien.
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == R.id.dialog_loading) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.firstLoadingMessage));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.oui), new Dialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    loadDatas();
                }
            });
            builder.setNegativeButton(getString(R.string.non), new Dialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    LoadingActivity.this.finish();
                }
            });
            return builder.create();
        }
        return super.onCreateDialog(id);
    }

}
