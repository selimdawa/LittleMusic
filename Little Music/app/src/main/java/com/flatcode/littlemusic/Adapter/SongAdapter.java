package com.flatcode.littlemusic.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.flatcode.littlemusic.Filter.SongFilter;
import com.flatcode.littlemusic.Model.Song;
import com.flatcode.littlemusic.Unit.CLASS;
import com.flatcode.littlemusic.Unit.DATA;
import com.flatcode.littlemusic.Unit.VOID;
import com.flatcode.littlemusic.databinding.ItemSongBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scwang.wave.MultiWaveHeader;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> implements Filterable {

    private ItemSongBinding binding;
    private final Context context;

    private int selectedPosition = -1;
    private final RecyclerItemClickListener listener;

    public ArrayList<Song> list, filterList;
    private SongFilter filter;

    public SongAdapter(Context context, ArrayList<Song> list, RecyclerItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.filterList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemSongBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Song item = list.get(position);
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();
        String artistId = DATA.EMPTY + item.getArtistId();
        String albumId = DATA.EMPTY + item.getAlbumId();
        String categoryId = DATA.EMPTY + item.getCategoryId();
        String nrLoves = DATA.EMPTY + item.getLovesCount();

        holder.name.setText(name);
        VOID.dataName(DATA.ARTISTS, artistId, holder.artist);
        VOID.dataName(DATA.ALBUMS, albumId, holder.album);
        VOID.dataName(DATA.CATEGORIES, categoryId, holder.category);
        String duration = VOID.convertDuration(Long.parseLong(item.getDuration()));
        holder.duration.setText(duration);

        holder.nrLoves.setText(nrLoves);
        VOID.isFavorite(holder.favorite, id, DATA.FirebaseUserUid);
        VOID.isLoves(holder.love, id);
        VOID.nrLoves(binding.nrLoves, id);

        holder.favorite.setOnClickListener(view -> VOID.checkFavorite(holder.favorite, id));
        holder.love.setOnClickListener(view -> VOID.checkLove(holder.love, id));

        IntentData(DATA.ARTISTS, artistId, DATA.ARTIST, holder.artist);
        IntentData(DATA.ALBUMS, albumId, DATA.ALBUM, holder.album);
        IntentData(DATA.CATEGORIES, categoryId, DATA.CATEGORY, holder.category);

        holder.bind(item, listener, id);

        if (getSelectedPosition() == position)
            open(holder.wave);
        else
            holder.wave.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new SongFilter(filterList, this);
        }
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, artist, album, category, duration, nrLoves;
        public ImageView favorite, love;
        public CardView card;
        public MultiWaveHeader wave;

        public ViewHolder(View view) {
            super(view);
            favorite = binding.favorite;
            love = binding.love;
            card = binding.card;
            wave = binding.wave;
            name = binding.name;
            artist = binding.artist;
            album = binding.album;
            category = binding.category;
            duration = binding.duration;
            nrLoves = binding.nrLoves;
        }

        public void bind(final Song getSongs, final RecyclerItemClickListener listener, String id) {
            card.setOnClickListener(view -> {
                listener.onClickListener(getSongs, getAdapterPosition());
                VOID.incrementViewCount(id);
            });
        }
    }

    public interface RecyclerItemClickListener {
        void onClickListener(Song songs, int postion);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    private void IntentData(String database, String dataId, String type, TextView text) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(database);
        reference.child(dataId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Name = DATA.EMPTY + snapshot.child(DATA.NAME).getValue();
                String Image = DATA.EMPTY + snapshot.child(DATA.IMAGE).getValue();
                if (type.equals(DATA.ARTIST))
                    text.setOnClickListener(v ->
                            VOID.IntentExtra2(context, CLASS.ARTIST_SONGS, DATA.ARTIST_ID, dataId, DATA.ARTIST_NAME, Name));
                if (type.equals(DATA.ALBUM))
                    text.setOnClickListener(v ->
                            VOID.IntentExtra3(context, CLASS.ALBUM_SONGS, DATA.ALBUM_ID, dataId, DATA.ALBUM_NAME, Name, DATA.ALBUM_IMAGE, Image));
                if (type.equals(DATA.CATEGORY))
                    text.setOnClickListener(v ->
                            VOID.IntentExtra2(context, CLASS.CATEGORY_SONGS, DATA.CATEGORY_ID, dataId, DATA.CATEGORY_NAME, Name));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void open(MultiWaveHeader wave) {
        wave.setVelocity(1);
        wave.setProgress(1);
        wave.isRunning();
        wave.setGradientAngle(45);
        wave.setWaveHeight(40);
        wave.setStartColor(Color.WHITE);
        wave.setCloseColor(Color.DKGRAY);
        wave.setVisibility(View.VISIBLE);
    }
}