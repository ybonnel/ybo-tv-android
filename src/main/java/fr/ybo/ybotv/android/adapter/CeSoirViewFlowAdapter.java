package fr.ybo.ybotv.android.adapter;


import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.activity.ListProgrammeManager;
import fr.ybo.ybotv.android.modele.ChannelWithProgramme;
import org.taptwo.android.widget.TitleProvider;

import java.text.SimpleDateFormat;
import java.util.*;

public class CeSoirViewFlowAdapter extends BaseAdapter implements TitleProvider {


    private LayoutInflater inflater;
    private Activity context;

    private int[] titles = {R.string.primeTime, R.string.partie2, R.string.finSoiree};

    private Calendar currentDate;

    private AbsListView.OnScrollListener onScrollListener;

    public CeSoirViewFlowAdapter(Activity context, Calendar currentDate, AbsListView.OnScrollListener onScrollListener) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.currentDate = currentDate;
        this.onScrollListener = onScrollListener;
    }

    public void changeCurrentDate(Calendar calendar) {
        currentDate = calendar;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class MyGetProgramme implements ListProgrammeManager.GetProgramme {

        private int position;
        private YboTvApplication application;
        private Calendar currentDate;

        private MyGetProgramme(int position, YboTvApplication application, Calendar currentDate) {
            this.position = position;
            this.application = application;
            this.currentDate = currentDate;
        }

        @Override
        public List<ChannelWithProgramme> getProgrammes() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String dateToSelect;
            switch (position) {
                case 1:
                    // Deuxième partie
                    dateToSelect = simpleDateFormat.format(currentDate.getTime()) + "231500";
                    break;
                case 2:
                    // Fin de soirée
                    Calendar calendarTwomorrow = (Calendar) currentDate.clone();
                    calendarTwomorrow.add(Calendar.DAY_OF_MONTH, 1);
                    Date twomorrow = calendarTwomorrow.getTime();
                    dateToSelect = simpleDateFormat.format(twomorrow) + "011500";
                    break;
                default:
                    // PrimeTime
                    dateToSelect = simpleDateFormat.format(currentDate.getTime()) + "211500";
                    break;
            }

            return ChannelWithProgramme.getProgrammesForDate(application, dateToSelect);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_ss_pub, null);
        }

        GridView gridView = (GridView) convertView.findViewById(R.id.grid);
        gridView.setOnScrollListener(onScrollListener);
        gridView.setTag(position);

        if (((YboTvApplication) context.getApplication()).isTablet()) {
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                gridView.setNumColumns(3);
            } else {
                gridView.setNumColumns(2);
            }
        }
        new ListProgrammeManager(gridView, context, new MyGetProgramme(position, (YboTvApplication) context.getApplication(), currentDate)).constructAdapter();

        return convertView;
    }


    /* (non-Javadoc)
    * @see org.taptwo.android.widget.TitleProvider#getTitle(int)
    */
    @Override
    public String getTitle(int position) {
        return context.getString(titles[position]);
    }
}
