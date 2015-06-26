package com.windsoft.means.model;

import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by dongkyu on 2015-06-20.
 */
public class SongModel {

    private int cond;

    private String name;

    private ArrayList<String> userName;

    private ArrayList<Intent> score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getUserName() {
        return userName;
    }

    public void setUserName(ArrayList<String> userName) {
        this.userName = userName;
    }

    public ArrayList<Intent> getScore() {
        return score;
    }

    public void setScore(ArrayList<Intent> score) {
        this.score = score;
    }

    public int getCond() {
        return cond;
    }

    public void setCond(int cond) {
        this.cond = cond;
    }
}
