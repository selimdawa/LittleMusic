package com.flatcode.littlemusicadmin.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemusicadmin.Model.Song;
import com.flatcode.littlemusicadmin.R;
import com.flatcode.littlemusicadmin.Unit.DATA;
import com.flatcode.littlemusicadmin.Unit.THEME;
import com.flatcode.littlemusicadmin.Unit.VOID;
import com.flatcode.littlemusicadmin.databinding.ActivitySongEditBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SongEditActivity extends AppCompatActivity {

    private ActivitySongEditBinding binding;
    Activity activity = SongEditActivity.this;

    private String songId, category, artist, album;

    private ArrayList<String> categoryList, categoryId;
    private ArrayList<String> artistList, artistId;
    private ArrayList<String> albumList, albumId;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivitySongEditBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        songId = getIntent().getStringExtra(DATA.SONG_ID);
        category = getIntent().getStringExtra(DATA.CATEGORY_ID);
        artist = getIntent().getStringExtra(DATA.ARTIST_ID);
        album = getIntent().getStringExtra(DATA.ALBUM_ID);

        dialog = new ProgressDialog(activity);
        dialog.setTitle("Please wait...");
        dialog.setCanceledOnTouchOutside(false);

        loadCategories();
        loadArtists();
        loadAlbums();
        loadInfo();

        binding.toolbar.nameSpace.setText(R.string.edit_song);
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.category.setOnClickListener(v -> categoryPickDialog());
        binding.artist.setOnClickListener(v -> artistPickDialog());
        binding.album.setOnClickListener(v -> albumPickDialog());
        binding.toolbar.ok.setOnClickListener(v -> validateData());
    }

    private String name = DATA.EMPTY;
    private String selectedCategoryId, selectedCategoryTitle;
    private String selectedArtistId, selectedArtistTitle;
    private String selectedAlbumId, selectedAlbumTitle;

    private void validateData() {
        //get data
        name = binding.nameEt.getText().toString().trim();

        //validate data
        if (TextUtils.isEmpty(name))
            Toast.makeText(activity, "Enter Name...", Toast.LENGTH_SHORT).show();
        else if (artist.isEmpty() && TextUtils.isEmpty(selectedArtistId))
            Toast.makeText(activity, "Enter Artist...", Toast.LENGTH_SHORT).show();
        else if (category.isEmpty() && TextUtils.isEmpty(selectedCategoryId))
            Toast.makeText(activity, "Enter Category...", Toast.LENGTH_SHORT).show();
        else if (album.isEmpty() && TextUtils.isEmpty(selectedAlbumId))
            Toast.makeText(activity, "Enter Album...", Toast.LENGTH_SHORT).show();
        else
            update();
    }

    private void update() {
        dialog.setMessage("Updating Song...");
        dialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(DATA.NAME, DATA.EMPTY + name);
        hashMap.put(DATA.CATEGORY_ID, DATA.EMPTY + selectedCategoryId);
        hashMap.put(DATA.ARTIST_ID, DATA.EMPTY + selectedArtistId);
        hashMap.put(DATA.ALBUM_ID, DATA.EMPTY + selectedAlbumId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
        reference.child(songId).updateChildren(hashMap).addOnSuccessListener(unused -> {
            dialog.dismiss();
            Toast.makeText(activity, "Song updated...", Toast.LENGTH_SHORT).show();
        }).addOnCompleteListener(task -> {
            if (!selectedCategoryId.equals(category)) {
                VOID.incrementItemCount(DATA.CATEGORIES, selectedCategoryId, DATA.SONGS_COUNT);
                if (category != null)
                    VOID.incrementItemRemoveCount(DATA.CATEGORIES, category, DATA.SONGS_COUNT);
            }
            if (!selectedArtistId.equals(artist)) {
                VOID.incrementItemCount(DATA.ARTISTS, selectedArtistId, DATA.SONGS_COUNT);
                if (artist != null)
                    VOID.incrementItemRemoveCount(DATA.ARTISTS, artist, DATA.SONGS_COUNT);
            }
            if (!selectedAlbumId.equals(album)) {
                VOID.incrementItemCount(DATA.ALBUMS, selectedAlbumId, DATA.SONGS_COUNT);
                if (album != null)
                    VOID.incrementItemRemoveCount(DATA.ALBUMS, album, DATA.SONGS_COUNT);
            }
            finish();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Failed to update db duo to : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
        reference.child(songId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Song item = snapshot.getValue(Song.class);
                assert item != null;
                selectedCategoryId = DATA.EMPTY + snapshot.child(DATA.CATEGORY_ID).getValue();
                selectedArtistId = DATA.EMPTY + snapshot.child(DATA.ARTIST_ID).getValue();
                selectedAlbumId = DATA.EMPTY + snapshot.child(DATA.ALBUM_ID).getValue();
                String name = item.getName();
                String duration = item.getDuration();

                binding.nameEt.setText(name);
                binding.duration.setText(VOID.convertDuration(Long.parseLong(duration)));

                DatabaseReference refCategory = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES);
                refCategory.child(selectedCategoryId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category
                        String category = DATA.EMPTY + snapshot.child(DATA.NAME).getValue();

                        binding.category.setText(category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                DatabaseReference refArtist = FirebaseDatabase.getInstance().getReference(DATA.ARTISTS);
                refArtist.child(selectedArtistId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get artist
                        String artist = DATA.EMPTY + snapshot.child(DATA.NAME).getValue();

                        binding.artist.setText(artist);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                DatabaseReference refAlbum = FirebaseDatabase.getInstance().getReference(DATA.ALBUMS);
                refAlbum.child(selectedAlbumId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get album
                        String album = DATA.EMPTY + snapshot.child(DATA.NAME).getValue();

                        binding.album.setText(album);
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

    private void loadCategories() {
        categoryList = new ArrayList<>();
        categoryId = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                categoryId.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String id = DATA.EMPTY + data.child(DATA.ID).getValue();
                    String name = DATA.EMPTY + data.child(DATA.NAME).getValue();

                    categoryList.add(name);
                    categoryId.add(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadArtists() {
        artistList = new ArrayList<>();
        artistId = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.ARTISTS);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                artistList.clear();
                artistId.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String id = DATA.EMPTY + data.child(DATA.ID).getValue();
                    String name = DATA.EMPTY + data.child(DATA.NAME).getValue();

                    artistList.add(name);
                    artistId.add(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadAlbums() {
        albumList = new ArrayList<>();
        albumId = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.ALBUMS);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                albumList.clear();
                albumId.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String id = DATA.EMPTY + data.child(DATA.ID).getValue();
                    String name = DATA.EMPTY + data.child(DATA.NAME).getValue();

                    albumList.add(name);
                    albumId.add(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void categoryPickDialog() {
        String[] categories = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            categories[i] = categoryList.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Pick Category").setItems(categories, (dialog, which) -> {
            selectedCategoryTitle = categoryList.get(which);
            selectedCategoryId = categoryId.get(which);
            binding.category.setText(selectedCategoryTitle);
        }).show();
    }

    private void artistPickDialog() {
        String[] artists = new String[artistList.size()];
        for (int i = 0; i < artistList.size(); i++) {
            artists[i] = artistList.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Pick Artist").setItems(artists, (dialog, which) -> {
            selectedArtistTitle = artistList.get(which);
            selectedArtistId = artistId.get(which);
            binding.artist.setText(selectedArtistTitle);
        }).show();
    }

    private void albumPickDialog() {
        String[] albums = new String[albumList.size()];
        for (int i = 0; i < albumList.size(); i++) {
            albums[i] = albumList.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Pick Album").setItems(albums, (dialog, which) -> {
            selectedAlbumTitle = albumList.get(which);
            selectedAlbumId = albumId.get(which);
            binding.album.setText(selectedAlbumTitle);
        }).show();
    }
}