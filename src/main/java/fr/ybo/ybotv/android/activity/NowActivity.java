package fr.ybo.ybotv.android.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.GridView;
import com.actionbarsherlock.view.MenuItem;
import com.octo.android.robodemo.LabeledPoint;
import com.octo.android.robodemo.RoboDemo;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.modele.ChannelWithProgramme;
import fr.ybo.ybotv.android.util.AdMobUtil;

import java.util.ArrayList;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_help) {
            displayDemo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String DEMO_ACTIVITY_ID = CeSoirActivity.class.getSimpleName();

    /**
     * Displays demo if never show again has never been checked by the user.
     */
    private void displayDemo() {
        ArrayList<LabeledPoint> arrayListPoints = new ArrayList<LabeledPoint>();

        // create a list of LabeledPoints
        arrayListPoints.add(new LabeledPoint(this, 0.25f, 0.4f, getString(R.string.clickToHaveDetail)));


        // start DemoActivity.
        Intent intent = new Intent(this, HelpDemoActivity.class);
        RoboDemo.prepareDemoActivityIntent(intent, DEMO_ACTIVITY_ID, arrayListPoints);
        startActivity(intent);
    }
}

