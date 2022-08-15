package com.flatcode.littlemusicadmin.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.flatcode.littlemusicadmin.Adapter.MainAdapter;
import com.flatcode.littlemusicadmin.Model.Main;
import com.flatcode.littlemusicadmin.Model.Song;
import com.flatcode.littlemusicadmin.Model.User;
import com.flatcode.littlemusicadmin.R;
import com.flatcode.littlemusicadmin.Unit.CLASS;
import com.flatcode.littlemusicadmin.Unit.DATA;
import com.flatcode.littlemusicadmin.Unit.THEME;
import com.flatcode.littlemusicadmin.Unit.VOID;
import com.flatcode.littlemusicadmin.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ActivityMainBinding binding;

    List<Main> list;
    MainAdapter adapter;

    Context context = MainActivity.this;

    private static final int SETTINGS_CODE = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                .registerOnSharedPreferenceChangeListener(this);
        THEME.setThemeOfApp(context);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Color Mode ----------------------------- Start
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        // Color Mode -------------------------------- End

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        if (sharedPreferences.getString(DATA.COLOR_OPTION, "ONE").equals("ONE")) {
            binding.toolbar.mode.setBackgroundResource(R.drawable.sun);
        } else if (sharedPreferences.getString(DATA.COLOR_OPTION, "NIGHT_ONE").equals("NIGHT_ONE")) {
            binding.toolbar.mode.setBackgroundResource(R.drawable.moon);
        }

        binding.toolbar.image.setOnClickListener(v ->
                VOID.IntentExtra(context, CLASS.PROFILE, DATA.PROFILE_ID, DATA.FirebaseUserUid));

        //binding.recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new MainAdapter(context, list);
        binding.recyclerView.setAdapter(adapter);
    }

    int U = 0, SO = 0, EC = 0, CA = 0, SL = 0, AL = 0, AR = 0, FA = 0;

    private void nrItems() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.USERS);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                U = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User item = data.getValue(User.class);
                    assert item != null;
                    if (item.getId() != null && !item.getId().equals(DATA.FirebaseUserUid))
                        U++;
                }
                nrSongs();
            }

            private void nrSongs() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        SO = 0;
                        EC = 0;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Song item = data.getValue(Song.class);
                            assert item != null;
                            if (item.getId() != null) {
                                SO++;
                                if (item.getEditorsChoice() != 0)
                                    if (item.getPublisher().equals(DATA.FirebaseUserUid))
                                        EC++;
                            }
                        }
                        nrCategories();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            private void nrCategories() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        CA = 0;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Song item = data.getValue(Song.class);
                            assert item != null;
                            if (item.getId() != null)
                                if (item.getPublisher().equals(DATA.FirebaseUserUid))
                                    CA++;
                        }
                        nrSliderShow();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            private void nrSliderShow() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.SLIDER_SHOW);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        SL = 0;
                        SL = (int) dataSnapshot.getChildrenCount();
                        nrAlbums();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            private void nrAlbums() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.ALBUMS);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        AL = 0;
                        AL = (int) dataSnapshot.getChildrenCount();
                        nrArtists();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            private void nrArtists() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.ARTISTS);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        AR = 0;
                        AR = (int) dataSnapshot.getChildrenCount();
                        nrFavorites();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            private void nrFavorites() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
                        .child(DATA.FirebaseUserUid);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FA = 0;
                        FA = (int) dataSnapshot.getChildrenCount();
                        IdeaPosts(U, SO, EC, CA, SL, AL, AR, FA);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.USERS)
                .child(DATA.FirebaseUserUid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                VOID.Glide(true, context, user.getProfileImage(), binding.toolbar.image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void IdeaPosts(int users, int songs, int editorsChoice, int categories, int sliderShow,
                           int albums, int artists, int favorites) {
        list.clear();
        Main item1 = new Main(R.drawable.ic_person, "Users", users, CLASS.USERS);
        Main item2 = new Main(R.drawable.ic_add, "Add Song", 0, CLASS.SONG_ADD);
        Main item3 = new Main(R.drawable.ic_music, "Songs", songs, CLASS.SONGS);
        Main item4 = new Main(R.drawable.ic_users, "Editors Choice", editorsChoice, CLASS.EDITORS_CHOICE);
        Main item5 = new Main(R.drawable.ic_add_category, "Add Category", 0, CLASS.CATEGORY_ADD);
        Main item6 = new Main(R.drawable.ic_category_gray, "Categories", categories, CLASS.CATEGORIES);
        Main item7 = new Main(R.drawable.ic_slider, "Slider Show", sliderShow, CLASS.SLIDER_SHOW);
        Main item8 = new Main(R.drawable.ic_adds, "Add Album", 0, CLASS.ALBUM_ADD);
        Main item9 = new Main(R.drawable.ic_album, "Albums", albums, CLASS.ALBUMS);
        Main item10 = new Main(R.drawable.ic__add, "Add Artist", 0, CLASS.ARTIST_ADD);
        Main item11 = new Main(R.drawable.ic_mic, "Artists", artists, CLASS.ARTISTS);
        Main item12 = new Main(R.drawable.ic_star_selected, "Favorites", favorites, CLASS.FAVORITES);
        Main item13 = new Main(R.drawable.ic_privacy_policy, "Privacy Policy", 0, CLASS.PRIVACY_POLICY);
        list.add(item1);
        list.add(item2);
        list.add(item3);
        list.add(item4);
        list.add(item5);
        list.add(item6);
        list.add(item7);
        list.add(item8);
        list.add(item9);
        list.add(item10);
        list.add(item11);
        list.add(item12);
        list.add(item13);
        adapter.notifyDataSetChanged();
        binding.bar.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
    }

    // Color Mode ----------------------------- Start
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("color_option")) {
            this.recreate();
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_CODE) {
            this.recreate();
        }
    }

    // Color Mode -------------------------------- End

    @Override
    protected void onResume() {
        userInfo();
        nrItems();
        super.onResume();
    }
}