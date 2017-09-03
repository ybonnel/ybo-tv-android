package fr.ybo.ybotv.android.util;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.database.YboTvDatabase;
import fr.ybo.ybotv.android.exception.YboTvErreurReseau;
import fr.ybo.ybotv.android.modele.Channel;
import fr.ybo.ybotv.android.modele.FavoriteChannel;
import fr.ybo.ybotv.android.modele.LastUpdate;
import fr.ybo.ybotv.android.modele.Programme;
import fr.ybo.ybotv.android.service.YboTvService;

import java.util.*;


public class UpdateProgrammes extends TacheAvecGestionErreurReseau {

    private Handler handler;
    private ProgressBar loadingBar;
    private TextView messageLoading;
    private YboTvDatabase database;

    private final static Set<String> defaultFavoriteChannels = new HashSet<String>() {{
        add("192");
        add("4");
        add("80");
        add("34");
        add("47");
        add("118");
        add("111");
        add("445");
        add("119");
        add("195");
        add("446");
        add("444");
        add("234");
        add("78");
        add("481");
        add("226");
        add("458");
        add("482");
        add("160");
        add("1404");
        add("1401");
        add("1403");
        add("1402");
        add("1400");
        add("1399");
        add("112");
        add("2111");
        add("191");
        add("205");
    }};

    public UpdateProgrammes(Context context, Handler handler, final ProgressBar loadingBar, final TextView messageLoading, YboTvDatabase database) {
        super(context);
        this.handler = handler;
        this.loadingBar = loadingBar;
        this.messageLoading = messageLoading;
        this.database = database;
    }

    @Override
    protected void myDoBackground() throws YboTvErreurReseau {
        // Récupération des chaines
        Chrono chrono = new Chrono("UpdateProgramme>Reseau").start();
        List<Channel> channels = YboTvService.getInstance().getChannels();

        if (handler != null) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    messageLoading.setText(R.string.getProgrammes);
                }
            });
        }


        List<FavoriteChannel> favoriteChannels = database.selectAll(FavoriteChannel.class);

        if (favoriteChannels.isEmpty()) {
            for (String defaultChannelId : defaultFavoriteChannels) {
                FavoriteChannel favoriteChannel = new FavoriteChannel();
                favoriteChannel.setChannel(defaultChannelId);
                database.insert(favoriteChannel);
                favoriteChannels.add(favoriteChannel);
            }
        }

        Set<String> favoriteChannelIds = new HashSet<String>(favoriteChannels.size());
        for (FavoriteChannel favoriteChannel : favoriteChannels) {
            favoriteChannelIds.add(favoriteChannel.getChannel());
        }


        int count = 0;
        int nbChaines = favoriteChannelIds.size();

        for (Channel channel : channels) {
            Set<String> programeIds = new HashSet<String>();


            if (!favoriteChannelIds.contains(channel.getId())) {
                continue;
            }

            List<Programme> programmes = YboTvService.getInstance().getProgrammes(channel);

            String[] channelCause = {channel.getId()};
            database.getWritableDatabase().delete("Programme", "channel = :channel", channelCause);

            SQLiteDatabase db = database.getWritableDatabase();
            database.beginTransaction();
            try {
                DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db, database.getBase().getTable(Programme.class).getName());

                int idCol = ih.getColumnIndex("id");
                int startCol = ih.getColumnIndex("start");
                int stopCol = ih.getColumnIndex("stop");
                int channelCol = ih.getColumnIndex("channel");
                int iconCol = ih.getColumnIndex("icon");
                int titleCol = ih.getColumnIndex("title");
                int descCol = ih.getColumnIndex("desc");
                int starRatingCol = ih.getColumnIndex("starRating");
                int csaRatingCol = ih.getColumnIndex("csaRating");
                int directorsCol = ih.getColumnIndex("directors");
                int actorsCol = ih.getColumnIndex("actors");
                int writersCol = ih.getColumnIndex("writers");
                int presentersCol = ih.getColumnIndex("presenters");
                int dateCol = ih.getColumnIndex("date");
                int categoriesCol = ih.getColumnIndex("categories");
                int critiqueCol = ih.getColumnIndex("critique");

                for (Programme programme : programmes) {

                    if (!programeIds.contains(programme.getId())) {
                        programme.fillFields();
                        programeIds.add(programme.getId());
                        ih.prepareForInsert();

                        // Add the data for each column
                        ih.bind(idCol, programme.getId());
                        ih.bind(startCol, programme.getStart());
                        ih.bind(stopCol, programme.getStop());
                        ih.bind(channelCol, programme.getChannel());
                        ih.bind(iconCol, programme.getIcon());
                        ih.bind(titleCol, programme.getTitle());
                        ih.bind(descCol, programme.getDesc());
                        ih.bind(starRatingCol, programme.getStarRating());
                        ih.bind(csaRatingCol, programme.getCsaRating());
                        ih.bind(directorsCol, programme.getDirectorsInCsv());
                        ih.bind(actorsCol, programme.getActorsInCsv());
                        ih.bind(writersCol, programme.getWritersInCsv());
                        ih.bind(presentersCol, programme.getPresentersInCsv());
                        ih.bind(dateCol, programme.getDate());
                        ih.bind(categoriesCol, programme.getCategoriesInCsv());
                        ih.bind(critiqueCol, programme.getCritique());

                        ih.execute();
                    }
                }
            } finally {
                database.endTransaction();
            }

            count++;
            final int progress = 100 * count / (nbChaines + 1);
            if (handler != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadingBar.setProgress(progress);
                    }
                });
            }
        }

        if (handler != null) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    messageLoading.setText(R.string.loadingChannels);
                }
            });
        }
        chrono.stop();

        chrono = new Chrono("Chaines>Suppression").start();
        // Suppression des anciennes chaînes.
        database.deleteAll(Channel.class);
        chrono.stop();

        chrono = new Chrono("Chaines>insertion").start();

        // insertion des nouvelles chaines
        try {
            database.beginTransaction();
            for (Channel channel : channels) {
                database.insert(channel);
            }
        } finally {
            database.endTransaction();
        }

        if (handler != null) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    messageLoading.setText(R.string.loadingProgrammes);
                }
            });
        }

        count++;
        final int progress = 100 * count / (nbChaines + 1);
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    loadingBar.setProgress(progress);
                }
            });
        }

        chrono.stop();

        chrono = new Chrono("LastUpdate>update").start();
        database.deleteAll(LastUpdate.class);
        LastUpdate lastUpdate = new LastUpdate();
        lastUpdate.setLastUpdate(new Date());
        database.insert(lastUpdate);
        chrono.stop();
    }
}
