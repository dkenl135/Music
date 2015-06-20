package com.windsoft.means;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by dongkyu on 2015-06-14.
 */
public class DBManager extends SQLiteOpenHelper {

    private final String Tag = "DBManager";

    public static final String TABLE_SONG = "songTable";

    public static final String COL_MUSIC = "musicName";
    public static final String COL_LIKE = "musicLike";

    public static final int MUSIC_INDEX = 0;
    public static final int LIKE_INDEX = 1;


    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String command = "CREATE TABLE IF NOT EXISTS " + TABLE_SONG + "("
                + COL_MUSIC + " TEXT NOT NULL UNIQUE, " +
                COL_LIKE + " INTEGER);";
        db.execSQL(command);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void insert(String songName) {
        SQLiteDatabase db = getWritableDatabase();

        String command = "INSERT INTO " + TABLE_SONG + " values('" + songName + "', " + "10);";
        db.execSQL(command);
    }


    public String find(String songName) {
        SQLiteDatabase db = getReadableDatabase();
        String name = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SONG + " WHERE " + COL_MUSIC + " = '" + songName + "';", null);
        if (cursor.moveToFirst()) {
            name = cursor.getString(MUSIC_INDEX);
        }

        return name;
    }


    public void update(String songName, int likeScore) {
        SQLiteDatabase db = getWritableDatabase();

        String command = "UPDATE " + TABLE_SONG + " set " + COL_LIKE + " where " + COL_MUSIC + " = '" + songName + "';";
        db.execSQL(command);
    }


    public ArrayList<MusicModel> findAll() {
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<MusicModel> list = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SONG + ";", null);
        while (cursor.moveToNext()) {
            MusicModel model = new MusicModel();

            model.setName(cursor.getString(MUSIC_INDEX));
            model.setLike(cursor.getInt(LIKE_INDEX));

            list.add(model);
        }

        return list;
    }
}
