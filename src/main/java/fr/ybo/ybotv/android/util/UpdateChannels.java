package fr.ybo.ybotv.android.util;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.database.YboTvDatabase;
import fr.ybo.ybotv.android.exception.YboTvErreurReseau;
import fr.ybo.ybotv.android.modele.FavoriteChannel;
import fr.ybo.ybotv.android.modele.Programme;
import fr.ybo.ybotv.android.service.YboTvService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UpdateChannels {


    public static void updateChannels(final Context context, YboTvDatabase database, Handler handler, final TextView messageLoading, final ProgressBar loadingBar) throws YboTvErreurReseau {
        // Récupération des favoris
        Set<String> favoriteChannels = getFavoriteChannelIds(database);

        // Récupération de la liste des chaînes déjà en base
        Set<String> channelsInDb = getChannelsInDb(database);

        // Calcul des chaînes à ajouter
        Set<String> channelsToAdd = getChannelsToAdd(favoriteChannels, channelsInDb);

        // Calcul des chaînes à supprimer
        Set<String> channelsToDelete = getChannelsToDelete(favoriteChannels, channelsInDb);

        int nbActions = channelsToAdd.size() + channelsToDelete.size() + 1;

        // Suppression de programmes qui doivent l'être.
        deleteChannels(context, database, handler, messageLoading, loadingBar, channelsToDelete, nbActions);

        int count = channelsToDelete.size();

        // Récupération des programmes à insérer.
        for (final String channelToAdd : channelsToAdd) {
            if (handler != null) {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        messageLoading.setText(context.getString(R.string.ajoutChaine, channelToAdd));
                    }
                });
            }

            // Insertion des programmes.
            insertProgrammes(database, getProgrammeToInsert(channelToAdd));

            count++;
            final int progress = 100 * count / (nbActions);
            if (handler != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadingBar.setProgress(progress);
                    }
                });
            }
        }
    }

    private static Set<String> getFavoriteChannelIds(YboTvDatabase database) {
        Chrono chrono = new Chrono("GetFavorites").start();
        Set<String> favoriteChannels = new HashSet<String>();
        for (FavoriteChannel favoriteChannel : database.selectAll(FavoriteChannel.class)) {
            favoriteChannels.add(favoriteChannel.getChannel());
        }
        chrono.stop();
        Log.d(YboTvApplication.TAG, "Chaines favorites : " + favoriteChannels.toString());
        return favoriteChannels;
    }

    private static Set<String> getChannelsInDb(YboTvDatabase database) {
        Chrono chrono = new Chrono("GetChannels").start();
        StringBuilder request = new StringBuilder();
        request.append("SELECT distinct(channel) as channel ");
        request.append("FROM Programme");
        Set<String> channelsInDb = new HashSet<String>();
        Cursor cursor = database.executeSelectQuery(request.toString(), null);
        int channelCol = cursor.getColumnIndex("channel");
        while (cursor.moveToNext()) {
            channelsInDb.add(cursor.getString(channelCol));
        }
        chrono.stop();
        Log.d(YboTvApplication.TAG, "Chaines en base : " + channelsInDb.toString());
        return channelsInDb;
    }

    private static Set<String> getChannelsToAdd(Set<String> favoriteChannels, Set<String> channelsInDb) {
        Chrono chrono = new Chrono("ChannelsToAdd>Calculate").start();
        Set<String> channelsToAdd = new HashSet<String>();
        for (String favoriteId : favoriteChannels) {
            if (!channelsInDb.contains(favoriteId)) {
                channelsToAdd.add(favoriteId);
            }
        }
        Log.d(YboTvApplication.TAG, "Nombre de chaines a ajouter : " + channelsToAdd.size());
        Log.d(YboTvApplication.TAG, "Liste des chaines a ajouter : " + channelsToAdd.toString());
        chrono.stop();
        return channelsToAdd;
    }

    private static Set<String> getChannelsToDelete(Set<String> favoriteChannels, Set<String> channelsInDb) {
        Chrono chrono = new Chrono("ChannelsToDelete>Calculate").start();
        Set<String> channelsToDelete = new HashSet<String>();
        for (String idInDb : channelsInDb) {
            if (!favoriteChannels.contains(idInDb)) {
                channelsToDelete.add(idInDb);
            }
        }
        Log.d(YboTvApplication.TAG, "Nombre de chaines a supprimer : " + channelsToDelete.size());
        Log.d(YboTvApplication.TAG, "Liste des chaines a supprimer : " + channelsToDelete.toString());
        chrono.stop();
        return channelsToDelete;
    }

    private static void insertProgrammes(YboTvDatabase database, List<Programme> programmesToInsert) {
        Chrono chrono = new Chrono("Programme>insertion").start();

        try {
            SQLiteDatabase db = database.getWritableDatabase();
            database.beginTransaction();
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

            for (Programme programme : programmesToInsert) {
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

                ih.execute();
            }
        } finally {
            database.endTransaction();
        }

        chrono.stop();
    }

    private static List<Programme> getProgrammeToInsert(String channelToAdd) throws YboTvErreurReseau {
        Chrono chrono = new Chrono("Channels>Add>Recuperation>" + channelToAdd).start();
        Set<String> programeIds = new HashSet<String>();
        List<Programme> programmesToInsert = new ArrayList<Programme>();


        for (Programme programme : YboTvService.getInstance().getProgrammes(channelToAdd)) {

            if (!programeIds.contains(programme.getId())) {
                programme.fillFields();
                programmesToInsert.add(programme);
                programeIds.add(programme.getId());
            }
        }
        chrono.stop();
        return programmesToInsert;
    }

    private static void deleteChannels(final Context context, YboTvDatabase database, Handler handler, final TextView messageLoading, final ProgressBar loadingBar, Set<String> channelsToDelete, int nbActions) {
        Chrono chrono = new Chrono("Channels>Delete").start();
        int count = 0;
        for (final String channelToDelete : channelsToDelete) {
            if (handler != null) {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        messageLoading.setText(context.getString(R.string.suppressionChaine, channelToDelete));
                    }
                });
            }
            String[] channelCause = {channelToDelete};
            int nbProgrammeDeleted = database.getWritableDatabase().delete("Programme", "channel = :channel", channelCause);
            Log.d(YboTvApplication.TAG, "Nombre de programme supprime pour la chaine " + channelToDelete + " : " + nbProgrammeDeleted);
            count++;
            final int progress = 100 * count / (nbActions);
            if (handler != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadingBar.setProgress(progress);
                    }
                });
            }
        }
        chrono.stop();
    }

}
