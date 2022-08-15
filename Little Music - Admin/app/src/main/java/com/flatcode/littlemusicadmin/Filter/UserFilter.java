package com.flatcode.littlemusicadmin.Filter;

import android.widget.Filter;

import com.flatcode.littlemusicadmin.Adapter.UserAdapter;
import com.flatcode.littlemusicadmin.Model.User;

import java.util.ArrayList;

public class UserFilter extends Filter {

    ArrayList<User> list;
    UserAdapter adapter;

    public UserFilter(ArrayList<User> list, UserAdapter adapter) {
        this.list = list;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<User> filter = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getUsername().toUpperCase().contains(constraint)) {
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
        adapter.list = (ArrayList<User>) results.values;
        adapter.notifyDataSetChanged();
    }
}