package com.windsoft.means.model;

/**
 * Created by dongkyu on 2015-06-14.
 */
public class MusicModel {
    private String name;
    private String artist;
    private int like;

    public MusicModel() {
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public MusicModel(int like) {
        this.like = like;
    }

    public MusicModel(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }
}
