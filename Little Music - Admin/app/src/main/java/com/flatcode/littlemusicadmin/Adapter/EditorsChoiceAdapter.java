package com.flatcode.littlemusicadmin.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.flatcode.littlemusicadmin.Model.EditorsChoice;
import com.flatcode.littlemusicadmin.Model.Song;
import com.flatcode.littlemusicadmin.Unit.CLASS;
import com.flatcode.littlemusicadmin.Unit.DATA;
import com.flatcode.littlemusicadmin.Unit.VOID;
import com.flatcode.littlemusicadmin.databinding.ItemSongEditorsChoiceBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.List;

public class EditorsChoiceAdapter extends RecyclerView.Adapter<EditorsChoiceAdapter.ViewHolder> {

    private ItemSongEditorsChoiceBinding binding;
    private final Activity activity;
    public List<EditorsChoice> list;

    public EditorsChoiceAdapter(Activity activity, List<EditorsChoice> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemSongEditorsChoiceBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int id = position + 1;
        String editorsChoiceId = DATA.EMPTY + id;

        loadMusicDetails(id, editorsChoiceId, holder.name, holder.nrViews, holder.nrLoves, holder.artist,
                holder.album, holder.category, holder.remove, holder.change, holder.addCard, holder.detailsCard);

        holder.numberEditorsChoice.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, id));
        holder.add.setOnClickListener(v ->
                VOID.IntentExtra2(activity, CLASS.EDITORS_CHOICE_ADD, DATA.EDITORS_CHOICE_ID, editorsChoiceId, DATA.OLD_ID, null));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView add, remove, change;
        TextView name, album, artist, nrLoves, nrViews, category, numberEditorsChoice;
        LinearLayout item, item2;
        CardView addCard, detailsCard;

        public ViewHolder(View view) {
            super(view);
            nrLoves = binding.nrLoves;
            nrViews = binding.nrViews;
            name = binding.name;
            album = binding.album;
            artist = binding.artist;
            category = binding.category;
            item = binding.item;
            item2 = binding.item2;
            add = binding.add;
            numberEditorsChoice = binding.numberEditorsChoice;
            addCard = binding.addCard;
            detailsCard = binding.detailsCard;
            remove = binding.remove;
            change = binding.change;
        }
    }

    private void loadMusicDetails(int i, String position, TextView title, TextView viewsCount
            , TextView lovesCount, TextView artist, TextView album, TextView category, ImageView remove
            , ImageView change, CardView addCard, CardView detailsCard) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Song item = snapshot.getValue(Song.class);
                    assert item != null;
                    if (item.getEditorsChoice() == i) {
                        String id = DATA.EMPTY + item.getId();
                        String name = DATA.EMPTY + item.getName();
                        loadData(id);
                        addCard.setVisibility(View.GONE);
                        detailsCard.setVisibility(View.VISIBLE);
                        remove.setVisibility(View.VISIBLE);
                        change.setVisibility(View.VISIBLE);
                        remove.setOnClickListener(v -> VOID.dialogOptionDelete(activity, id, name
                                , DATA.EDITORS_CHOICE, DATA.EDITORS_CHOICE, true, DATA.NULL, DATA.NULL
                                , DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL));
                        change.setOnClickListener(v -> VOID.IntentExtra2(activity, CLASS.EDITORS_CHOICE_ADD,
                                DATA.EDITORS_CHOICE_ID, position, DATA.OLD_ID, id));
                    } else {
                        addCard.setVisibility(View.VISIBLE);
                        detailsCard.setVisibility(View.GONE);
                        remove.setVisibility(View.GONE);
                        change.setVisibility(View.GONE);
                    }
                }
            }

            private void loadData(String id) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
                ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //get data
                        Song item = dataSnapshot.getValue(Song.class);
                        assert item != null;

                        String name = DATA.EMPTY + item.getName();
                        String ViewsCount = DATA.EMPTY + item.getViewsCount();
                        String LovesCount = DATA.EMPTY + item.getLovesCount();
                        String artistId = DATA.EMPTY + item.getArtistId();
                        String albumId = DATA.EMPTY + item.getAlbumId();
                        String categoryId = DATA.EMPTY + item.getCategoryId();

                        title.setText(name);
                        viewsCount.setText(ViewsCount);
                        lovesCount.setText(LovesCount);
                        addCard.setVisibility(View.GONE);
                        detailsCard.setVisibility(View.VISIBLE);
                        remove.setVisibility(View.VISIBLE);
                        change.setVisibility(View.VISIBLE);

                        VOID.dataName(DATA.ARTISTS, artistId, artist);
                        VOID.dataName(DATA.ALBUMS, albumId, album);
                        VOID.dataName(DATA.CATEGORIES, categoryId, category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}