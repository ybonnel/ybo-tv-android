package fr.ybo.ybotv.android.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.modele.Channel;

import java.util.List;

public class ChannelsAdapter extends BaseAdapter {

    private final List<Channel> channels;
    private final LayoutInflater inflater;

    public ChannelsAdapter(Context context, List<Channel> channels) {
        this.channels = channels;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return channels.size();
    }

    @Override
    public Channel getItem(int position) {
        return channels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    private static class ViewHolder {
        ImageView iconeChaine;
        TextView displayName;
    }

    @Override
    public View getView(int position, View convertViewIn, ViewGroup parent) {
        View convertView = convertViewIn;
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.channel, null);
            holder = new ViewHolder();
            holder.iconeChaine = (ImageView) convertView.findViewById(R.id.channel_imageChaine);
            holder.displayName = (TextView) convertView.findViewById(R.id.channel_displayName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Channel channel = getItem(position);

        holder.displayName.setText(channel.getDisplayName());
        holder.iconeChaine.setImageResource(channel.getIconResource());
        return convertView;
    }
}
