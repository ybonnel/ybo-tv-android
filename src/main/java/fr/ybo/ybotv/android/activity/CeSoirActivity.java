package fr.ybo.ybotv.android.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.octo.android.robodemo.LabeledPoint;
import com.octo.android.robodemo.RoboDemo;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.adapter.CeSoirViewFlowAdapter;
import fr.ybo.ybotv.android.util.AdMobUtil;
import org.taptwo.android.widget.TitleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import java.util.ArrayList;
import java.util.Calendar;

public class CeSoirActivity extends MenuManager.AbstractSimpleActivity implements DatePickerDialog.OnDateSetListener {

    private Calendar currentDate;

    private CeSoirViewFlowAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flow);
        createMenu();
        currentDate = Calendar.getInstance();

        ViewFlow viewFlow = (ViewFlow) findViewById(R.id.viewflow);
        adapter = new CeSoirViewFlowAdapter(this, currentDate);
        viewFlow.setAdapter(adapter);
        TitleFlowIndicator indicator = (TitleFlowIndicator) findViewById(R.id.viewflowindic);
        indicator.setTitleProvider(adapter);
        viewFlow.setFlowIndicator(indicator);

        AdMobUtil.manageAds(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.cesoirmenu, menu);
        return true;
    }


    @Override
    public int getMenuIdOfClass() {
        return R.id.menu_cesoir;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        if (id == R.id.dialog_choixDate) {
            return new DatePickerDialog(this, this, currentDate.get(Calendar.YEAR),
                    currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        }
        return super.onCreateDialog(id);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        currentDate.set(Calendar.YEAR, year);
        currentDate.set(Calendar.MONTH, monthOfYear);
        currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        adapter.changeCurrentDate(currentDate);
    }

    private String DEMO_ACTIVITY_ID = CeSoirActivity.class.getSimpleName();

    /**
     * Displays demo if never show again has never been checked by the user.
     */
    private void displayDemo() {
        ArrayList<LabeledPoint> arrayListPoints = new ArrayList<LabeledPoint>();

        // create a list of LabeledPoints
        arrayListPoints.add(new LabeledPoint(this, 0.95f, 0.05f, getString(R.string.choixDate)));
        arrayListPoints.add(new LabeledPoint(this, 1.0f, 0.5f, getString(R.string.slideToChangeTime)));
        arrayListPoints.add(new LabeledPoint(this, 0.25f, 0.35f, getString(R.string.clickToHaveDetail)));


        // start DemoActivity.
        Intent intent = new Intent(this, HelpDemoActivity.class);
        RoboDemo.prepareDemoActivityIntent(intent, DEMO_ACTIVITY_ID, arrayListPoints);
        startActivity(intent);
    }

}

