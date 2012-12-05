package fr.ybo.ybotv.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.widget.ArrayAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.util.ArraysUtil;
import fr.ybo.ybotv.android.util.ChangeLogDialog;

public class MenuManager implements ActionBar.OnNavigationListener {

    public static interface MenuManagerInterface {

        abstract int getMenuIdOfClass();

        abstract ActionBar getSupportActionBar();

        abstract void refreshContent();
    }

    public static abstract class AbstractSimpleActivity extends SherlockActivity implements MenuManagerInterface {

        private MenuManager actionBarManager = new MenuManager(this);

        public void createMenu() {
            actionBarManager.createMenu();
        }

        @Override
        protected void onResume() {
            super.onResume();
            getSupportActionBar().setSelectedNavigationItem(actionBarManager.getItemPositionForCurrentClass());
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getSupportMenuInflater();
            inflater.inflate(R.menu.mainmenu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            boolean retour = actionBarManager.onOptionsItemSelected(item);
            if (!retour) {
                retour = super.onOptionsItemSelected(item);
            }
            return retour;
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            actionBarManager.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        public void onStart() {
            super.onStart();
            EasyTracker.getInstance().activityStart(this);
        }

        @Override
        public void onStop() {
            super.onStop();
            EasyTracker.getInstance().activityStop(this);
        }

        @Override
        public void refreshContent() {
            startActivity(new Intent(this, getClass()));
            finish();
        }
    }

    public static abstract class AbstractListActivity extends SherlockListActivity implements MenuManagerInterface {

        private MenuManager actionBarManager = new MenuManager(this);

        public void createMenu() {
            actionBarManager.createMenu();
        }

        @Override
        protected void onResume() {
            super.onResume();
            getSupportActionBar().setSelectedNavigationItem(actionBarManager.getItemPositionForCurrentClass());
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getSupportMenuInflater();
            inflater.inflate(R.menu.mainmenu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            boolean retour = actionBarManager.onOptionsItemSelected(item);
            if (!retour) {
                retour = super.onOptionsItemSelected(item);
            }
            return retour;
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            actionBarManager.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        public void onStart() {
            super.onStart();
            EasyTracker.getInstance().activityStart(this);
        }

        @Override
        public void onStop() {
            super.onStop();
            EasyTracker.getInstance().activityStop(this);
        }

        @Override
        public void refreshContent() {
            startActivity(new Intent(this, getClass()));
            finish();
        }
    }

    private final static SparseArray<Class<? extends MenuManagerInterface>> arrayOfActivity = new SparseArray<Class<? extends MenuManagerInterface>>() {{
        put(R.id.menu_now, NowActivity.class);
        put(R.id.menu_cesoir, CeSoirActivity.class);
        put(R.id.menu_parchaine, ParChaineActivity.class);
    }};

    private int[] menuIds;

    private MenuManagerInterface menuManagerInterface;
    private Activity activity;

    public MenuManager(Activity activity) {
        this.menuManagerInterface = (MenuManagerInterface) activity;
        this.activity = activity;
    }

    public void createMenu() {

        menuIds = ArraysUtil.getIdsArray(activity, R.array.menu_principal_ids);

        Context context = menuManagerInterface.getSupportActionBar().getThemedContext();
        ArrayAdapter<CharSequence> listMenu = ArrayAdapter.createFromResource(context, R.array.menu_principal_chaines, R.layout.sherlock_spinner_item);
        listMenu.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        menuManagerInterface.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        menuManagerInterface.getSupportActionBar().setListNavigationCallbacks(listMenu, this);
        menuManagerInterface.getSupportActionBar().setSelectedNavigationItem(getItemPositionForCurrentClass());
        menuManagerInterface.getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Launch change log dialog
        ChangeLogDialog changeLogDialog = new ChangeLogDialog(activity);
        String currentVersion = changeLogDialog.getAppVersion();
        YboTvApplication application = (YboTvApplication) activity.getApplication();
        if (!application.getVersionInPref().equals(currentVersion)) {
            changeLogDialog.show();
            application.updateVersionInPref(currentVersion);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_preferences) {
            activity.startActivity(new Intent(activity, PreferenceActivity.class));
            return true;
        } else if (item.getItemId() == R.id.menu_filter_channels) {
            activity.startActivityForResult(new Intent(activity, FilterChannelsActivity.class), R.id.requestCode_filterChannels);
            return true;
        }
        return false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == R.id.requestCode_filterChannels) {
            activity.startActivityForResult(new Intent(activity, UpdateChannelsActivity.class), R.id.requestCode_updateChannels);
        }
        if (requestCode == R.id.requestCode_updateChannels) {
            menuManagerInterface.refreshContent();
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        startActivityIfNotAlreadyIn(arrayOfActivity.get(menuIds[itemPosition]));
        return true;
    }


    protected int getItemPositionForCurrentClass() {
        for (int index = 0; index < menuIds.length; index++) {
            if (menuIds[index] == menuManagerInterface.getMenuIdOfClass()) {
                return index;
            }
        }
        return -1;
    }

    protected void startActivityIfNotAlreadyIn(Class<? extends MenuManagerInterface> activityToStart) {
        if (this.activity.getClass() != activityToStart) {
            this.activity.finish();
            Intent intent = new Intent(this.activity, activityToStart);
            this.activity.startActivity(intent);
        }
    }


}
