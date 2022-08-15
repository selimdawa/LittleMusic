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
import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemusicadmin.Model.Category;
import com.flatcode.littlemusicadmin.R;
import com.flatcode.littlemusicadmin.Unit.DATA;
import com.flatcode.littlemusicadmin.Unit.THEME;
import com.flatcode.littlemusicadmin.Unit.VOID;
import com.flatcode.littlemusicadmin.databinding.ActivityCategoryAddBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class CategoryEditActivity extends AppCompatActivity {

    private ActivityCategoryAddBinding binding;
    Activity activity = CategoryEditActivity.this;

    String categoryId;

    private Uri imageUri;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryAddBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        categoryId = getIntent().getStringExtra(DATA.CATEGORY_ID);

        dialog = new ProgressDialog(activity);
        dialog.setTitle("Please wait...");
        dialog.setCanceledOnTouchOutside(false);

        loadCategoryInfo();

        binding.toolbar.nameSpace.setText(R.string.edit_category);
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.image.setOnClickListener(v -> VOID.CropImageSquare(activity));
        binding.toolbar.ok.setOnClickListener(v -> validateData());
    }

    private String name = DATA.EMPTY;

    private void validateData() {
        name = binding.nameEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(activity, "Enter name...", Toast.LENGTH_SHORT).show();
        } else {
            if (imageUri == null) {
                updateCategory(DATA.EMPTY);
            } else {
                uploadImage();
            }
        }
    }

    private void uploadImage() {
        dialog.setMessage("Updating Category...");
        dialog.show();

        String filePathAndName = "Images/Category/" + categoryId;

        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName
                + DATA.DOT + VOID.getFileExtension(imageUri, activity));
        reference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            String uploadedImageUrl = DATA.EMPTY + uriTask.getResult();

            updateCategory(uploadedImageUrl);
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Failed to upload image due to : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateCategory(String imageUrl) {
        dialog.setMessage("Updating category image...");
        dialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(DATA.NAME, DATA.EMPTY + name);
        if (imageUri != null) {
            hashMap.put(DATA.IMAGE, DATA.EMPTY + imageUrl);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES);
        reference.child(categoryId).updateChildren(hashMap).addOnSuccessListener(unused -> {
            dialog.dismiss();
            Toast.makeText(activity, "Category updated...", Toast.LENGTH_SHORT).show();
            //finish();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Failed to update db duo to : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadCategoryInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES);
        reference.child(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Category item = snapshot.getValue(Category.class);
                assert item != null;
                String name = item.getName();
                String image = item.getImage();

                VOID.Glide(true, activity, image, binding.image);
                binding.nameEt.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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