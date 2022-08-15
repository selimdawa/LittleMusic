package com.flatcode.littlemusicadmin.Adapter;

import android.app.Activity;
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

import com.flatcode.littlemusicadmin.Filter.CategoryFilter;
import com.flatcode.littlemusicadmin.Model.Category;
import com.flatcode.littlemusicadmin.Unit.CLASS;
import com.flatcode.littlemusicadmin.Unit.DATA;
import com.flatcode.littlemusicadmin.Unit.VOID;
import com.flatcode.littlemusicadmin.databinding.ItemCategoryBinding;

import java.text.MessageFormat;
import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> implements Filterable {

    private ItemCategoryBinding binding;
    private final Activity activity;

    public ArrayList<Category> list, filterList;
    private CategoryFilter filter;

    public CategoryAdapter(Activity activity, ArrayList<Category> list) {
        this.activity = activity;
        this.list = list;
        this.filterList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemCategoryBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Category item = list.get(position);
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();
        String image = DATA.EMPTY + item.getImage();
        String interestedCount = DATA.EMPTY + item.getInterestedCount();
        String albumCount = DATA.EMPTY + item.getAlbumsCount();
        String songsCount = DATA.EMPTY + item.getSongsCount();

        VOID.Glide(false, activity, image, holder.image);

        if (item.getName().equals(DATA.EMPTY)) {
            holder.name.setVisibility(View.GONE);
        } else {
            holder.name.setVisibility(View.VISIBLE);
            holder.name.setText(name);
        }

        if (interestedCount.equals(DATA.EMPTY))
            holder.numberInterested.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO));
        else
            holder.numberInterested.setText(interestedCount);

        if (songsCount.equals(DATA.EMPTY))
            holder.numberSongs.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO));
        else
            holder.numberSongs.setText(songsCount);

        if (albumCount.equals(DATA.EMPTY))
            holder.numberAlbums.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO));
        else
            holder.numberAlbums.setText(albumCount);

        holder.more.setOnClickListener(v -> VOID.moreDeleteCategory(activity, item, DATA.NULL, DATA.NULL, DATA.NULL
                , DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL));

        holder.item.setOnClickListener(view ->
                VOID.IntentExtra2(activity, CLASS.CATEGORY_SONGS, DATA.CATEGORY_ID, id, DATA.CATEGORY_NAME, name));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CategoryFilter(filterList, this);
        }
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image, more;
        TextView name, numberSongs, numberAlbums, numberInterested;
        LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            image = binding.image;
            name = binding.name;
            more = binding.more;
            numberSongs = binding.numberSongs;
            numberAlbums = binding.numberAlbums;
            numberInterested = binding.numberInterested;
            item = binding.item;
        }
    }
}