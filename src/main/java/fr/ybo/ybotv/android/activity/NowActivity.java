package fr.ybo.ybotv.android.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.GridView;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.modele.ChannelWithProgramme;
import fr.ybo.ybotv.android.util.AdMobUtil;

import java.util.List;

public class NowActivity extends MenuManager.AbstractSimpleActivity implements ListProgrammeManager.GetProgramme {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid);
        createMenu();

        GridView gridView = (GridView) findViewById(R.id.grid);

        if (((YboTvApplication)getApplication()).isTablet()) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                gridView.setNumColumns(3);
            } else {
                gridView.setNumColumns(2);
            }
        }

        ListProgrammeManager listProgrammeManager = new ListProgrammeManager(gridView, this, this);
        listProgrammeManager.constructAdapter();

        AdMobUtil.manageAds(this);
    }


    public List<ChannelWithProgramme> getProgrammes() {
        return ChannelWithProgramme.getCurrentProgrammes((YboTvApplication) getApplication());
    }

    @Override
    public int getMenuIdOfClass() {
        return R.id.menu_now;
    }
}

