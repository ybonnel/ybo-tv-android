package fr.ybo.ybotv.android.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.database.YboTvDatabase;
import fr.ybo.ybotv.android.lasylist.ImageLoader;
import fr.ybo.ybotv.android.modele.Channel;
import fr.ybo.ybotv.android.modele.ChannelWithProgramme;
import fr.ybo.ybotv.android.modele.FavoriteChannel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterChannelsAdapter extends BaseAdapter {

    private final List<Channel> channels;
    private final LayoutInflater inflater;

    public FilterChannelsAdapter(Context context, YboTvDatabase database) {
        channels = database.selectAll(Channel.class);

        Set<String> favoriteIds = new HashSet<String>();
        for (FavoriteChannel favoriteChannel : database.selectAll(FavoriteChannel.class)) {
            favoriteIds.add(favoriteChannel.getChannel());
        }
        for (Channel channel : channels) {
            channel.setFavorite(favoriteIds.contains(channel.getId()));
        }
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
        CheckBox favorite;
    }

    @Override
    public View getView(int position, View convertViewIn, ViewGroup parent) {
        View convertView = convertViewIn;
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.filter_channel_element, null);
            holder = new ViewHolder();
            holder.iconeChaine = (ImageView) convertView.findViewById(R.id.filter_channel_element_imageChaine);
            holder.displayName = (TextView) convertView.findViewById(R.id.filter_channel_element_displayName);
            holder.favorite = (CheckBox) convertView.findViewById(R.id.filter_channel_element_favorite);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Channel channel = getItem(position);

        holder.displayName.setText(channel.getDisplayName());
        holder.iconeChaine.setImageResource(channel.getIconResource());
        holder.favorite.setChecked(channel.isFavorite());
        return convertView;
    }
}
