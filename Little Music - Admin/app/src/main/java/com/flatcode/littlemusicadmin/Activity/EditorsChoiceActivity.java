package com.flatcode.littlemusicadmin.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemusicadmin.Adapter.EditorsChoiceAdapter;
import com.flatcode.littlemusicadmin.Model.EditorsChoice;
import com.flatcode.littlemusicadmin.R;
import com.flatcode.littlemusicadmin.Unit.THEME;
import com.flatcode.littlemusicadmin.databinding.ActivityEditorsChoiceBinding;

import java.util.ArrayList;

public class EditorsChoiceActivity extends AppCompatActivity {

    private ActivityEditorsChoiceBinding binding;
    Activity activity = EditorsChoiceActivity.this;

    ArrayList<EditorsChoice> list;
    EditorsChoiceAdapter adapter;
    EditorsChoice editorsChoice = new EditorsChoice();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivityEditorsChoiceBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.toolbar.nameSpace.setText(R.string.editors_choice);
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        //binding.recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new EditorsChoiceAdapter(activity, list);
        binding.recyclerView.setAdapter(adapter);

        getData();
    }

    public void getData() {
        list.clear();
        for (int i = 0; i < 50; i++) {
            list.add(editorsChoice);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onRestart() {
        getData();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        getData();
        super.onResume();
    }
}