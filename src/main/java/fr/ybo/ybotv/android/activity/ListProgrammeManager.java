package fr.ybo.ybotv.android.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import fr.ybo.ybotv.android.adapter.ProgrammeAdapter;
import fr.ybo.ybotv.android.lasylist.RatingLoader;
import fr.ybo.ybotv.android.modele.ChannelWithProgramme;
import fr.ybo.ybotv.android.modele.Programme;
import fr.ybo.ybotv.android.util.Chrono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListProgrammeManager {

    public interface GetProgramme {
        List<ChannelWithProgramme> getProgrammes();
    }

    private final GetProgramme getProgramme;
    private final ProgrammeAdapter adapter;
    private List<ChannelWithProgramme> channels = new ArrayList<ChannelWithProgramme>();
    private Activity context;

    public ListProgrammeManager(AbsListView listView, Activity context, GetProgramme getProgramme) {
        this.getProgramme = getProgramme;
        this.adapter = new ProgrammeAdapter(context, channels);
        this.context = context;
        if (listView instanceof ListView) {
            ((ListView) listView).setAdapter(adapter);
        } else if (listView instanceof GridView) {
            ((GridView) listView).setAdapter(adapter);
        } else {
            listView.setAdapter(adapter);
        }
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Programme programme = channels.get(position).getProgramme();
                Intent intent = new Intent(ListProgrammeManager.this.context, ProgrammeActivity.class);
                intent.putExtra("programme", (Parcelable) programme);
                ListProgrammeManager.this.context.startActivity(intent);
            }
        });
        context.registerForContextMenu(listView);
    }

    private void getRatings() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (ChannelWithProgramme channel : channels) {
            if (channel.getProgramme().isTvShow() || channel.getProgramme().isMovie()) {
                executorService.submit(new RatingLoader(channel.getProgramme(), new RunNotifyDataSetChangedOnUiThread(context, adapter)));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void constructAdapter() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Chrono chrono = new Chrono("GetProgrammes").start();
                List<ChannelWithProgramme> newChannels = getProgramme.getProgrammes();
                chrono.stop();

                chrono = new Chrono("Programmes>sort").start();
                // Sort
                Collections.sort(newChannels, new Comparator<ChannelWithProgramme>() {
                    @Override
                    public int compare(ChannelWithProgramme channelWithProgramme, ChannelWithProgramme channelWithProgramme1) {
                        Integer numero1 = channelWithProgramme.getChannel().getNumero();
                        Integer numero2 = channelWithProgramme1.getChannel().getNumero();
                        if (numero1.equals(numero2)) {
                            String start1 = channelWithProgramme.getProgramme().getStart();
                            String start2 = channelWithProgramme1.getProgramme().getStart();
                            return start1.compareTo(start2);
                        }
                        return numero1.compareTo(numero2);
                    }
                });
                chrono.stop();

                chrono = new Chrono("Programmes>listUpdate").start();
                channels.clear();
                channels.addAll(newChannels);
                chrono.stop();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.notifyDataSetChanged();
                getRatings();
                super.onPostExecute(aVoid);
            }
        }.execute();

    }

    private static class NotifyDataSetChanged implements Runnable {

        private ProgrammeAdapter adapter;

        private NotifyDataSetChanged(ProgrammeAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void run() {
            adapter.notifyDataSetChanged();
        }
    }

    private static class RunNotifyDataSetChangedOnUiThread implements Runnable {

        private Activity context;
        private ProgrammeAdapter adapter;

        private RunNotifyDataSetChangedOnUiThread(Activity context, ProgrammeAdapter adapter) {
            this.context = context;
            this.adapter = adapter;
        }

        @Override
        public void run() {
            context.runOnUiThread(new NotifyDataSetChanged(adapter));
        }
    }
}
