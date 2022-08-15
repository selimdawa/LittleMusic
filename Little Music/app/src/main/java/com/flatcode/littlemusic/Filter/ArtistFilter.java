package com.flatcode.littlemusic.Filter;

import android.widget.Filter;

import com.flatcode.littlemusic.Adapter.ArtistAdapter;
import com.flatcode.littlemusic.Model.Artist;

import java.util.ArrayList;

public class ArtistFilter extends Filter {

    ArrayList<Artist> list;
    ArtistAdapter adapter;

    public ArtistFilter(ArrayList<Artist> list, ArtistAdapter adapter) {
        this.list = list;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<Artist> filter = new ArrayList<>();
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
        adapter.list = (ArrayList<Artist>) results.values;
        adapter.notifyDataSetChanged();
    }
}