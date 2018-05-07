package com.vanity.iqbal.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vanity.iqbal.R;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends BaseAdapter {
    private List<String> names;
    private List<String> scores;

    Context context;
    private static LayoutInflater inflater=null;

    public LeaderboardAdapter(Context context, List<String> names, List<String> scores) {
	   
	   this.context = context;
	   this.names = new ArrayList<>(names);
	   this.scores = new ArrayList<>(scores);
       inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
       return names.size(); //since names.size() = scores.size()
   }

    @Override
    public Object getItem(int position) {
       return position;
   }

    @Override
    public long getItemId(int position) {
       return position;
   }

    public class Holder
    {
       TextView tv_name;
       TextView tv_score;
       ImageView img;
    }

    private static int getColorBasedOnRank(int position) {
        switch (position) {
            case 0:
                return Color.rgb(255, 215, 0);
            case 1:
                return Color.rgb(255, 215, 0);
            case 2:
                return Color.rgb(255, 215, 0);
            case 3:
                return Color.rgb(238, 118, 0);
            case 4:
                return Color.rgb(255, 140, 0);
            case 5:
                return Color.rgb(227, 168, 105);
            case 6:
                return Color.rgb(139, 125, 107);
            case 7:
                return Color.rgb(205, 183, 158);
            case 8:
                return Color.rgb(238, 213, 183);
            default:
                return Color.rgb(255, 228, 196);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.leaderboard_list, null);
        holder.tv_name = (TextView) rowView.findViewById(R.id.tv_leader_name_entry);
        holder.tv_score = (TextView) rowView.findViewById(R.id.tv_leader_score_entry);
        holder.img = (ImageView) rowView.findViewById(R.id.image_Leader_entry);

        holder.tv_name.setText(names.get(position));
        holder.tv_score.setText(scores.get(position));
        holder.img.setBackgroundColor(getColorBasedOnRank(position));
        return rowView;
   }
}