package com.flatcode.littlemusic.Model;

import com.flatcode.littlemusic.Unit.DATA;

public class Song {

    public String id, publisher, categoryId, name, artistId, albumId, duration, songLink, key;
    int viewsCount, lovesCount, editorsChoice;
    long timestamp;

    public Song(String id, String publisher, long timestamp, String categoryId, String name, String albumId,
                String artistId, String duration, String songLink, int viewsCount, int lovesCount, int editorsChoice) {

        if (name.trim().equals(DATA.EMPTY)) {
            name = "No Name";
        }

        this.id = id;
        this.publisher = publisher;
        this.timestamp = timestamp;
        this.categoryId = categoryId;
        this.name = name;
        this.artistId = artistId;
        this.duration = duration;
        this.albumId = albumId;
        this.songLink = songLink;
        this.viewsCount = viewsCount;
        this.lovesCount = lovesCount;
        this.editorsChoice = editorsChoice;
    }

    public Song() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public int getLovesCount() {
        return lovesCount;
    }

    public void setLovesCount(int lovesCount) {
        this.lovesCount = lovesCount;
    }

    public int getEditorsChoice() {
        return editorsChoice;
    }

    public void setEditorsChoice(int editorsChoice) {
        this.editorsChoice = editorsChoice;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
