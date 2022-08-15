package com.flatcode.littlemusic.Filter;

import android.widget.Filter;

import com.flatcode.littlemusic.Adapter.SongMainAdapter;
import com.flatcode.littlemusic.Model.Song;

import java.util.ArrayList;

public class SongMainFilter extends Filter {

    ArrayList<Song> list;
    SongMainAdapter adapter;

    public SongMainFilter(ArrayList<Song> list, SongMainAdapter adapter) {
        this.list = list;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<Song> filter = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().toUpperCase().contains(constraint)) {
                    filter.add(list.get(i));
                }
            }
            results.count = filter.size();
            results.values = filter;
        } else {
            results.count = list.size();
            results.values = list;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.list = (ArrayList<Song>) results.values;
        adapter.notifyDataSetChanged();
    }
}