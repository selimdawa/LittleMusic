package com.flatcode.littlemusicadmin.Unit;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.flatcode.littlemusicadmin.Model.Album;
import com.flatcode.littlemusicadmin.Model.Artist;
import com.flatcode.littlemusicadmin.Model.Category;
import com.flatcode.littlemusicadmin.Model.Song;
import com.flatcode.littlemusicadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Objects;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class VOID {

    public static void IntentClear(Context context, Class c) {
        Intent intent = new Intent(context, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void Intent(Context context, Class c) {
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
    }

    public static void IntentExtra(Context context, Class c, String key, String value) {
        Intent intent = new Intent(context, c);
        intent.putExtra(key, value);
        context.startActivity(intent);
    }

    public static void IntentExtra2(Context context, Class c, String key, String value, String key2, String value2) {
        Intent intent = new Intent(context, c);
        intent.putExtra(key, value);
        intent.putExtra(key2, value2);
        context.startActivity(intent);
    }

    public static void IntentExtra3(Context context, Class c, String key, String value
            , String key2, String value2, String key3, String value3) {
        Intent intent = new Intent(context, c);
        intent.putExtra(key, value);
        intent.putExtra(key2, value2);
        intent.putExtra(key3, value3);
        context.startActivity(intent);
    }

    public static void IntentExtra4(Context context, Class c, String key, String value
            , String key2, String value2, String key3, String value3, String key4, String value4) {
        Intent intent = new Intent(context, c);
        intent.putExtra(key, value);
        intent.putExtra(key2, value2);
        intent.putExtra(key3, value3);
        intent.putExtra(key4, value4);
        context.startActivity(intent);
    }

    public static void Glide(Boolean isUser, Context context, String Url, ImageView Image) {
        try {
            if (Url.equals(DATA.BASIC)) {
                if (isUser) {
                    Image.setImageResource(R.drawable.basic_user);
                } else {
                    Image.setImageResource(R.drawable.basic_music);
                }
            } else {
                Glide.with(context).load(Url).placeholder(R.color.image_profile).into(Image);
            }
        } catch (Exception e) {
            Image.setImageResource(R.drawable.basic_music);
        }
    }

    public static void GlideBlur(Boolean isUser, Context context, String Url, ImageView Image, int level) {
        try {
            if (Url.equals(DATA.BASIC)) {
                if (isUser) {
                    Image.setImageResource(R.drawable.basic_user);
                } else {
                    Image.setImageResource(R.drawable.basic_music);
                }
            } else {
                Glide.with(context).load(Url).placeholder(R.color.image_profile)
                        .apply(bitmapTransform(new BlurTransformation(level))).into(Image);
            }
        } catch (Exception e) {
            Image.setImageResource(R.drawable.basic_music);
        }
    }

    public static void incrementViewCount(String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get views count
                String viewsCount = DATA.EMPTY + snapshot.child(DATA.VIEWS_COUNT).getValue();
                if (viewsCount.equals(DATA.EMPTY) || viewsCount.equals(DATA.NULL)) {
                    viewsCount = "0";
                }

                long newViewsCount = Long.parseLong(viewsCount) + 1;
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(DATA.VIEWS_COUNT, newViewsCount);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
                reference.child(id).updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void incrementLovesCount(String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get views count
                String lovesCount = DATA.EMPTY + snapshot.child(DATA.LOVES_COUNT).getValue();
                if (lovesCount.equals(DATA.EMPTY) || lovesCount.equals(DATA.NULL)) {
                    lovesCount = "0";
                }

                long newLovesCount = Long.parseLong(lovesCount) + 1;
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(DATA.LOVES_COUNT, newLovesCount);
                hashMap.put(DATA.LOVES_COUNT, newLovesCount);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
                reference.child(id).updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void incrementLovesRemoveCount(String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get views count
                String lovesCount = DATA.EMPTY + snapshot.child("lovesCount").getValue();
                if (lovesCount.equals(DATA.EMPTY) || lovesCount.equals(DATA.NULL)) {
                    lovesCount = "0";
                }

                long removeLovesCount = Long.parseLong(lovesCount) - 1;
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("lovesCount", removeLovesCount);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
                reference.child(id).updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void incrementItemCount(String database, String id, String childDB) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(database);
        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get views count
                String itemsCount = DATA.EMPTY + snapshot.child(childDB).getValue();
                if (itemsCount.equals(DATA.EMPTY) || itemsCount.equals(DATA.NULL)) {
                    itemsCount = DATA.EMPTY + DATA.ZERO;
                }

                long newItemsCount = Long.parseLong(itemsCount) + 1;
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(childDB, newItemsCount);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(database);
                reference.child(id).updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void incrementItemRemoveCount(String database, String id, String childDB) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(database);
        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get views count
                String lovesCount = DATA.EMPTY + snapshot.child(childDB).getValue();
                if (lovesCount.equals(DATA.EMPTY) || lovesCount.equals(DATA.NULL)) {
                    lovesCount = DATA.EMPTY + DATA.ZERO;
                }

                long removeLovesCount = Long.parseLong(lovesCount) - 1;
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(childDB, removeLovesCount);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(database);
                reference.child(id).updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public static void isFavorite(final ImageView add, final String Id, final String UserId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(DATA.FAVORITES).child(UserId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Id).exists()) {
                    add.setImageResource(R.drawable.ic_star_selected);
                    add.setTag("added");
                } else {
                    add.setImageResource(R.drawable.ic_star_unselected);
                    add.setTag("add");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void checkFavorite(ImageView image, String id) {
        if (image.getTag().equals("add"))
            FirebaseDatabase.getInstance().getReference(DATA.FAVORITES).child(DATA.FirebaseUserUid)
                    .child(id).setValue(true);
        else
            FirebaseDatabase.getInstance().getReference(DATA.FAVORITES).child(DATA.FirebaseUserUid)
                    .child(id).removeValue();
    }

    public static void checkLove(ImageView image, String id) {
        if (image.getTag().equals("love")) {
            FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id)
                    .child(DATA.FirebaseUserUid).setValue(true);
            VOID.incrementLovesCount(id);
        } else {
            FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id)
                    .child(DATA.FirebaseUserUid).removeValue();
            VOID.incrementLovesRemoveCount(id);
        }
    }

    public static void isLoves(ImageView image, String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(DATA.LOVES).child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(DATA.FirebaseUserUid).exists()) {
                    image.setImageResource(R.drawable.ic_heart_selected);
                    image.setTag("loved");
                } else {
                    image.setImageResource(R.drawable.ic_heart_unselected);
                    image.setTag("love");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void nrLoves(TextView number, String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(DATA.LOVES).child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                number.setText(MessageFormat.format(" {0} ", dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void CropImageSquare(Activity activity) {
        CropImage.activity()
                .setMinCropResultSize(DATA.MIX_SQUARE, DATA.MIX_SQUARE)
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(activity);
    }

    public static void CropImageSlider(Activity activity) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setMinCropResultSize(DATA.MIX_SLIDER_X, DATA.MIX_SLIDER_Y)
                .setAspectRatio(16, 9)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(activity);
    }

    public static void Intro(Context context, ImageView background, ImageView backWhite, ImageView backDark) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (sharedPreferences.getString("color_option", "ONE").equals("ONE")) {
            background.setImageResource(R.drawable.background_day);
            backWhite.setVisibility(View.VISIBLE);
            backDark.setVisibility(View.GONE);
        } else if (sharedPreferences.getString("color_option", "NIGHT_ONE").equals("NIGHT_ONE")) {
            background.setImageResource(R.drawable.background_night);
            backWhite.setVisibility(View.GONE);
            backDark.setVisibility(View.VISIBLE);
        }
    }

    public static void Logo(Context context, ImageView background) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (sharedPreferences.getString("color_option", "ONE").equals("ONE")) {
            background.setImageResource(R.drawable.logo);
        } else if (sharedPreferences.getString("color_option", "NIGHT_ONE").equals("NIGHT_ONE")) {
            background.setImageResource(R.drawable.logo_night);
        }
    }

    public static String getFileExtension(Uri uri, Context context) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public static void moreDeleteCategory(Activity activity, Category item, String DB, String idDB, String childDB
            , String DB2, String idDB2, String childDB2, String DB3, String idDB3, String childDB3) {
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();

        String[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose Options").setItems(options, (dialog, which) -> {
            if (which == 0) {
                VOID.IntentExtra(activity, CLASS.CATEGORY_EDIT, DATA.CATEGORY_ID, id);
            } else if (which == 1) {
                dialogOptionDelete(activity, id, name, DATA.CATEGORY, DATA.CATEGORIES, false, DB, idDB
                        , childDB, DB2, idDB2, childDB2, DB3, idDB3, childDB3);
            }
        }).show();
    }

    public static void moreDeleteAlbum(Activity activity, Album item, String DB, String idDB, String childDB
            , String DB2, String idDB2, String childDB2, String DB3, String idDB3, String childDB3) {
        String id = DATA.EMPTY + item.getId();
        String category = DATA.EMPTY + item.getCategoryId();
        String artist = DATA.EMPTY + item.getArtistId();
        String name = DATA.EMPTY + item.getName();

        String[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose Options").setItems(options, (dialog, which) -> {
            if (which == 0) {
                VOID.IntentExtra3(activity, CLASS.ALBUM_EDIT, DATA.ALBUM_ID, id, DATA.CATEGORY_ID
                        , category, DATA.ARTIST_ID, artist);
            } else if (which == 1) {
                dialogOptionDelete(activity, id, name, DATA.ALBUM, DATA.ALBUMS, false, DB, idDB
                        , childDB, DB2, idDB2, childDB2, DB3, idDB3, childDB3);
            }
        }).show();
    }

    public static void moreDeleteArtist(Activity activity, Artist item, String DB, String idDB, String childDB
            , String DB2, String idDB2, String childDB2, String DB3, String idDB3, String childDB3) {
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();

        String[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose Options").setItems(options, (dialog, which) -> {
            if (which == 0) {
                VOID.IntentExtra(activity, CLASS.ARTIST_EDIT, DATA.ARTIST_ID, id);
            } else if (which == 1) {
                dialogOptionDelete(activity, id, name, DATA.ARTIST, DATA.ARTISTS, false, DB, idDB
                        , childDB, DB2, idDB2, childDB2, DB3, idDB3, childDB3);
            }
        }).show();
    }

    public static void moreDeleteSong(Activity activity, Song item, String DB, String idDB, String childDB
            , String DB2, String idDB2, String childDB2, String DB3, String idDB3, String childDB3) {
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();
        String category = DATA.EMPTY + item.getCategoryId();
        String artist = DATA.EMPTY + item.getArtistId();
        String album = DATA.EMPTY + item.getAlbumId();

        String[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose Options").setItems(options, (dialog, which) -> {
            if (which == 0) {
                VOID.IntentExtra4(activity, CLASS.SONG_EDIT, DATA.SONG_ID, id, DATA.CATEGORY_ID, category
                        , DATA.ARTIST_ID, artist, DATA.ALBUM_ID, album);
            } else if (which == 1) {
                dialogOptionDelete(activity, id, name, DATA.SONG, DATA.SONGS, false, DB, idDB
                        , childDB, DB2, idDB2, childDB2, DB3, idDB3, childDB3);
            }
        }).show();
    }

    public static void dialogOptionDelete(Activity activity, String id, String name, String type
            , String nameDB, boolean isEditorsChoice, String DB, String idDB, String childDB
            , String DB2, String idDB2, String childDB2, String DB3, String idDB3, String childDB3) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextView title = dialog.findViewById(R.id.title);
        title.setText(MessageFormat.format("Do you want to delete {0} ( {1} ) ?", name, type));

        dialog.findViewById(R.id.yes).setOnClickListener(view -> {
            if (isEditorsChoice) {
                dialogUpdateEditorsChoice(dialog, activity, id);
            } else {
                deleteDB(dialog, activity, id, name, nameDB, DB, idDB, childDB
                        , DB2, idDB2, childDB2, DB3, idDB3, childDB3);
            }
        });

        dialog.findViewById(R.id.no).setOnClickListener(view2 -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public static void dialogUpdateEditorsChoice(Dialog dialogDelete, Context context, String id) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Updating Editors Choice...");
        dialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(DATA.EDITORS_CHOICE, 0);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
        reference.child(id).updateChildren(hashMap).addOnSuccessListener(unused -> {
            dialog.dismiss();
            Toast.makeText(context, "Editors Choice updated...", Toast.LENGTH_SHORT).show();
            dialogDelete.dismiss();
            //finish();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(context, "Failed to update db duo to " + e.getMessage(), Toast.LENGTH_SHORT).show();
            dialogDelete.dismiss();
        });
    }

    public static void deleteDB(Dialog dialogDelete, Activity activity, String id, String
            name, String nameDB, String DB, String idDB, String childDB
            , String DB2, String idDB2, String childDB2, String DB3, String idDB3, String childDB3) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setTitle("Please wait");
        dialog.setMessage("Deleting " + name + " ...");
        dialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(nameDB);
        reference.child(id).removeValue().addOnSuccessListener(unused1 -> {
            VOID.incrementItemRemoveCount(DB, idDB, childDB);
            VOID.incrementItemRemoveCount(DB2, idDB2, childDB2);
            VOID.incrementItemRemoveCount(DB3, idDB3, childDB3);
            DATA.isChange = true;
            activity.onBackPressed();
            dialog.dismiss();
            Toast.makeText(activity, name + " Deleted Successfully...", Toast.LENGTH_SHORT).show();
            dialogDelete.dismiss();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    public static void addToEditorsChoice(Context context, Activity activity, String id,
                                          int number) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Updating Editors Choice...");
        dialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(DATA.EDITORS_CHOICE, number);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS);
        reference.child(id).updateChildren(hashMap).addOnSuccessListener(unused -> {
            dialog.dismiss();
            Toast.makeText(context, "Editors Choice updated...", Toast.LENGTH_SHORT).show();
            activity.finish();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(context, "Failed to update db duo to " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public static void dataName(String database, String dataId, TextView name) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(database);
        reference.child(dataId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Name = DATA.EMPTY + snapshot.child(DATA.NAME).getValue();
                name.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, Name));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void dialogAboutArtist(Context context, String imageDB, String nameDB, String aboutDB) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_about_artist);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ImageView image = dialog.findViewById(R.id.image);
        TextView name = dialog.findViewById(R.id.name);
        TextView aboutTheArtist = dialog.findViewById(R.id.aboutTheArtist);

        VOID.Glide(true, context, imageDB, image);
        name.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, nameDB));
        aboutTheArtist.setText(MessageFormat.format("{0}{1}", DATA.EMPTY, aboutDB));

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public static String convertDuration(long duration) {
        long minutes = (duration / 1000) / 60;
        long seconds = (duration / 1000) % 60;
        String converted = String.format("%d:%02d", minutes, seconds);
        return converted;
    }
}