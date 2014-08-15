package com.kdoherty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kdoherty.model.Player;
import com.kdoherty.set.R;

import java.util.List;

/**
 * Created by kdoherty on 8/15/14.
 */
public class PlayerAdapter extends BaseAdapter {

    List<Player> players;
    LayoutInflater mInflater;
    int mResource;
    int mTextResId;

    public PlayerAdapter(Context context, List<Player> players) {
        super();
        mInflater = LayoutInflater.from(context);
        this.players = players;
        System.out.println("Created playerAdapter with list of players: " + players);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Player getItem(int i) {
        return players.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("Getting view from position " + position);
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.player, null);
            TextView playerName = (TextView) convertView.findViewById(R.id.player_name);
            TextView playerScore = (TextView) convertView.findViewById(R.id.player_score);
            Player player = getItem(position);
            playerName.setText(player.getName());
            playerScore.setText(player.getSetCount());
        }

        return convertView;
    }
}
