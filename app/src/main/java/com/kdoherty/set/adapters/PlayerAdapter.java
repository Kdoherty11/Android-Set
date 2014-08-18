package com.kdoherty.set.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kdoherty.set.R;
import com.kdoherty.set.model.Player;

import java.util.List;

/**
 * Created by kdoherty on 8/15/14.
 */
public class PlayerAdapter extends BaseAdapter {

    List<Player> players;
    LayoutInflater mInflater;
    Context context;

    public PlayerAdapter(Context context, List<Player> players) {
        this.players = players;
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    private static class ViewHolder {
        public final TextView name;
        public final TextView score;

        public ViewHolder(TextView name, TextView score) {
            this.name = name;
            this.score = score;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("Getting view from position " + position);

        TextView name;
        TextView score;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.player, parent, false);
            name = (TextView) convertView.findViewById(R.id.player_name);
            score = (TextView) convertView.findViewById(R.id.player_score);
            convertView.setTag(new ViewHolder(name, score));
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            name = viewHolder.name;
            score = viewHolder.score;
        }

        Player player = getItem(position);
        name.setText(player.getName());
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        score.setText(String.valueOf(player.getSetCount()));
        score.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        return convertView;
    }
}
