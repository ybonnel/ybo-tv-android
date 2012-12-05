package fr.ybo.ybotv.android.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.database.YboTvDatabase;
import fr.ybo.ybotv.android.exception.YboTvErreurReseau;
import fr.ybo.ybotv.android.util.TacheAvecGestionErreurReseau;
import fr.ybo.ybotv.android.util.UpdateChannels;

@SuppressWarnings("unchecked")
public class UpdateChannelsActivity extends SherlockActivity {

    private TextView messageLoading;
    private ProgressBar loadingBar;
    private YboTvDatabase database;

    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_channels);

        messageLoading = (TextView) findViewById(R.id.messageLoading);
        loadingBar = (ProgressBar) findViewById(R.id.loadingBar);

        getSupportActionBar().setTitle(R.string.updateChannels);

        messageLoading.setText(R.string.calculUpdateChannels);

        database = ((YboTvApplication) getApplication()).getDatabase();

        new TacheAvecGestionErreurReseau(this) {
            @Override
            protected void myDoBackground() throws YboTvErreurReseau {
                UpdateChannels.updateChannels(UpdateChannelsActivity.this, database, handler, messageLoading, loadingBar);

            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                UpdateChannelsActivity.this.setResult(RESULT_OK);
                finish();
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

}
