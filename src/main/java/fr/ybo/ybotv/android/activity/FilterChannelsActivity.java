package fr.ybo.ybotv.android.activity;


import android.os.Bundle;
import com.actionbarsherlock.app.SherlockListActivity;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.adapter.FilterChannelsAdapter;

public class FilterChannelsActivity extends SherlockListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_ss_pub);
        getSupportActionBar().setTitle(R.string.filter_channels);

        getListView().setAdapter(new FilterChannelsAdapter(this, ((YboTvApplication)getApplication()).getDatabase()));
        getListView().setTextFilterEnabled(true);
        registerForContextMenu(getListView());
    }
}
