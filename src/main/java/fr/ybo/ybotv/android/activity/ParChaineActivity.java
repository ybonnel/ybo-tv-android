package fr.ybo.ybotv.android.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.adapter.ParChaineViewFlowAdapter;
import fr.ybo.ybotv.android.modele.Channel;
import fr.ybo.ybotv.android.modele.FavoriteChannel;
import fr.ybo.ybotv.android.util.AdMobUtil;
import org.taptwo.android.widget.TitleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ParChaineActivity extends MenuManager.AbstractSimpleActivity implements DatePickerDialog.OnDateSetListener {

    private List<Channel> channels;
    private ViewFlow viewFlow;
    private Calendar currentCalendar;
    private ParChaineViewFlowAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flow);
        createMenu();

        currentCalendar = Calendar.getInstance();

        List<FavoriteChannel> favoriteChannels = ((YboTvApplication) getApplication()).getDatabase().selectAll(FavoriteChannel.class);

        channels = new ArrayList<Channel>();

        for (FavoriteChannel favoriteChannel : favoriteChannels) {
            Channel channelTmp = new Channel();
            channelTmp.setId(favoriteChannel.getChannel());
            channelTmp = ((YboTvApplication) getApplication()).getDatabase().selectSingle(channelTmp);
            if (channelTmp != null) {
                channels.add(channelTmp);
            }
        }

        Collections.sort(channels);

        viewFlow = (ViewFlow) findViewById(R.id.viewflow);
        adapter = new ParChaineViewFlowAdapter(this, channels);
        viewFlow.setAdapter(adapter);
        TitleFlowIndicator indicator = (TitleFlowIndicator) findViewById(R.id.viewflowindic);
        indicator.setTitleProvider(adapter);
        viewFlow.setFlowIndicator(indicator);

        AdMobUtil.manageAds(this);
    }

    @Override
    public int getMenuIdOfClass() {
        return R.id.menu_parchaine;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.choixchaine, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_choixChaine) {
            showDialog(R.id.dialog_choixChaine);
        }
        if (item.getItemId() == R.id.menu_choixDate) {
            showDialog(R.id.dialog_choixDate);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == R.id.dialog_choixChaine) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.choixChaine);
            builder.setCancelable(true);
            List<CharSequence> chaines = new ArrayList<CharSequence>(channels.size());
            for (Channel channel : channels) {
                chaines.add(channel.getDisplayName());
            }
            builder.setItems(chaines.toArray(new CharSequence[channels.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    viewFlow.setSelection(which);
                }
            });
            return builder.create();
        } else if (id == R.id.dialog_choixDate) {
            return new DatePickerDialog(this, this, currentCalendar.get(Calendar.YEAR),
                    currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
        }
        return super.onCreateDialog(id);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        currentCalendar.set(Calendar.YEAR, year);
        currentCalendar.set(Calendar.MONTH, monthOfYear);
        currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        adapter.changeCurrentDate(currentCalendar);
    }
}

