package com.flatcode.littlemusic.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.jean.jcplayer.model.JcAudio;
import com.flatcode.littlemusic.Adapter.SongAdapter;
import com.flatcode.littlemusic.Model.Song;
import com.flatcode.littlemusic.Unit.DATA;
import com.flatcode.littlemusic.databinding.FragmentMySongsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class mySongsFragment extends Fragment {

    private FragmentMySongsBinding binding;

    List<String> check;
    ArrayList<Song> list;
    SongAdapter adapter;

    Boolean isPlaying = false;
    ArrayList<JcAudio> jcAudios;
    private int currentSong;

    private String type;
    private static String DB;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMySongsBinding.inflate(LayoutInflater.from(getContext()), container, false);

        type = DATA.TIMESTAMP;
        DB = DATA.ARTISTS;

        init();

        binding.switchBar.all.setOnClickListener(v -> {
            type = DATA.TIMESTAMP;
            init();
            getData(type, DB);
        });
        binding.switchBar.mostViews.setOnClickListener(v -> {
            type = DATA.VIEWS_COUNT;
            init();
            getData(type, DB);
        });
        binding.switchBar.mostLoves.setOnClickListener(v -> {
            type = DATA.LOVES_COUNT;
            init();
            getData(type, DB);
        });
        binding.switchBar.name.setOnClickListener(v -> {
            type = DATA.NAME;
            init();
            getData(type, DB);
        });
        //Switch Type
        binding.switchBar.artists.setOnClickListener(v -> {
            type = DATA.NAME;
            init();
            DB = DATA.ARTISTS;
            getData(type, DB);
        });
        binding.switchBar.albums.setOnClickListener(v -> {
            type = DATA.NAME;
            init();
            DB = DATA.ALBUMS;
            getData(type, DB);
        });
        binding.switchBar.categories.setOnClickListener(v -> {
            type = DATA.NAME;
            init();
            DB = DATA.CATEGORIES;
            getData(type, DB);
        });

        return binding.getRoot();
    }

    private void init() {
        //binding.recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        jcAudios = new ArrayList<>();
        binding.recyclerView.setAdapter(adapter);
        adapter = new SongAdapter(getContext(), list, (songs, position) -> {
            changeSelectedSong(position);
            binding.player.jcPlayer.playAudio(jcAudios.get(position));
            binding.player.jcPlayer.setVisibility(View.VISIBLE);
        });
    }

    private void getData(String orderBy, String typeDB) {
        check = new ArrayList();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
                .child(DATA.FirebaseUserUid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                check.clear();
                for (DataSnapshot snapshot : dataSnapshot.child(typeDB).getChildren())
                    check.add(snapshot.getKey());
                getItems(orderBy, typeDB);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getItems(String orderBy, String typeDB) {
        changeSelectedSong(-1);
        Query ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
        ref.orderByChild(orderBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Song song = snapshot.getValue(Song.class);
                    for (String id : check) {
                        assert song != null;
                        if (song.getId() != null) {
                            if (typeDB.equals(DATA.ARTISTS)) {
                                if (song.getArtistId().equals(id)) {
                                    list.add(song);
                                    song.setKey(snapshot.getKey());
                                    currentSong = -1;
                                    isPlaying = true;
                                    jcAudios.add(JcAudio.createFromURL(song.getName(), song.getSongLink()));
                                    binding.recyclerView.setAdapter(adapter);
                                }
                            } else if (typeDB.equals(DATA.ALBUMS)) {
                                if (song.getAlbumId().equals(id)) {
                                    list.add(song);
                                    song.setKey(snapshot.getKey());
                                    currentSong = -1;
                                    isPlaying = true;
                                    jcAudios.add(JcAudio.createFromURL(song.getName(), song.getSongLink()));
                                    binding.recyclerView.setAdapter(adapter);
                                }
                            } else if (typeDB.equals(DATA.CATEGORIES)) {
                                if (song.getCategoryId().equals(id)) {
                                    list.add(song);
                                    song.setKey(snapshot.getKey());
                                    currentSong = -1;
                                    isPlaying = true;
                                    jcAudios.add(JcAudio.createFromURL(song.getName(), song.getSongLink()));
                                    binding.recyclerView.setAdapter(adapter);
                                }
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                binding.progress.setVisibility(View.GONE);
                if (!list.isEmpty()) {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.emptyText.setVisibility(View.GONE);
                } else {
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.emptyText.setVisibility(View.VISIBLE);
                }

                if (isPlaying) {
                    binding.player.jcPlayer.initPlaylist(jcAudios, null);
                } else {
                    Toast.makeText(getContext(), "There is no songs!", Toast.LENGTH_SHORT).show();
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
    public void onPause() {
        binding.player.jcPlayer.pause();
        super.onPause();
    }

    @Override
    public void onStop() {
        binding.player.jcPlayer.pause();
        super.onStop();
    }

    @Override
    public void onResume() {
        getData(type, DATA.ARTISTS);
        super.onResume();
    }
}