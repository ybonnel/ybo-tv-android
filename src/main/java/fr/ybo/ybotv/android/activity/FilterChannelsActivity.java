package fr.ybo.ybotv.android.activity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockListActivity;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.adapter.FilterChannelsAdapter;
import fr.ybo.ybotv.android.database.YboTvDatabase;
import fr.ybo.ybotv.android.modele.Channel;
import fr.ybo.ybotv.android.modele.FavoriteChannel;

public class FilterChannelsActivity extends SherlockListActivity  {

    private FilterChannelsAdapter adapter;
    private YboTvDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_ss_pub);
        getSupportActionBar().setTitle(R.string.filter_channels);

        database =  ((YboTvApplication)getApplication()).getDatabase();

        adapter = new FilterChannelsAdapter(this, database);
        ListView listView = getListView();
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Channel channel = adapter.getItem(position);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.filter_channel_element_favorite);
                boolean favorite = checkBox.isChecked();
                favorite = !favorite;
                channel.setFavorite(favorite);
                checkBox.setChecked(favorite);
                FavoriteChannel favoriteChannel = new FavoriteChannel();
                favoriteChannel.setChannel(channel.getId());
                if (favorite) {
                    Log.d(YboTvApplication.TAG, "New favorite channel : " + channel.getId());
                    database.insert(favoriteChannel);
                } else {
                    Log.d(YboTvApplication.TAG, "Remove favorite channel : " + channel.getId());
                    database.delete(favoriteChannel);
                }
            }
        });
        registerForContextMenu(listView);
    }
}
