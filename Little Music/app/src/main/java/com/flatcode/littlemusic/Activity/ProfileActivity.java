package com.flatcode.littlemusic.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemusic.R;
import com.flatcode.littlemusic.Unit.CLASS;
import com.flatcode.littlemusic.Unit.DATA;
import com.flatcode.littlemusic.Unit.THEME;
import com.flatcode.littlemusic.Unit.VOID;
import com.flatcode.littlemusic.databinding.ActivityProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    Context context = ProfileActivity.this;

    String profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(context);
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        profileId = intent.getStringExtra(DATA.PROFILE_ID);

        if (profileId.equals(DATA.FirebaseUserUid)) {
            binding.edit.setVisibility(View.VISIBLE);
            binding.edit.setImageResource(R.drawable.ic_edit_white);
            binding.edit.setOnClickListener(v -> VOID.Intent(context, CLASS.PROFILE_EDIT));
        }
        binding.back.setOnClickListener(v -> onBackPressed());
    }

    private void start() {
        loadUserInfo();
        getNrFavorites();
        nrInterested(DATA.ALBUMS, binding.numberAlbums);
        nrInterested(DATA.ARTISTS, binding.numberArtists);
        nrInterested(DATA.CATEGORIES, binding.numberCategories);
    }

    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.USERS);
        reference.child(profileId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //String email = DATA.EMPTY + snapshot.child(DATA.EMAIL).getValue();
                String username = DATA.EMPTY + snapshot.child(DATA.USER_NAME).getValue();
                String profileImage = DATA.EMPTY + snapshot.child(DATA.PROFILE_IMAGE).getValue();
                //String timestamp = DATA.EMPTY + snapshot.child(DATA.TIMESTAMP).getValue();
                //String id = DATA.EMPTY + snapshot.child(DATA.ID).getValue();
                //int version = DATA.ZERO + snapshot.child(DATA.VERSION).getValue();

                binding.username.setText(username);

                VOID.Glide(true, context, profileImage, binding.profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void nrInterested(String database, TextView text) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED).child(profileId).child(database);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                text.setText(MessageFormat.format("{0}", dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getNrFavorites() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.FAVORITES).child(profileId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.numberFavorites.setText(MessageFormat.format("{0}", dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onRestart() {
        start();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        start();
        super.onResume();
    }
}