package com.flatcode.littlemusicadmin.Filter;

import android.widget.Filter;

import com.flatcode.littlemusicadmin.Adapter.AlbumAdapter;
import com.flatcode.littlemusicadmin.Model.Album;

import java.util.ArrayList;

public class AlbumFilter extends Filter {

    ArrayList<Album> list;
    AlbumAdapter adapter;

    public AlbumFilter(ArrayList<Album> list, AlbumAdapter adapter) {
        this.list = list;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<Album> filter = new ArrayList<>();
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
        adapter.list = (ArrayList<Album>) results.values;
        adapter.notifyDataSetChanged();
    }
}