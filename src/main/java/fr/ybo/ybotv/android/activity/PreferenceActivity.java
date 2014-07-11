package fr.ybo.ybotv.android.activity;


import android.os.Bundle;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.util.AdMobUtil;

public class PreferenceActivity extends android.preference.PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        //getSupportActionBar().setTitle(R.string.preference);
        addPreferencesFromResource(R.xml.preferences);

        AdMobUtil.manageAds(this);
    }
}
