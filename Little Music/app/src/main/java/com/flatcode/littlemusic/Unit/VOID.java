package com.flatcode.littlemusic.Unit;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
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

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.flatcode.littlemusic.BuildConfig;
import com.flatcode.littlemusic.R;
import com.google.firebase.auth.FirebaseAuth;
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

    public static void closeApp(Context context, Activity a) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_close_app);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.findViewById(R.id.yes).setOnClickListener(v -> a.finish());
        dialog.findViewById(R.id.no).setOnClickListener(v -> dialog.cancel());

        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setAttributes(lp);
    }

    public static void dialogLogout(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.findViewById(R.id.yes).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            IntentClear(context, CLASS.AUTH);
        });

        dialog.findViewById(R.id.no).setOnClickListener(v -> dialog.cancel());

        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setAttributes(lp);
    }

    public static void shareApp(Context context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app");
        shareIntent.putExtra(Intent.EXTRA_TEXT, " Download the app now from Google Play " + " https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        context.startActivity(Intent.createChooser(shareIntent, "Choose how to share"));
    }

    public static void rateApp(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static void dialogAboutApp(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_about_app);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.findViewById(R.id.website).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(getWebsiteIntent());
            }

            public Intent getWebsiteIntent() {
                return new Intent(Intent.ACTION_VIEW, Uri.parse(DATA.WEB_SITE));
            }
        });

        dialog.findViewById(R.id.facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(getOpenFacebookIntent());
            }

            public Intent getOpenFacebookIntent() {
                try {
                    context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + DATA.FB_ID));
                } catch (Exception e) {
                    return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + DATA.FB_ID));
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
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

        VOID.Glide(false, context, imageDB, image);
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

    public static void checkFavorite(ImageView image, String id) {
        if (image.getTag().equals("add")) {
            FirebaseDatabase.getInstance().getReference().child(DATA.FAVORITES).child(DATA.FirebaseUserUid)
                    .child(id).setValue(true);
        } else {
            FirebaseDatabase.getInstance().getReference().child(DATA.FAVORITES).child(DATA.FirebaseUserUid)
                    .child(id).removeValue();
        }
    }

    public static void checkInterested(ImageView image, String type, String id) {
        if (image.getTag().equals("add")) {
            incrementInterestedCount(id, type);
            FirebaseDatabase.getInstance().getReference().child(DATA.INTERESTED).child(DATA.FirebaseUserUid)
                    .child(type).child(id).setValue(true);
        } else {
            incrementInterestedRemoveCount(id, type);
            FirebaseDatabase.getInstance().getReference().child(DATA.INTERESTED).child(DATA.FirebaseUserUid)
                    .child(type).child(id).removeValue();
        }
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

    public static void isInterested(final ImageView add, final String Id, String type) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
                .child(DATA.FirebaseUserUid).child(type);
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

    public static void incrementInterestedCount(String id, String type) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(type);
        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get views count
                String interestedCount = DATA.EMPTY + snapshot.child(DATA.INTERESTED_COUNT).getValue();
                if (interestedCount.equals(DATA.EMPTY) || interestedCount.equals(DATA.NULL)) {
                    interestedCount = "0";
                }

                long newInterestedCount = Long.parseLong(interestedCount) + 1;
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(DATA.INTERESTED_COUNT, newInterestedCount);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(type);
                reference.child(id).updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void incrementInterestedRemoveCount(String id, String type) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(type);
        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get views count
                String interestedCount = DATA.EMPTY + snapshot.child(DATA.INTERESTED_COUNT).getValue();
                if (interestedCount.equals(DATA.EMPTY) || interestedCount.equals(DATA.NULL)) {
                    interestedCount = "0";
                }

                long removeInterestedCount = Long.parseLong(interestedCount) - 1;
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(DATA.INTERESTED_COUNT, removeInterestedCount);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(type);
                reference.child(id).updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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
}