package fr.ybo.ybotv.android.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.grid.ProgrammeGridView;
import fr.ybo.ybotv.android.modele.Channel;
import fr.ybo.ybotv.android.modele.FavoriteChannel;
import fr.ybo.ybotv.android.modele.Programme;
import fr.ybo.ybotv.android.util.AdMobUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProgrammeGridActivity extends MenuManager.AbstractSimpleActivity {

    private class GetProgrammes extends AsyncTask<Void, Void, Void> {

        private YboTvApplication app;
        private List<Channel> channels;
        private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        private SimpleDateFormat sdfComplet = new SimpleDateFormat("yyyyMMddHHmmss");
        private Date currentDate = new Date();
        private Date start;
        private Calendar calendar = Calendar.getInstance();

        private GetProgrammes(YboTvApplication app) {
            this.app = app;
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<FavoriteChannel> favoriteChannels = app.getDatabase().selectAll(FavoriteChannel.class);
            channels = new ArrayList<Channel>();

            for (FavoriteChannel favoriteChannel : favoriteChannels) {
                Channel channelTmp = new Channel();
                channelTmp.setId(favoriteChannel.getChannel());
                channelTmp = app.getDatabase().selectSingle(channelTmp);
                if (channelTmp != null) {
                    channels.add(channelTmp);
                }
            }

            Collections.sort(channels);

            Calendar calendarYesterday = (Calendar) calendar.clone();
            calendarYesterday.add(Calendar.DAY_OF_MONTH, -1);

            Date today = calendar.getTime();
            Date yesterday  = calendarYesterday.getTime();

            String dateDebut;

            if (calendar.get(Calendar.HOUR_OF_DAY) < 3) {
                dateDebut = sdf.format(yesterday) + "030000";
            } else {
                dateDebut = sdf.format(today) + "030000";
            }

            try {
                start = sdfComplet.parse(dateDebut);
            } catch (ParseException ignore) {
                start = new Date();
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            findViewById(R.id.messageLoading).setVisibility(View.GONE);

            LinearLayout gridContainer = (LinearLayout) findViewById(R.id.grid_container);
            LinearLayout iconeContainer = (LinearLayout) findViewById(R.id.icone_chaine_container);

            LayoutInflater inflater = LayoutInflater.from(ProgrammeGridActivity.this);



            for (Channel channel : channels) {
                channel.programmes = Programme.getProgrammes(app, channel, calendar);


                LinearLayout layoutForIcone = (LinearLayout) inflater.inflate(R.layout.programme_grid_icone_chaine, null);
                ImageView iconeChaine = (ImageView) layoutForIcone.findViewById(R.id.programme_imageChaine);
                iconeChaine.setImageResource(channel.getIconResource());

                LinearLayout layoutForGrid = (LinearLayout) inflater.inflate(R.layout.programme_grid_view, null);
                ProgrammeGridView programmeGridView = (ProgrammeGridView) layoutForGrid.findViewById(R.id.programme_grid_view);
                programmeGridView.setProgrammes(channel.programmes, start, currentDate);

                iconeContainer.addView(layoutForIcone);
                gridContainer.addView(layoutForGrid);
            }

            gridContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontal_scroll);
                    int lineOffset = (int) ((currentDate.getTime() - start.getTime()) / 1000 / 60 * ProgrammeGridView.sizeofminute);
                    Log.d(YboTvApplication.TAG, "OffsetToScroll : " + lineOffset);
                    horizontalScrollView.scrollTo(lineOffset, 0);
                    findViewById(R.id.grid_container).getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        createMenu();
        setContentView(R.layout.list_for_grid);

        new GetProgrammes((YboTvApplication) getApplication()).execute();

        AdMobUtil.manageAds(this);
    }


    @Override
    public int getMenuIdOfClass() {
        return R.id.menu_grid;
    }
}

