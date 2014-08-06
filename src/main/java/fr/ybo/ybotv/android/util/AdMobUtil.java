package fr.ybo.ybotv.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;

import java.util.List;

public class AdMobUtil {

    private final static String PACKAGE_PRO = "fr.ybo.ybotv.android.pro";

    public static void manageAds(Activity activity) {
        final PackageManager pm = activity.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm
                .getInstalledApplications(PackageManager.GET_META_DATA);

        boolean yboTvProFound = false;

        for (ApplicationInfo info : packages) {
            if (PACKAGE_PRO.equals(info.packageName)) {
                yboTvProFound = true;
                break;
            }
        }

        // Look up the AdView as a resource and load a request.
        AdView adView = (AdView)activity.findViewById(R.id.adView);
        if (yboTvProFound) {
            View layout = activity.findViewById(R.id.ad_container);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();
            layoutParams.bottomMargin = 0;
            layout.setLayoutParams(layoutParams);
            adView.setVisibility(View.GONE);
        } else {
            adView.loadAd(new AdRequest());
        }
    }

    public static void manageAds(Context context, ViewGroup viewGroup) {
        final PackageManager pm = context.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm
                .getInstalledApplications(PackageManager.GET_META_DATA);

        boolean yboTvProFound = false;

        for (ApplicationInfo info : packages) {
            if (PACKAGE_PRO.equals(info.packageName)) {
                yboTvProFound = true;
                break;
            }
        }

        // Look up the AdView as a resource and load a request.
        AdView adView = (AdView)viewGroup.findViewById(R.id.adView);
        if (yboTvProFound) {
            View layout = viewGroup.findViewById(R.id.ad_container);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();
            layoutParams.bottomMargin = 0;
            layout.setLayoutParams(layoutParams);
            adView.setVisibility(View.GONE);
        } else {
            adView.loadAd(new AdRequest());
        }
    }
}
