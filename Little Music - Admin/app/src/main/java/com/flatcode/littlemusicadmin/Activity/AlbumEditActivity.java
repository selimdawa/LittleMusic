package com.flatcode.littlemusicadmin.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemusicadmin.Model.Album;
import com.flatcode.littlemusicadmin.R;
import com.flatcode.littlemusicadmin.Unit.DATA;
import com.flatcode.littlemusicadmin.Unit.THEME;
import com.flatcode.littlemusicadmin.Unit.VOID;
import com.flatcode.littlemusicadmin.databinding.ActivityAlbumAddBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;

public class AlbumEditActivity extends AppCompatActivity {

    private ActivityAlbumAddBinding binding;
    Activity activity = AlbumEditActivity.this;

    private String albumId, category, artist;

    private ArrayList<String> categoryList, categoryId;
    private ArrayList<String> artistList, artistId;

    private Uri imageUri;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivityAlbumAddBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        albumId = getIntent().getStringExtra(DATA.ALBUM_ID);
        category = getIntent().getStringExtra(DATA.CATEGORY_ID);
        artist = getIntent().getStringExtra(DATA.ARTIST_ID);

        dialog = new ProgressDialog(activity);
        dialog.setTitle("Please wait...");
        dialog.setCanceledOnTouchOutside(false);

        loadCategories();
        loadArtists();
        loadInfo();

        binding.toolbar.nameSpace.setText(R.string.edit_album);
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.category.setOnClickListener(v -> categoryPickDialog());
        binding.artist.setOnClickListener(v -> artistPickDialog());
        binding.image.setOnClickListener(v -> VOID.CropImageSquare(activity));
        binding.toolbar.ok.setOnClickListener(v -> validateData());
    }

    private String name = DATA.EMPTY;
    private String selectedCategoryId, selectedCategoryTitle;
    private String selectedArtistId, selectedArtistTitle;

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
        else
            update();
    }

    private void update() {
        dialog.setMessage("Updating Album image...");
        dialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(DATA.NAME, DATA.EMPTY + name);
        hashMap.put(DATA.CATEGORY_ID, DATA.EMPTY + selectedCategoryId);
        hashMap.put(DATA.ARTIST_ID, DATA.EMPTY + selectedArtistId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.ALBUMS);
        reference.child(albumId).updateChildren(hashMap).addOnSuccessListener(unused -> {
            dialog.dismiss();
            Toast.makeText(activity, "Album updated...", Toast.LENGTH_SHORT).show();
        }).addOnCompleteListener(task -> {
            if (!selectedCategoryId.equals(category)) {
                VOID.incrementItemCount(DATA.CATEGORIES, selectedCategoryId, DATA.ALBUMS_COUNT);
                if (category != null)
                    VOID.incrementItemRemoveCount(DATA.CATEGORIES, category, DATA.ALBUMS_COUNT);
            }
            if (!selectedArtistId.equals(artist)) {
                VOID.incrementItemCount(DATA.ARTISTS, selectedArtistId, DATA.ALBUMS_COUNT);
                if (artist != null)
                    VOID.incrementItemRemoveCount(DATA.ARTISTS, artist, DATA.ALBUMS_COUNT);
            }
            if (imageUri != null) {
                uploadImage();
            } else {
                finish();
            }
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Failed to update db duo to : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadImage() {
        dialog.setMessage("Updating Album...");
        dialog.show();

        String filePathAndName = "Images/Album/" + albumId;

        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName
                + DATA.DOT + VOID.getFileExtension(imageUri, activity));
        reference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            String uploadedImageUrl = DATA.EMPTY + uriTask.getResult();

            updateImageAlbum(uploadedImageUrl, albumId);
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Failed to upload image due to : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateImageAlbum(String imageUrl, String id) {
        dialog.setMessage("Updating image album...");
        dialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        if (imageUri != null) {
            hashMap.put(DATA.IMAGE, DATA.EMPTY + imageUrl);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.ALBUMS);
        reference.child(id).updateChildren(hashMap).addOnSuccessListener(unused -> {
            dialog.dismiss();
            Toast.makeText(activity, "Image updated...", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Failed to update db duo to " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.ALBUMS);
        reference.child(albumId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Album item = snapshot.getValue(Album.class);
                assert item != null;
                selectedCategoryId = DATA.EMPTY + snapshot.child(DATA.CATEGORY_ID).getValue();
                selectedArtistId = DATA.EMPTY + snapshot.child(DATA.ARTIST_ID).getValue();
                String name = item.getName();
                String image = item.getImage();

                VOID.Glide(true, activity, image, binding.image);
                binding.nameEt.setText(name);

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = CropImage.getPickImageResultUri(activity, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(activity, uri)) {
                imageUri = uri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                VOID.CropImageSquare(activity);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                binding.image.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error! " + error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}