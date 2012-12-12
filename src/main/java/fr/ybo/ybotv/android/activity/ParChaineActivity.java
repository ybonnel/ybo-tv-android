package fr.ybo.ybotv.android.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ListView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.octo.android.robodemo.LabeledPoint;
import com.octo.android.robodemo.RoboDemo;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.adapter.ChannelsAdapter;
import fr.ybo.ybotv.android.adapter.ParChaineViewFlowAdapter;
import fr.ybo.ybotv.android.adapter.ProgrammeByChaineAdapter;
import fr.ybo.ybotv.android.modele.Channel;
import fr.ybo.ybotv.android.modele.FavoriteChannel;
import fr.ybo.ybotv.android.modele.Programme;
import fr.ybo.ybotv.android.util.AdMobUtil;
import org.taptwo.android.widget.TitleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public class ParChaineActivity extends MenuManager.AbstractSimpleActivity implements DatePickerDialog.OnDateSetListener {

    private List<Channel> channels;
    private ViewFlow viewFlow;
    private Calendar currentCalendar;
    private ParChaineViewFlowAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        if (((YboTvApplication) getApplication()).isTablet()) {
            constructViewForTablet();
        } else {
            constructViewForPhone();
        }

        AdMobUtil.manageAds(this);
    }

    private List<Programme> programmes = new ArrayList<Programme>();
    private Channel currentChannel;
    private ProgrammeByChaineAdapter tabletAdapter;
    private GridView programmesListView;

    private void constructViewForTablet() {
        setContentView(R.layout.parchaine_for_tablet);

        ListView channelsListView = (ListView) findViewById(R.id.list_chaine);
        channelsListView.setAdapter(new ChannelsAdapter(this, channels));
        channelsListView.setTextFilterEnabled(true);
        channelsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentChannel = channels.get(position);
                refreshTabletView();
            }
        });

        if (!channels.isEmpty()) {
            currentChannel = channels.get(0);
        }

        programmesListView = (GridView) findViewById(R.id.grid_programmes);
        tabletAdapter = new ProgrammeByChaineAdapter(this, programmes);
        programmesListView.setAdapter(tabletAdapter);
        programmesListView.setTextFilterEnabled(true);
        registerForContextMenu(programmesListView);

        programmesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Programme programme = tabletAdapter.getItem(position);
                Intent intent = new Intent(ParChaineActivity.this, ProgrammeActivity.class);
                intent.putExtra("programme", (Parcelable)programme);
                startActivity(intent);
            }
        });

        refreshTabletView();
    }

    private void refreshTabletView() {
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                List<Programme> programmesTmp = Programme.getProgrammes((YboTvApplication) getApplication(), currentChannel, currentCalendar);

                String currentDateChaine = new SimpleDateFormat("yyyyMMddHHmmss").format(currentCalendar.getTime());

                int currentPosition = 0;
                for (Programme programme : programmesTmp) {
                    if (currentDateChaine.compareTo(programme.getStart()) >= 0
                            && currentDateChaine.compareTo(programme.getStop()) < 0) {
                        break;
                    }
                    currentPosition++;
                }

                programmes.clear();
                programmes.addAll(programmesTmp);

                return currentPosition;
            }

            @Override
            protected void onPostExecute(Integer currentPosition) {
                Log.d(YboTvApplication.TAG, "Nombre de programmes : " + programmes.size());
                tabletAdapter.notifyDataSetChanged();
                if (currentPosition < programmes.size()) {
                    programmesListView.setSelection(currentPosition);
                }
            }
        }.execute();
    }

    private void constructViewForPhone() {
        setContentView(R.layout.flow);
        viewFlow = (ViewFlow) findViewById(R.id.viewflow);
        adapter = new ParChaineViewFlowAdapter(this, channels);
        viewFlow.setAdapter(adapter);
        TitleFlowIndicator indicator = (TitleFlowIndicator) findViewById(R.id.viewflowindic);
        indicator.setTitleProvider(adapter);
        viewFlow.setFlowIndicator(indicator);
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
            return true;
        }
        if (item.getItemId() == R.id.menu_choixDate) {
            showDialog(R.id.dialog_choixDate);
            return true;
        } else if (item.getItemId() == R.id.menu_help) {
            displayDemo();
            return true;
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
                    if (viewFlow != null) {
                        viewFlow.setSelection(which);
                    } else {
                        refreshTabletView();
                    }
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

        if (adapter != null) {
            adapter.changeCurrentDate(currentCalendar);
        } else {
            refreshTabletView();
        }
    }



    private String DEMO_ACTIVITY_ID = CeSoirActivity.class.getSimpleName();

    /**
     * Displays demo if never show again has never been checked by the user.
     */
    private void displayDemo() {
        ArrayList<LabeledPoint> arrayListPoints = new ArrayList<LabeledPoint>();

        // create a list of LabeledPoints
        arrayListPoints.add(new LabeledPoint(this, 0.95f, 0.05f, getString(R.string.choixDate)));
        arrayListPoints.add(new LabeledPoint(this, 1.0f, 0.5f, getString(R.string.slideToChangeChannel)));
        arrayListPoints.add(new LabeledPoint(this, 0.25f, 0.35f, getString(R.string.clickToHaveDetail)));
        arrayListPoints.add(new LabeledPoint(this, 0.75f, 0.05f, getString(R.string.choixChaine)));


        // start DemoActivity.
        Intent intent = new Intent(this, HelpDemoActivity.class);
        RoboDemo.prepareDemoActivityIntent(intent, DEMO_ACTIVITY_ID, arrayListPoints);
        startActivity(intent);
    }
}

