package com.flatcode.littlemusic.Model;

public class Album {

    String id, name, image, artistId, categoryId, publisher;
    int interestedCount, songsCount;
    long timestamp;

    public Album() {

    }

    public Album(String id, String name, String artistId, String categoryId, String image, String publisher
            , long timestamp, int interestedCount, int songsCount) {
        this.id = id;
        this.name = name;
        this.publisher = publisher;
        this.artistId = artistId;
        this.categoryId = categoryId;
        this.image = image;
        this.timestamp = timestamp;
        this.interestedCount = interestedCount;
        this.songsCount = songsCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getInterestedCount() {
        return interestedCount;
    }

    public void setInterestedCount(int interestedCount) {
        this.interestedCount = interestedCount;
    }

    public int getSongsCount() {
        return songsCount;
    }

    public void setSongsCount(int songsCount) {
        this.songsCount = songsCount;
    }
}