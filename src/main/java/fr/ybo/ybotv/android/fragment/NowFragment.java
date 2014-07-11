package fr.ybo.ybotv.android.fragment;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.activity.ListProgrammeManager;
import fr.ybo.ybotv.android.activity.MainActivity;
import fr.ybo.ybotv.android.modele.ChannelWithProgramme;
import fr.ybo.ybotv.android.util.AdMobUtil;

public class NowFragment extends Fragment implements ListProgrammeManager.GetProgramme {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.grid, container, false);

        GridView gridView = (GridView) viewGroup.findViewById(R.id.grid);

        if (((YboTvApplication)getActivity().getApplication()).isTablet()) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                gridView.setNumColumns(3);
            } else {
                gridView.setNumColumns(2);
            }
        }

        ListProgrammeManager listProgrammeManager = new ListProgrammeManager(
                gridView, getActivity(), this);
        listProgrammeManager.constructAdapter();

        AdMobUtil.manageAds(getActivity(), viewGroup);

        return viewGroup;
    }

    @Override
    public List<ChannelWithProgramme> getProgrammes() {
        return ChannelWithProgramme.getCurrentProgrammes((YboTvApplication) getActivity().getApplication());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity)activity).onSectionAttached(R.string.now);
    }
}
