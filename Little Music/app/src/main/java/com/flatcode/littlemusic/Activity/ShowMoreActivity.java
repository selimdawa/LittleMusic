package com.flatcode.littlemusic.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jean.jcplayer.model.JcAudio;
import com.flatcode.littlemusic.Adapter.SongAdapter;
import com.flatcode.littlemusic.Model.Song;
import com.flatcode.littlemusic.Unit.DATA;
import com.flatcode.littlemusic.Unit.THEME;
import com.flatcode.littlemusic.databinding.ActivityShowMoreBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.ArrayList;

public class ShowMoreActivity extends AppCompatActivity {

    private ActivityShowMoreBinding binding;
    Activity activity = ShowMoreActivity.this;

    ArrayList<Song> list;
    SongAdapter adapter;

    Boolean isPlaying = false;
    ArrayList<JcAudio> jcAudios;
    private int currentSong;

    String type, name, isReverse;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivityShowMoreBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        type = intent.getStringExtra(DATA.SHOW_MORE_TYPE);
        name = intent.getStringExtra(DATA.SHOW_MORE_NAME);
        isReverse = intent.getStringExtra(DATA.SHOW_MORE_BOOLEAN);

        binding.toolbar.nameSpace.setText(name);

        if (isReverse.equals("true")) {
            recyclerView = binding.recyclerViewReverse;
        } else if (isReverse.equals("false")) {
            recyclerView = binding.recyclerView;
        }

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

        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        //binding.recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        jcAudios = new ArrayList<>();
        recyclerView.setAdapter(adapter);
        adapter = new SongAdapter(activity, list, (songs, position) -> {
            changeSelectedSong(position);
            binding.player.jcPlayer.playAudio(jcAudios.get(position));
            binding.player.jcPlayer.setVisibility(View.VISIBLE);
        });
    }

    private void getData(String orderBy) {
        changeSelectedSong(-1);
        Query ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
        ref.orderByChild(orderBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                int i = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Song item = data.getValue(Song.class);
                    assert item != null;
                    if (orderBy.equals(DATA.EDITORS_CHOICE)) {
                        if (item.getEditorsChoice() > 0)
                            list.add(item);
                    } else
                        list.add(item);
                    item.setKey(data.getKey());
                    currentSong = -1;
                    isPlaying = true;
                    jcAudios.add(JcAudio.createFromURL(item.getName(), item.getSongLink()));
                    i++;
                    binding.toolbar.number.setText(MessageFormat.format("( {0} )", i));
                    recyclerView.setAdapter(adapter);
                }
                adapter.notifyDataSetChanged();
                binding.progress.setVisibility(View.GONE);
                if (!list.isEmpty()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    binding.emptyText.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    binding.emptyText.setVisibility(View.VISIBLE);
                }

                if (isPlaying) {
                    binding.player.jcPlayer.initPlaylist(jcAudios, null);
                } else {
                    Toast.makeText(activity, "There is no songs!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void changeSelectedSong(int index) {
        adapter.notifyItemChanged(adapter.getSelectedPosition());
        currentSong = index;
        adapter.setSelectedPosition(currentSong);
        adapter.notifyItemChanged(currentSong);
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
    protected void onPause() {
        binding.player.jcPlayer.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        binding.player.jcPlayer.pause();
        super.onStop();
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