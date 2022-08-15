package com.flatcode.littlemusicadmin.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemusicadmin.R;
import com.flatcode.littlemusicadmin.Unit.DATA;
import com.flatcode.littlemusicadmin.Unit.THEME;
import com.flatcode.littlemusicadmin.Unit.VOID;
import com.flatcode.littlemusicadmin.databinding.ActivitySongAddBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.HashMap;

public class SongAddActivity extends AppCompatActivity {

    private ActivitySongAddBinding binding;
    Activity activity = SongAddActivity.this;

    Uri audioUri = null;
    StorageTask uploadsTask;

    MediaMetadataRetriever metadataRetriever;
    //byte[] art;
    String nameSong, durations;//album_art = "",;

    private ProgressDialog dialog;

    private ArrayList<String> categoryId, categoryList, albumId, albumList, artistId, artistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivitySongAddBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait...");
        dialog.setCanceledOnTouchOutside(false);

        loadCategories();
        loadAlbums();
        loadArtists();

        binding.toolbar.nameSpace.setText(R.string.add_new_song);
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.category.setOnClickListener(v -> categoryPickDialog());
        binding.album.setOnClickListener(v -> albumPickDialog());
        binding.artist.setOnClickListener(v -> artistPickDialog());
        binding.chooseSong.setOnClickListener(v -> openAudioFiles());
        binding.toolbar.ok.setOnClickListener(v -> validateData());

        metadataRetriever = new MediaMetadataRetriever();
    }

    private String name = DATA.EMPTY;

    private void validateData() {
        //get data
        name = binding.nameEt.getText().toString().trim();

        //validate data
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(activity, "Enter Name...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedArtistTitle)) {
            Toast.makeText(activity, "Pick Artist...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedAlbumTitle)) {
            Toast.makeText(activity, "Pick Album...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedCategoryTitle)) {
            Toast.makeText(activity, "Pick Category...", Toast.LENGTH_SHORT).show();
        } else if (audioUri == null) {
            Toast.makeText(activity, "Pick Song...", Toast.LENGTH_SHORT).show();
        } else {
            uploadFileToDB();
        }
    }

    public void openAudioFiles() {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, 101);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data.getData() != null) {
                if (requestCode == 101) {
                    try {
                        audioUri = data.getData();
                        metadataRetriever.setDataSource(this, audioUri);
                        /*if (metadataRetriever.getEmbeddedPicture() != null) {
                        art = metadataRetriever.getEmbeddedPicture();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                        binding.albumArt.setImageBitmap(bitmap);
                        }*/
                        assert metadataRetriever != null;

                        nameSong = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                        durations = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                        binding.choose.setText(R.string.ok);
                        name = binding.nameEt.getText().toString().trim();
                        if (TextUtils.isEmpty(name))
                            binding.nameEt.setText(nameSong);
                        binding.duration.setText(VOID.convertDuration(Long.parseLong(durations)));
                        name = nameSong;
                    } catch (Exception e) {
                        Toast.makeText(activity, "Error!", Toast.LENGTH_SHORT).show();
                        audioUri = null;
                    }
                }
            } else {
                Toast.makeText(this, "Error ! ", Toast.LENGTH_SHORT).show();
                audioUri = null;
            }
        }
    }

    public void uploadFileToDB() {
        if (binding.choose.equals("No file Selected")) {
            Toast.makeText(this, "Please selected an image!", Toast.LENGTH_SHORT).show();
        } else {
            if (uploadsTask != null && uploadsTask.isInProgress()) {
                Toast.makeText(this, "Songs uploads in already progress!", Toast.LENGTH_SHORT).show();
            } else {
                uploadFile();
            }
        }
    }

    private void uploadFile() {
        Toast.makeText(this, "Uploads please wait!", Toast.LENGTH_SHORT).show();

        dialog.setMessage("Uploads Song...");
        dialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
        String id = ref.push().getKey();

        String filePathAndName = "Songs/" + selectedArtistTitle + "/" + id;

        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName);
        reference.putFile(audioUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            String uploadedImageUrl = "" + uriTask.getResult();
            dialog.dismiss();
            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();

            uploadInfoToDB(uploadedImageUrl, id, ref);
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(this, "Error ! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            dialog.setMessage("uploaded " + ((int) progress) + "%.....");
        });
    }

    private void uploadInfoToDB(String uploadedSongUrl, String id, DatabaseReference ref) {
        dialog.setMessage("Uploading song info...");
        dialog.show();

        //setup data to upload
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(DATA.PUBLISHER, DATA.EMPTY + DATA.FirebaseUserUid);
        hashMap.put(DATA.TIMESTAMP, System.currentTimeMillis());
        hashMap.put(DATA.ID, id);
        hashMap.put(DATA.NAME, DATA.EMPTY + name);
        hashMap.put(DATA.CATEGORY_ID, DATA.EMPTY + selectedCategoryId);
        hashMap.put(DATA.ARTIST_ID, DATA.EMPTY + selectedArtistId);
        hashMap.put(DATA.ALBUM_ID, DATA.EMPTY + selectedAlbumId);
        hashMap.put(DATA.DURATION, DATA.EMPTY + durations);
        hashMap.put(DATA.SONG_LINK, DATA.EMPTY + uploadedSongUrl);
        hashMap.put(DATA.EDITORS_CHOICE, DATA.ZERO);
        hashMap.put(DATA.LOVES_COUNT, DATA.ZERO);
        hashMap.put(DATA.VIEWS_COUNT, DATA.ZERO);

        //db reference: DB > Songs
        assert id != null;
        ref.child(id).setValue(hashMap).addOnSuccessListener(unused -> {
            if (selectedArtistId != null)
                VOID.incrementItemCount(DATA.ARTISTS, selectedArtistId, DATA.SONGS_COUNT);
            if (selectedCategoryId != null)
                VOID.incrementItemCount(DATA.CATEGORIES, selectedCategoryId, DATA.SONGS_COUNT);
            if (selectedAlbumId != null)
                VOID.incrementItemCount(DATA.ALBUMS, selectedAlbumId, DATA.SONGS_COUNT);
            dialog.dismiss();
            Toast.makeText(activity, "Successfully uploaded...", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Failure to upload to db due to :" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private String selectedCategoryId, selectedCategoryTitle;

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

    private String selectedAlbumId, selectedAlbumTitle;

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

    private String selectedArtistId, selectedArtistTitle;

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
}