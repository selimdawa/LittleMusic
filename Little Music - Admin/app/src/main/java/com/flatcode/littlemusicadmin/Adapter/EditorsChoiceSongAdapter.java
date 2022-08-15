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

import com.flatcode.littlemusicadmin.Filter.EditorsChoiceFilter;
import com.flatcode.littlemusicadmin.Model.Song;
import com.flatcode.littlemusicadmin.Unit.DATA;
import com.flatcode.littlemusicadmin.Unit.VOID;
import com.flatcode.littlemusicadmin.databinding.ItemEditorsChoiceBinding;

import java.util.ArrayList;

public class EditorsChoiceSongAdapter extends RecyclerView.Adapter<EditorsChoiceSongAdapter.ViewHolder> implements Filterable {

    private ItemEditorsChoiceBinding binding;
    private final Activity activity;

    public ArrayList<Song> list, filterList;
    private EditorsChoiceFilter filter;
    public int number;
    public String oldId;

    public EditorsChoiceSongAdapter(Activity activity, String oldId, ArrayList<Song> list, int number) {
        this.oldId = oldId;
        this.activity = activity;
        this.list = list;
        this.filterList = list;
        this.number = number;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemEditorsChoiceBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Song item = list.get(position);
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();
        String nrViews = DATA.EMPTY + item.getViewsCount();
        String nrLoves = DATA.EMPTY + item.getLovesCount();
        String artistId = DATA.EMPTY + item.getArtistId();
        String albumId = DATA.EMPTY + item.getAlbumId();
        String categoryId = DATA.EMPTY + item.getCategoryId();

        if (name.equals(DATA.EMPTY)) {
            holder.name.setVisibility(View.GONE);
        } else {
            holder.name.setVisibility(View.VISIBLE);
            holder.name.setText(name);
        }

        holder.nrViews.setText(nrViews);
        holder.nrLoves.setText(nrLoves);

        VOID.dataName(DATA.ARTISTS, artistId, holder.artist);
        VOID.dataName(DATA.ALBUMS, albumId, holder.album);
        VOID.dataName(DATA.CATEGORIES, categoryId, holder.category);

        holder.add.setOnClickListener(view -> {
            if (oldId != null) {
                VOID.addToEditorsChoice(activity, activity, id, number);
                VOID.addToEditorsChoice(activity, activity, oldId, 0);
            } else {
                VOID.addToEditorsChoice(activity, activity, id, number);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new EditorsChoiceFilter(filterList, this);
        }
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView add;
        TextView name, nrViews, nrLoves, artist, album, category;
        LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            name = binding.name;
            nrViews = binding.nrViews;
            nrLoves = binding.nrLoves;
            add = binding.add;
            artist = binding.artist;
            album = binding.album;
            category = binding.category;
            item = binding.item;
        }
    }
}