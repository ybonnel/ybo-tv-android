package fr.ybo.ybotv.android.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.database.YboTvDatabase;
import fr.ybo.ybotv.android.exception.YboTvErreurReseau;
import fr.ybo.ybotv.android.modele.LastUpdate;
import fr.ybo.ybotv.android.util.FavorisManager;
import fr.ybo.ybotv.android.util.TacheAvecGestionErreurReseau;
import fr.ybo.ybotv.android.util.TimeUnit;
import fr.ybo.ybotv.android.util.UpdateChannels;
import fr.ybo.ybotv.android.util.UpdateProgrammes;

import java.util.Date;

@SuppressWarnings("unchecked")
public class ExportChannelsActivity extends SherlockActivity {

    private FavorisManager favorisManager;

    private TextView message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.export);
        favorisManager = new FavorisManager(getApplicationContext(), ((YboTvApplication) getApplication()).getDatabase());
        getSupportActionBar().setTitle(R.string.titre_export);

        Button exportButton = (Button) findViewById(R.id.export_favorite);
        Button importButton = (Button) findViewById(R.id.import_favorite);
        message = (TextView) findViewById(R.id.export_message);

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message.setText(favorisManager.export());
            }
        });

        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message.setText(favorisManager.load());
            }
        });
    }

}
