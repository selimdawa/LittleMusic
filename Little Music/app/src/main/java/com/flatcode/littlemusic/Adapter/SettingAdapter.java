package com.flatcode.littlemusic.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flatcode.littlemusic.Model.Setting;
import com.flatcode.littlemusic.Unit.DATA;
import com.flatcode.littlemusic.Unit.VOID;
import com.flatcode.littlemusic.databinding.ItemSettingBinding;

import java.text.MessageFormat;
import java.util.ArrayList;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolder> {

    private ItemSettingBinding binding;
    private final Context context;
    private ArrayList<Setting> list;

    public SettingAdapter(Context context, ArrayList<Setting> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemSettingBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Setting item = list.get(position);
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();
        int image = item.getImage();
        int number = item.getNumber();
        Class to = item.getC();

        holder.name.setText(name);
        holder.image.setImageResource(image);

        if (number != 0) {
            holder.number.setVisibility(View.VISIBLE);
            holder.number.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, number));
        } else {
            holder.number.setVisibility(View.GONE);
        }

        holder.item.setOnClickListener(view -> {
            switch (id) {
                case "6":
                    VOID.dialogAboutApp(context);
                    break;
                case "7":
                    VOID.dialogLogout(context);
                    break;
                case "8":
                    VOID.shareApp(context);
                    break;
                case "9":
                    VOID.rateApp(context);
                    break;
                default:
                    VOID.Intent(context, to);
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, number;
        LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            image = binding.image;
            number = binding.number;
            name = binding.name;
            item = binding.item;
        }
    }
}