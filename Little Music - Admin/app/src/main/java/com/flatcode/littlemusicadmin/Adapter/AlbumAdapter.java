package com.flatcode.littlemusicadmin.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flatcode.littlemusicadmin.Filter.AlbumFilter;
import com.flatcode.littlemusicadmin.Model.Album;
import com.flatcode.littlemusicadmin.Unit.CLASS;
import com.flatcode.littlemusicadmin.Unit.DATA;
import com.flatcode.littlemusicadmin.Unit.VOID;
import com.flatcode.littlemusicadmin.databinding.ItemAlbumBinding;

import java.text.MessageFormat;
import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> implements Filterable {

    private ItemAlbumBinding binding;
    private final Activity activity;

    public ArrayList<Album> list, filterList;
    private AlbumFilter filter;

    public AlbumAdapter(Activity activity, ArrayList<Album> list) {
        this.activity = activity;
        this.list = list;
        this.filterList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemAlbumBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Album item = list.get(position);
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();
        String image = DATA.EMPTY + item.getImage();
        String artistId = DATA.EMPTY + item.getArtistId();
        String categoryId = DATA.EMPTY + item.getCategoryId();
        String interestedCount = DATA.EMPTY + item.getInterestedCount();
        String songsCount = DATA.EMPTY + item.getSongsCount();

        VOID.Glide(false, activity, image, holder.image);

        if (name.equals(DATA.EMPTY)) {
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

        holder.more.setOnClickListener(v -> VOID.moreDeleteAlbum(activity, item, DATA.ARTISTS, artistId
                , DATA.ALBUMS_COUNT, DATA.CATEGORIES, categoryId, DATA.ALBUMS_COUNT, DATA.NULL, DATA.NULL, DATA.NULL));

        holder.item.setOnClickListener(view ->
                VOID.IntentExtra3(activity, CLASS.ALBUM_SONGS,
                        DATA.ALBUM_ID, id, DATA.ALBUM_NAME, name, DATA.ALBUM_IMAGE, image));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new AlbumFilter(filterList, this);
        }
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ImageButton more;
        TextView name, numberSongs, numberInterested;
        LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            image = binding.image;
            name = binding.name;
            more = binding.more;
            numberSongs = binding.numberSongs;
            numberInterested = binding.numberInterested;
            item = binding.item;
        }
    }
}