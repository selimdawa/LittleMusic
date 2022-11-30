package com.flatcode.littlemusic.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jean.jcplayer.model.JcAudio;
import com.flatcode.littlemusic.Adapter.CategoryHomeAdapter;
import com.flatcode.littlemusic.Adapter.ImageSliderAdapter;
import com.flatcode.littlemusic.Adapter.SongMainAdapter;
import com.flatcode.littlemusic.Model.Category;
import com.flatcode.littlemusic.Model.Song;
import com.flatcode.littlemusic.Unit.CLASS;
import com.flatcode.littlemusic.Unit.DATA;
import com.flatcode.littlemusic.Unit.VOID;
import com.flatcode.littlemusic.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private ArrayList<Song> list, list2, list3, list4;
    private SongMainAdapter adapter, adapter2, adapter3, adapter4;
    private Boolean B_one = false, B_two = true, B_three = true, B_four = true;

    Boolean isPlaying = false;
    ArrayList<JcAudio> jcAudios = new ArrayList<>();
    private int currentSong;

    int TotalCounts;

    private ArrayList<Category> categoryList;
    private CategoryHomeAdapter categoryAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(LayoutInflater.from(getContext()), container, false);

        loadCategories();

        binding.showMore.setOnClickListener(v -> VOID.IntentExtra3(getContext(), CLASS.SHOW_MORE,
                DATA.SHOW_MORE_TYPE, DATA.EDITORS_CHOICE, DATA.SHOW_MORE_NAME,
                binding.name.getText().toString(), DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + B_one));
        binding.showMore2.setOnClickListener(v -> VOID.IntentExtra3(getContext(), CLASS.SHOW_MORE,
                DATA.SHOW_MORE_TYPE, DATA.VIEWS_COUNT, DATA.SHOW_MORE_NAME,
                binding.mostViews.getText().toString(), DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + B_two));
        binding.showMore3.setOnClickListener(v -> VOID.IntentExtra3(getContext(), CLASS.SHOW_MORE,
                DATA.SHOW_MORE_TYPE, DATA.LOVES_COUNT, DATA.SHOW_MORE_NAME,
                binding.name3.getText().toString(), DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + B_three));
        binding.showMore4.setOnClickListener(v -> VOID.IntentExtra3(getContext(), CLASS.SHOW_MORE,
                DATA.SHOW_MORE_TYPE, DATA.TIMESTAMP, DATA.SHOW_MORE_NAME,
                binding.name4.getText().toString(), DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + B_four));

        //RecyclerView Category
        //binding.recyclerCategory.setHasFixedSize(true);
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryHomeAdapter(getContext(), categoryList);
        binding.recyclerCategory.setAdapter(categoryAdapter);

        //RecyclerView Editor's Choice
        //binding.recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        binding.recyclerView.setAdapter(adapter);
        adapter = new SongMainAdapter(getContext(), list, (songs, position) -> {
            changeSelectedSong(position, adapter);
            binding.player.jcPlayer.playAudio(jcAudios.get(position));
            changeSelectedSong(-1, adapter2);
            changeSelectedSong(-1, adapter3);
            changeSelectedSong(-1, adapter4);
        }, ((songs, position) -> binding.player.jcPlayer.pause()));

        //RecyclerView Views Count
        //binding.recyclerView2.setHasFixedSize(true);
        list2 = new ArrayList<>();
        binding.recyclerView2.setAdapter(adapter2);
        adapter2 = new SongMainAdapter(getContext(), list2, (songs, position) -> {
            changeSelectedSong(position, adapter2);
            changeSelectedSong(-1, adapter);
            changeSelectedSong(-1, adapter3);
            changeSelectedSong(-1, adapter4);
            binding.player.jcPlayer.playAudio(jcAudios.get(position));
        }, ((songs, position) -> binding.player.jcPlayer.pause()));

        //RecyclerView Loves Count
        //binding.recyclerView3.setHasFixedSize(true);
        list3 = new ArrayList<>();
        binding.recyclerView3.setAdapter(adapter3);
        adapter3 = new SongMainAdapter(getContext(), list3, (songs, position) -> {
            changeSelectedSong(position, adapter3);
            changeSelectedSong(-1, adapter);
            changeSelectedSong(-1, adapter2);
            changeSelectedSong(-1, adapter4);
            binding.player.jcPlayer.playAudio(jcAudios.get(position));
        }, ((songs, position) -> binding.player.jcPlayer.pause()));

        //RecyclerView New Songs
        //binding.recyclerView4.setHasFixedSize(true);
        list4 = new ArrayList<>();
        binding.recyclerView4.setAdapter(adapter4);
        adapter4 = new SongMainAdapter(getContext(), list4, (songs, position) -> {
            changeSelectedSong(position, adapter4);
            changeSelectedSong(-1, adapter);
            changeSelectedSong(-1, adapter2);
            changeSelectedSong(-1, adapter3);
            binding.player.jcPlayer.playAudio(jcAudios.get(position));
        }, ((songs, position) -> binding.player.jcPlayer.pause()));

        FirebaseDatabase.getInstance().getReference(DATA.SLIDER_SHOW).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long counts = snapshot.getChildrenCount();
                TotalCounts = (int) counts;

                binding.imageSlider.setSliderAdapter(new ImageSliderAdapter(getContext(), TotalCounts));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }

    private void start() {
        loadPostEditorsChoice(DATA.EDITORS_CHOICE, list, adapter, binding.bar, binding.recyclerView, binding.empty);
        loadPostBy(DATA.VIEWS_COUNT, list2, adapter2, binding.bar2, binding.recyclerView2, binding.empty2);
        loadPostBy(DATA.LOVES_COUNT, list3, adapter3, binding.bar3, binding.recyclerView3, binding.empty3);
        loadPostBy(DATA.TIMESTAMP, list4, adapter4, binding.bar4, binding.recyclerView4, binding.empty4);
    }

    private void loadCategories() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Category category = data.getValue(Category.class);
                    categoryList.add(category);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPostBy(String orderBy, ArrayList<Song> list, SongMainAdapter adapter,
                            ProgressBar bar, RecyclerView recyclerView, TextView empty) {
        changeSelectedSong(-1, adapter);
        Query ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
        ref.orderByChild(orderBy).limitToLast(DATA.ORDER_MAIN).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Song item = data.getValue(Song.class);
                    assert item != null;
                    if (!orderBy.equals(DATA.EDITORS_CHOICE)) {
                        list.add(item);
                        item.setKey(snapshot.getKey());
                        currentSong = -1;
                        isPlaying = true;
                        jcAudios.add(JcAudio.createFromURL(item.getName(), item.getSongLink()));
                        recyclerView.setAdapter(adapter);
                    }
                }
                adapter.notifyDataSetChanged();
                bar.setVisibility(View.GONE);
                if (!list.isEmpty()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    if (!orderBy.equals(DATA.EDITORS_CHOICE))
                        Collections.reverse(list);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                }

                if (isPlaying) {
                    binding.player.jcPlayer.initPlaylist(jcAudios, null);
                } else {
                    Toast.makeText(getContext(), "There is no songs!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPostEditorsChoice(String orderBy, ArrayList<Song> list, SongMainAdapter adapter,
                                       ProgressBar bar, RecyclerView recyclerView, TextView empty) {
        changeSelectedSong(-1, adapter);
        Query ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
        ref.orderByChild(orderBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Song item = data.getValue(Song.class);
                    assert item != null;
                    if (orderBy.equals(DATA.EDITORS_CHOICE)) {
                        if (item.getEditorsChoice() <= 5 && item.getEditorsChoice() > 0) {
                            list.add(item);
                            item.setKey(snapshot.getKey());
                            currentSong = -1;
                            isPlaying = true;
                            jcAudios.add(JcAudio.createFromURL(item.getName(), item.getSongLink()));
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                bar.setVisibility(View.GONE);
                bar.setVisibility(View.GONE);
                if (!list.isEmpty()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    if (!orderBy.equals(DATA.EDITORS_CHOICE))
                        Collections.reverse(list);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                }

                if (isPlaying) {
                    binding.player.jcPlayer.initPlaylist(jcAudios, null);
                } else {
                    Toast.makeText(getContext(), "There is no songs!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeSelectedSong(int index, SongMainAdapter adapter) {
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
        start();
        super.onResume();
    }
}