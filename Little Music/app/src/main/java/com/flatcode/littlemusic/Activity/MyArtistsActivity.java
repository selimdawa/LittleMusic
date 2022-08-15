package com.flatcode.littlemusic.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemusic.Adapter.ArtistAdapter;
import com.flatcode.littlemusic.Model.Artist;
import com.flatcode.littlemusic.R;
import com.flatcode.littlemusic.Unit.CLASS;
import com.flatcode.littlemusic.Unit.DATA;
import com.flatcode.littlemusic.Unit.THEME;
import com.flatcode.littlemusic.Unit.VOID;
import com.flatcode.littlemusic.databinding.ActivityMyArtistsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyArtistsActivity extends AppCompatActivity {

    private ActivityMyArtistsBinding binding;
    Activity activity = MyArtistsActivity.this;

    List<String> item;
    ArrayList<Artist> list;
    ArtistAdapter adapter;

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivityMyArtistsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.toolbar.nameSpace.setText(R.string.my_artists);
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        type = DATA.TIMESTAMP;

        binding.toolbar.search.setOnClickListener(v -> {
            binding.toolbar.toolbar.setVisibility(View.GONE);
            binding.toolbar.toolbarSearch.setVisibility(View.VISIBLE);
            DATA.searchStatus = true;
        });

        binding.toolbar.close.setOnClickListener(v -> onBackPressed());

        binding.toolbar.textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapter.getFilter().filter(s);
                } catch (Exception e) {
                    //None
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //binding.recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new ArtistAdapter(activity, list);
        binding.recyclerView.setAdapter(adapter);

        binding.switchBar.explore.setOnClickListener(v -> VOID.Intent(activity, CLASS.ARTISTS));
        binding.switchBar.all.setOnClickListener(v -> {
            type = DATA.TIMESTAMP;
            getData(type);
        });
        binding.switchBar.mostSongs.setOnClickListener(v -> {
            type = DATA.SONGS_COUNT;
            getData(type);
        });
        binding.switchBar.mostAlbums.setOnClickListener(v -> {
            type = DATA.ALBUMS_COUNT;
            getData(type);
        });
        binding.switchBar.mostInterested.setOnClickListener(v -> {
            type = DATA.INTERESTED_COUNT;
            getData(type);
        });
        binding.switchBar.name.setOnClickListener(v -> {
            type = DATA.NAME;
            getData(type);
        });
    }

    private void getData(String orderBy) {
        item = new ArrayList();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
                .child(DATA.FirebaseUserUid).child(DATA.ARTISTS);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                item.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    item.add(snapshot.getKey());
                }
                getItems(orderBy);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getItems(String orderBy) {
        Query ref = FirebaseDatabase.getInstance().getReference(DATA.ARTISTS);
        ref.orderByChild(orderBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                int i = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Artist artist = data.getValue(Artist.class);
                    for (String id : item) {
                        assert artist != null;
                        if (artist.getId() != null)
                            if (artist.getId().equals(id)) {
                                list.add(artist);
                                i++;
                            }
                    }
                }
                Collections.reverse(list);
                binding.toolbar.number.setText(MessageFormat.format("( {0} )", i));
                adapter.notifyDataSetChanged();
                binding.progress.setVisibility(View.GONE);
                if (!list.isEmpty()) {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.emptyText.setVisibility(View.GONE);
                } else {
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.emptyText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (DATA.searchStatus) {
            binding.toolbar.toolbar.setVisibility(View.VISIBLE);
            binding.toolbar.toolbarSearch.setVisibility(View.GONE);
            DATA.searchStatus = false;
            binding.toolbar.textSearch.setText(DATA.EMPTY);
        } else
            super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        getData(type);
        super.onRestart();
    }

    @Override
    protected void onResume() {
        getData(type);
        super.onResume();
    }
}