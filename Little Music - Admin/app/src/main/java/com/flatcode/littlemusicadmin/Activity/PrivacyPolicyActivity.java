package com.flatcode.littlemusicadmin.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemusicadmin.R;
import com.flatcode.littlemusicadmin.Unit.CLASS;
import com.flatcode.littlemusicadmin.Unit.DATA;
import com.flatcode.littlemusicadmin.Unit.THEME;
import com.flatcode.littlemusicadmin.Unit.VOID;
import com.flatcode.littlemusicadmin.databinding.ActivityPrivacyPolicyBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private ActivityPrivacyPolicyBinding binding;
    Activity activity = PrivacyPolicyActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyPolicyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.toolbar.nameSpace.setText(R.string.privacy_policy);
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.edit.setOnClickListener(v -> VOID.Intent(activity, CLASS.PRIVACY_POLICY_EDIT));

        VOID.Logo(activity, binding.logo);
        privacyPolicy();
    }

    private void privacyPolicy() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(DATA.TOOLS)
                .child(DATA.PRIVACY_POLICY);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = String.valueOf(dataSnapshot.getValue());

                binding.text.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onRestart() {
        privacyPolicy();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        privacyPolicy();
        super.onResume();
    }
}