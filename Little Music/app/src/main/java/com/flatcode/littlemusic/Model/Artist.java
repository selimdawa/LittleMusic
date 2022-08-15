package com.flatcode.littlemusic.Model;

public class Artist {

    String id, name, image, aboutTheArtist, publisher;
    int interestedCount, songsCount, albumsCount;
    long timestamp;

    public Artist() {

    }

    public Artist(String id, String name, String aboutTheArtist, String image, String publisher
            , long timestamp, int interestedCount, int songsCount, int albumsCount) {
        this.id = id;
        this.name = name;
        this.publisher = publisher;
        this.aboutTheArtist = aboutTheArtist;
        this.image = image;
        this.timestamp = timestamp;
        this.interestedCount = interestedCount;
        this.songsCount = songsCount;
        this.albumsCount = albumsCount;
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

    public String getAboutTheArtist() {
        return aboutTheArtist;
    }

    public void setAboutTheArtist(String aboutTheArtist) {
        this.aboutTheArtist = aboutTheArtist;
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

    public int getAlbumsCount() {
        return albumsCount;
    }

    public void setAlbumsCount(int albumsCount) {
        this.albumsCount = albumsCount;
    }
}
