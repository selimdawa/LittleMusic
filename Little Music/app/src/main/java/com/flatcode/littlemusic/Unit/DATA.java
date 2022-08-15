package com.flatcode.littlemusic.Unit;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DATA {
    //Database
    public static String USERS = "Users";
    public static String TOOLS = "Tools";
    public static String EMAIL = "email";
    public static String CATEGORIES = "Categories";
    public static String ALBUMS = "Albums";
    public static String ARTISTS = "Artists";
    public static String ALBUM = "Album";
    public static String ARTIST = "Artist";
    public static String SONGS = "Songs";
    public static String INTERESTED = "Interested";
    public static String LOVES = "Loves";
    public static String VERSION = "version";
    public static String PRIVACY_POLICY = "privacyPolicy";
    public static String BASIC = "basic";
    public static String USER_NAME = "username";
    public static String PROFILE_IMAGE = "profileImage";
    public static String TIMESTAMP = "timestamp";
    public static String ID = "id";
    public static String IMAGE = "image";
    public static String SLIDER_SHOW = "SliderShow";
    public static String CATEGORY = "category";
    public static String NULL = "null";
    public static String FAVORITES = "Favorites";
    public static String VIEWS_COUNT = "viewsCount";
    public static String INTERESTED_COUNT = "interestedCount";
    public static String ALBUMS_COUNT = "albumsCount";
    public static String SONGS_COUNT = "songsCount";
    public static String LOVES_COUNT = "lovesCount";
    public static String EDITORS_CHOICE = "editorsChoice";
    public static String NAME = "name";
    public static String DOT = ".";
    public static int CURRENT_VERSION = 1;
    //Shared
    public static String PROFILE_ID = "profileId";
    public static String COLOR_OPTION = "color_option";
    public static String CATEGORY_ID = "categoryId";
    public static String ARTIST_ID = "artistId";
    public static String ARTIST_ABOUT = "artistAbout";
    public static String ALBUM_ID = "albumId";
    public static String SHOW_MORE_TYPE = "showMoreType";
    public static String CATEGORY_NAME = "categoryName";
    public static String ALBUM_NAME = "albumName";
    public static String ALBUM_IMAGE = "albumImage";
    public static String ARTIST_NAME = "artistName";
    public static String ARTIST_IMAGE = "artistImage";
    public static String SHOW_MORE_NAME = "showMoreName";
    public static String SHOW_MORE_BOOLEAN = "showMoreBoolean";
    //Other
    public static String EMPTY = "";
    public static String SPACE = " ";
    public static int MIX_SQUARE = 500;
    public static int ZERO = 0;
    public static int ORDER_MAIN = 5; // Here Max Item Show
    public static Boolean searchStatus = false;
    public static String FB_ID = "";
    public static String WEB_SITE = "";
    public static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    public static final FirebaseUser FIREBASE_USER = AUTH.getCurrentUser();
    public static final String FirebaseUserUid = FIREBASE_USER.getUid();
}