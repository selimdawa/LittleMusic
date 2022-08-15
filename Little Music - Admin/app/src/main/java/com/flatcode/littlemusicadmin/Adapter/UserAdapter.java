package com.flatcode.littlemusicadmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flatcode.littlemusicadmin.Filter.UserFilter;
import com.flatcode.littlemusicadmin.Model.User;
import com.flatcode.littlemusicadmin.Unit.CLASS;
import com.flatcode.littlemusicadmin.Unit.DATA;
import com.flatcode.littlemusicadmin.Unit.VOID;
import com.flatcode.littlemusicadmin.databinding.ItemUserBinding;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements Filterable {

    private ItemUserBinding binding;
    private final Context context;

    public ArrayList<User> list, filterList;
    private UserFilter filter;

    public UserAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
        this.filterList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemUserBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final User item = list.get(position);
        String id = item.getId();
        String image = item.getProfileImage();

        VOID.Glide(true, context, image, holder.image);

        if (item.getUsername().equals(DATA.EMPTY)) {
            holder.username.setVisibility(View.GONE);
        } else {
            holder.username.setVisibility(View.VISIBLE);
            holder.username.setText(item.getUsername());
        }

        holder.item.setOnClickListener(view -> VOID.IntentExtra(context, CLASS.PROFILE, DATA.PROFILE_ID, id));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new UserFilter(filterList, this);
        }
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView username;
        public LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            this.image = binding.imageProfile;
            this.username = binding.username;
            this.item = binding.item;
        }
    }
}