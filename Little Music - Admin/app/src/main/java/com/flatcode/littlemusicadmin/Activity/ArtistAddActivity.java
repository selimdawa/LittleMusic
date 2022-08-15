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

import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemusicadmin.R;
import com.flatcode.littlemusicadmin.Unit.DATA;
import com.flatcode.littlemusicadmin.Unit.THEME;
import com.flatcode.littlemusicadmin.Unit.VOID;
import com.flatcode.littlemusicadmin.databinding.ActivityArtistAddBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class ArtistAddActivity extends AppCompatActivity {

    private ActivityArtistAddBinding binding;
    Activity activity = ArtistAddActivity.this;

    private Uri imageUri = null;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivityArtistAddBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        dialog = new ProgressDialog(activity);
        dialog.setTitle("Please wait...");
        dialog.setCanceledOnTouchOutside(false);

        binding.toolbar.nameSpace.setText(R.string.add_new_artist);
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.image.setOnClickListener(v -> VOID.CropImageSquare(activity));
        binding.toolbar.ok.setOnClickListener(v -> validateData());
    }

    private String name = DATA.EMPTY, aboutTheArtist = DATA.EMPTY;

    private void validateData() {
        //get data
        name = binding.nameEt.getText().toString().trim();
        aboutTheArtist = binding.aboutTheArtistEt.getText().toString().trim();

        //validate data
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(activity, "Enter Name...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(aboutTheArtist)) {
            Toast.makeText(activity, "Enter About The Artist...", Toast.LENGTH_SHORT).show();
        } else if (imageUri == null) {
            Toast.makeText(activity, "Pick Image...", Toast.LENGTH_SHORT).show();
        } else {
            uploadToStorage();
        }
    }

    private void uploadToStorage() {
        dialog.setMessage("Uploading Artist...");
        dialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.ARTISTS);
        String id = ref.push().getKey();

        String filePathAndName = "Images/Artists/" + id;

        StorageReference reference = FirebaseStorage.getInstance()
                .getReference(filePathAndName + DATA.DOT + VOID.getFileExtension(imageUri, activity));
        reference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            String uploadedImageUrl = DATA.EMPTY + uriTask.getResult();

            uploadInfoDB(uploadedImageUrl, id, ref);
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Artist upload failed due to : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadInfoDB(String uploadedImageUrl, String id, DatabaseReference ref) {
        dialog.setMessage("Uploading Artist info...");
        dialog.show();

        //setup data to upload
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(DATA.PUBLISHER, DATA.EMPTY + DATA.FirebaseUserUid);
        hashMap.put(DATA.TIMESTAMP, System.currentTimeMillis());
        hashMap.put(DATA.ID, id);
        hashMap.put(DATA.NAME, DATA.EMPTY + name);
        hashMap.put(DATA.ABOUT_THE_ARTIST, DATA.EMPTY + aboutTheArtist);
        hashMap.put(DATA.IMAGE, uploadedImageUrl);
        hashMap.put(DATA.INTERESTED_COUNT, DATA.ZERO);
        hashMap.put(DATA.SONGS_COUNT, DATA.ZERO);
        hashMap.put(DATA.ALBUMS_COUNT, DATA.ZERO);

        //db reference: DB > Artists
        assert id != null;
        ref.child(id).setValue(hashMap).addOnSuccessListener(unused -> {

            dialog.dismiss();
            Toast.makeText(activity, "Successfully uploaded...", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Failure to upload to db due to : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
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