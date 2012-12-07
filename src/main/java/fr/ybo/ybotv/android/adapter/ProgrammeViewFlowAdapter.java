package fr.ybo.ybotv.android.adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.activity.ProgrammeActivity;
import fr.ybo.ybotv.android.modele.Programme;
import fr.ybo.ybotv.android.util.GetView;
import org.taptwo.android.widget.TitleProvider;

public class ProgrammeViewFlowAdapter extends BaseAdapter implements TitleProvider {


    private LayoutInflater inflater;
    private Activity context;
    private Programme programme;

    private int[] titles = {R.string.resume, R.string.detail};


    public ProgrammeViewFlowAdapter(Activity context, Programme programme) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.programme = programme;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        switch (position) {
            case 0:
                convertView = getResumeView();
                break;
            case 1:
                convertView = getDetailView();
                break;
        }

        return convertView;
    }

    private View getDetailView() {
        final View view = inflater.inflate(R.layout.programme_detail, null);
        ProgrammeActivity.contructDetailView(context, new GetView() {
            @Override
            public View findViewById(int resource) {
                return view.findViewById(resource);
            }
        }, programme);
        return view;
    }

    private View getResumeView() {
        final View view = inflater.inflate(R.layout.programme_resume_withscroll, null);
        ProgrammeActivity.contructResumeView(context, new GetView() {
            @Override
            public View findViewById(int resource) {
                return view.findViewById(resource);
            }
        }, programme);
        return view;
    }



    /* (non-Javadoc)
    * @see org.taptwo.android.widget.TitleProvider#getTitle(int)
    */
    @Override
    public String getTitle(int position) {
        return context.getString(titles[position]);
    }
}
