package com.flatcode.littlemusicadmin.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jean.jcplayer.model.JcAudio;
import com.flatcode.littlemusicadmin.Adapter.AlbumAdapter;
import com.flatcode.littlemusicadmin.Adapter.SongAdapter;
import com.flatcode.littlemusicadmin.Model.Album;
import com.flatcode.littlemusicadmin.Model.Song;
import com.flatcode.littlemusicadmin.Unit.DATA;
import com.flatcode.littlemusicadmin.Unit.THEME;
import com.flatcode.littlemusicadmin.Unit.VOID;
import com.flatcode.littlemusicadmin.databinding.ActivityArtistSongsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

public class ArtistSongsActivity extends AppCompatActivity {

    private ActivityArtistSongsBinding binding;
    Activity activity = ArtistSongsActivity.this;

    ArrayList<Album> albumList;
    AlbumAdapter albumAdapter;

    ArrayList<Song> songList;
    SongAdapter songAdapter;

    Boolean isPlaying = false, isAlbum = true, isSong = false;
    ArrayList<JcAudio> jcAudios;
    private int currentSong;

    String artistId, artistName, artistImage, artistAbout, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivityArtistSongsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        artistId = getIntent().getStringExtra(DATA.ARTIST_ID);
        artistName = getIntent().getStringExtra(DATA.ARTIST_NAME);
        artistImage = getIntent().getStringExtra(DATA.ARTIST_IMAGE);
        artistAbout = getIntent().getStringExtra(DATA.ARTIST_ABOUT);

        binding.toolbar.nameSpace.setText(artistName);
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.switchBarAlbums.scrollSwitch.setVisibility(View.VISIBLE);
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
                    if (isAlbum)
                        albumAdapter.getFilter().filter(s);
                    else if (isSong)
                        songAdapter.getFilter().filter(s);
                } catch (Exception e) {
                    //None
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //binding.recyclerView.setHasFixedSize(true);
        albumList = new ArrayList<>();
        albumAdapter = new AlbumAdapter(activity, albumList);
        binding.recyclerAlbums.setAdapter(albumAdapter);

        init();

        binding.switchBarAlbums.songs.setOnClickListener(v -> {
            binding.switchBarAlbums.scrollSwitch.setVisibility(View.GONE);
            binding.switchBarSongs.scrollSwitch.setVisibility(View.VISIBLE);
            binding.player.jcPlayer.pause();
            binding.player.jcPlayer.setVisibility(View.GONE);
            albumList.clear();
            init();
            getSongs(type);
            isAlbum = false;
            isSong = true;
            if (DATA.searchStatus)
                onBackPressed();
        });
        //Albums
        binding.switchBarAlbums.aboutTheArtist.setOnClickListener(v -> VOID.dialogAboutArtist(activity
                , artistImage, artistName, artistAbout));
        binding.switchBarAlbums.all.setOnClickListener(v -> {
            type = DATA.TIMESTAMP;
            getAlbums(type);
        });
        binding.switchBarAlbums.mostSongs.setOnClickListener(v -> {
            type = DATA.SONGS_COUNT;
            getAlbums(type);
        });
        binding.switchBarAlbums.mostInterested.setOnClickListener(v -> {
            type = DATA.INTERESTED_COUNT;
            getAlbums(type);
        });
        binding.switchBarAlbums.name.setOnClickListener(v -> {
            type = DATA.NAME;
            getAlbums(type);
        });
        //Songs
        binding.switchBarSongs.albums.setOnClickListener(v -> {
            binding.switchBarSongs.scrollSwitch.setVisibility(View.GONE);
            binding.switchBarAlbums.scrollSwitch.setVisibility(View.VISIBLE);
            songList.clear();
            getAlbums(type);
            isAlbum = true;
            isSong = false;
            if (DATA.searchStatus)
                onBackPressed();
        });
        binding.switchBarSongs.aboutTheArtist.setOnClickListener(v -> VOID.dialogAboutArtist(activity
                , artistImage, artistName, artistAbout));
        binding.switchBarSongs.all.setOnClickListener(v -> {
            type = DATA.TIMESTAMP;
            init();
            getSongs(type);
        });
        binding.switchBarSongs.mostViews.setOnClickListener(v -> {
            type = DATA.VIEWS_COUNT;
            init();
            getSongs(type);
        });
        binding.switchBarSongs.mostLoves.setOnClickListener(v -> {
            type = DATA.LOVES_COUNT;
            init();
            getSongs(type);
        });
        binding.switchBarSongs.name.setOnClickListener(v -> {
            type = DATA.NAME;
            init();
            getSongs(type);
        });
    }

    private void init() {
        //binding.recyclerView.setHasFixedSize(true);
        songList = new ArrayList<>();
        jcAudios = new ArrayList<>();
        binding.recyclerSongs.setAdapter(songAdapter);
        songAdapter = new SongAdapter(activity, songList, (songs, position) -> {
            changeSelectedSong(position);
            binding.player.jcPlayer.playAudio(jcAudios.get(position));
            binding.player.jcPlayer.setVisibility(View.VISIBLE);
        });
    }

    private void getAlbums(String orderBy) {
        binding.recyclerSongs.setVisibility(View.GONE);
        Query ref = FirebaseDatabase.getInstance().getReference(DATA.ALBUMS);
        ref.orderByChild(orderBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                albumList.clear();
                int i = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Album item = data.getValue(Album.class);
                    assert item != null;
                    if (item.getArtistId().equals(artistId)) {
                        albumList.add(item);
                        i++;
                    }
                }

                Collections.reverse(albumList);
                binding.toolbar.number.setText(MessageFormat.format("( {0} )", i));
                albumAdapter.notifyDataSetChanged();
                binding.progress.setVisibility(View.GONE);
                if (!albumList.isEmpty()) {
                    binding.recyclerAlbums.setVisibility(View.VISIBLE);
                    binding.emptyText.setVisibility(View.GONE);
                } else {
                    binding.recyclerAlbums.setVisibility(View.GONE);
                    binding.emptyText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSongs(String orderBy) {
        changeSelectedSong(-1);
        binding.recyclerAlbums.setVisibility(View.GONE);
        Query ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
        ref.orderByChild(orderBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                songList.clear();
                int i = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Song item = data.getValue(Song.class);
                    assert item != null;
                    if (item.getId() != null) {
                        if (item.getArtistId().equals(artistId)) {
                            songList.add(item);
                            item.setKey(data.getKey());
                            currentSong = -1;
                            isPlaying = true;
                            jcAudios.add(JcAudio.createFromURL(item.getName(), item.getSongLink()));
                            i++;
                            binding.toolbar.number.setText(MessageFormat.format("( {0} )", i));
                            binding.recyclerSongs.setAdapter(songAdapter);
                        }
                    }
                }

                binding.toolbar.number.setText(MessageFormat.format("( {0} )", i));
                songAdapter.notifyDataSetChanged();
                binding.progress.setVisibility(View.GONE);
                if (!songList.isEmpty()) {
                    binding.recyclerSongs.setVisibility(View.VISIBLE);
                    binding.emptyText.setVisibility(View.GONE);
                } else {
                    binding.recyclerSongs.setVisibility(View.GONE);
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
        songAdapter.notifyItemChanged(songAdapter.getSelectedPosition());
        currentSong = index;
        songAdapter.setSelectedPosition(currentSong);
        songAdapter.notifyItemChanged(currentSong);
    }

    @Override
    public void onBackPressed() {
        if (DATA.searchStatus) {
            binding.toolbar.toolbar.setVisibility(View.VISIBLE);
            binding.toolbar.toolbarSearch.setVisibility(View.GONE);
            DATA.searchStatus = false;
            binding.toolbar.textSearch.setText(DATA.EMPTY);
        } else if (DATA.isChange) {
            onResume();
            DATA.isChange = false;
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
        getAlbums(type);
        super.onRestart();
    }

    @Override
    protected void onResume() {
        getAlbums(type);
        super.onResume();
    }
}