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

import com.flatcode.littlemusicadmin.Filter.ArtistFilter;
import com.flatcode.littlemusicadmin.Model.Artist;
import com.flatcode.littlemusicadmin.Unit.CLASS;
import com.flatcode.littlemusicadmin.Unit.DATA;
import com.flatcode.littlemusicadmin.Unit.VOID;
import com.flatcode.littlemusicadmin.databinding.ItemArtistBinding;

import java.text.MessageFormat;
import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> implements Filterable {

    private ItemArtistBinding binding;
    private final Activity activity;

    public ArrayList<Artist> list, filterList;
    private ArtistFilter filter;

    public ArtistAdapter(Activity activity, ArrayList<Artist> list) {
        this.activity = activity;
        this.list = list;
        this.filterList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemArtistBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Artist item = list.get(position);
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();
        String image = DATA.EMPTY + item.getImage();
        String aboutTheArtist = DATA.EMPTY + item.getAboutTheArtist();
        String interestedCount = DATA.EMPTY + item.getInterestedCount();
        String albumCount = DATA.EMPTY + item.getAlbumsCount();
        String songsCount = DATA.EMPTY + item.getSongsCount();

        VOID.Glide(true, activity, image, holder.image);

        if (item.getName().equals(DATA.EMPTY)) {
            holder.name.setVisibility(View.GONE);
        } else {
            holder.name.setVisibility(View.VISIBLE);
            holder.name.setText(name);
        }

        if (albumCount.equals(DATA.EMPTY))
            holder.numberAlbums.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO));
        else
            holder.numberAlbums.setText(albumCount);

        if (songsCount.equals(DATA.EMPTY))
            holder.numberSongs.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO));
        else
            holder.numberSongs.setText(songsCount);

        holder.more.setOnClickListener(v -> VOID.moreDeleteArtist(activity, item, DATA.NULL, DATA.NULL, DATA.NULL
                , DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL));

        holder.item.setOnClickListener(view ->
                VOID.IntentExtra4(activity, CLASS.ARTIST_SONGS, DATA.ARTIST_ID, id, DATA.ARTIST_NAME,
                        name, DATA.ARTIST_IMAGE, image, DATA.ARTIST_ABOUT, aboutTheArtist));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ArtistFilter(filterList, this);
        }
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ImageButton more;
        TextView name, numberSongs, numberAlbums;
        LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            image = binding.image;
            name = binding.name;
            more = binding.more;
            numberSongs = binding.numberSongs;
            numberAlbums = binding.numberAlbums;
            item = binding.item;
        }
    }
}