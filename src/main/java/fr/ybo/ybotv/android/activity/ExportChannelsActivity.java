package fr.ybo.ybotv.android.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.util.FavorisManager;

@SuppressWarnings("unchecked")
public class ExportChannelsActivity extends ActionBarActivity {

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
