package com.flatcode.littlemusicadmin.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

public class AlbumAddActivity extends AppCompatActivity {

    private ActivityAlbumAddBinding binding;
    Activity activity;
    Context context = activity = AlbumAddActivity.this;

    private Uri imageUri = null;
    private ProgressDialog dialog;

    private ArrayList<String> categoryId, categoryList;
    private ArrayList<String> artistId, artistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(context);
        super.onCreate(savedInstanceState);
        binding = ActivityAlbumAddBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        dialog = new ProgressDialog(context);
        dialog.setTitle("Please wait...");
        dialog.setCanceledOnTouchOutside(false);

        loadCategories();
        loadArtists();

        binding.toolbar.nameSpace.setText(R.string.add_new_album);
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.image.setOnClickListener(v -> VOID.CropImageSquare(activity));
        binding.category.setOnClickListener(v -> categoryPickDialog());
        binding.artist.setOnClickListener(v -> artistPickDialog());
        binding.toolbar.ok.setOnClickListener(v -> validateData());
    }

    private String name = DATA.EMPTY;

    private void validateData() {
        //get data
        name = binding.nameEt.getText().toString().trim();

        //validate data
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(context, "Enter Name...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedArtistId)) {
            Toast.makeText(context, "Enter Artist...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedCategoryId)) {
            Toast.makeText(context, "Enter Category...", Toast.LENGTH_SHORT).show();
        } else if (imageUri == null) {
            Toast.makeText(context, "Pick Image...", Toast.LENGTH_SHORT).show();
        } else {
            uploadToStorage();
        }
    }

    private void uploadToStorage() {
        dialog.setMessage("Uploading Album...");
        dialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.ALBUMS);
        String id = ref.push().getKey();

        String filePathAndName = "Images/Album/" + id;

        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName + DATA.DOT + VOID.getFileExtension(imageUri, context));
        reference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            String uploadedImageUrl = DATA.EMPTY + uriTask.getResult();

            uploadInfoToDB(uploadedImageUrl, id, ref);
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(context, "Category upload failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadInfoToDB(String uploadedImageUrl, String id, DatabaseReference ref) {
        dialog.setMessage("Uploading category info...");
        dialog.show();

        //setup data to upload
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(DATA.PUBLISHER, DATA.EMPTY + DATA.FirebaseUserUid);
        hashMap.put(DATA.TIMESTAMP, System.currentTimeMillis());
        hashMap.put(DATA.ID, id);
        hashMap.put(DATA.NAME, DATA.EMPTY + name);
        hashMap.put(DATA.CATEGORY_ID, DATA.EMPTY + selectedCategoryId);
        hashMap.put(DATA.ARTIST_ID, DATA.EMPTY + selectedArtistId);
        hashMap.put(DATA.IMAGE, uploadedImageUrl);
        hashMap.put(DATA.INTERESTED_COUNT, DATA.ZERO);
        hashMap.put(DATA.SONGS_COUNT, DATA.ZERO);

        //db reference: DB > Albums
        assert id != null;
        ref.child(id).setValue(hashMap).addOnSuccessListener(unused -> {
            if (selectedArtistId != null)
                VOID.incrementItemCount(DATA.ARTISTS, selectedArtistId, DATA.ALBUMS_COUNT);
            if (selectedCategoryId != null)
                VOID.incrementItemCount(DATA.CATEGORIES, selectedCategoryId, DATA.ALBUMS_COUNT);
            dialog.dismiss();
            Toast.makeText(context, "Successfully uploaded...", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(context, "Failure to upload to db due to :" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private String selectedCategoryId, selectedCategoryTitle;

    private void categoryPickDialog() {
        String[] categories = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            categories[i] = categoryList.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pick Category").setItems(categories, (dialog, which) -> {
            selectedCategoryTitle = categoryList.get(which);
            selectedCategoryId = categoryId.get(which);
            binding.category.setText(selectedCategoryTitle);
        }).show();
    }

    private String selectedArtistId, selectedArtistTitle;

    private void artistPickDialog() {
        String[] artists = new String[artistList.size()];
        for (int i = 0; i < artistList.size(); i++) {
            artists[i] = artistList.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pick Artist").setItems(artists, (dialog, which) -> {
            selectedArtistTitle = artistList.get(which);
            selectedArtistId = artistId.get(which);
            binding.artist.setText(selectedArtistTitle);
        }).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = CropImage.getPickImageResultUri(context, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(context, uri)) {
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