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

    public static final String TABLE_NEW = "NewMusic";
    public static final String TABLE_SONG_BALAD = "SongBalad";
    public static final String TABLE_SONG_DANCE = "songDance";
    public static final String TABLE_SONG_CLUB = "songClub";
    public static final String TABLE_SONG_ROCK = "songRock";
    public static final String TABLE_SONG_RNB = "songRnb";
    public static final String TABLE_SONG_HIPHOP = "songHiphop";
    public static final String TABLE_SONG_INDI = "songIndi";
    public static final String TABLE_SONG_TROT = "songTrot";

    public static final String TABLE_POP = "pop";
    public static final String TABLE_POP_ROCK = "popRock";
    public static final String TABLE_POP_RNB = "popRnb";
    public static final String TABLE_POP_CLUB = "popClub";
    public static final String TABLE_POP_HIPHOP = "popHiphop";

    public static final String TABLE_OST = "ost";
    public static final String TABLE_JPOP = "jpop";
    public static final String TABLE_CLASSIC = "classic";
    public static final String TABLE_JAZZ = "jazz";
    public static final String TABLE_CHILDE = "childe";

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
        for (int i = 0; i < Global.SONG_LIST.length; i++) {
            String command = "CREATE TABLE IF NOT EXIST" + Global.SONG_LIST[i] + "("
                    + COL_MUSIC + " TEXT NOT NULL UNIQUE, " +
                    COL_LIKE + " INTEGER);";

            db.execSQL(command);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String tableName, String songName) {
        SQLiteDatabase db = getWritableDatabase();

        String command = "INSERT INTO " + tableName + " values('" + songName + "', " + "10);";
        db.execSQL(command);
    }

    public void update(String tableName, String songName, int likeScore) {
        SQLiteDatabase db = getWritableDatabase();

        String command = "UPDATE " + tableName + " set " + COL_LIKE + " where " + COL_MUSIC + " = '" + songName + "';";
        db.execSQL(command);
    }

    public ArrayList<MusicModel> findAll() {
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<MusicModel> list = new ArrayList<>();

        for (int i = 0; i < Global.SONG_LIST.length; i++) {
            Cursor cursor = db.rawQuery("SELECT * FROM " + Global.SONG_LIST[i], null);

            while (cursor.moveToNext()) {
                MusicModel model = new MusicModel();

                model.setName(cursor.getString(MUSIC_INDEX));
                model.setLike(cursor.getInt(LIKE_INDEX));

                list.add(model);
            }
        }

        return list;
    }

    public ArrayList<MusicModel> findList(String tableName) {
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<MusicModel> list = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);

        while (cursor.moveToNext()) {
            MusicModel model = new MusicModel();

            model.setName(cursor.getString(MUSIC_INDEX));
            model.setLike(cursor.getInt(LIKE_INDEX));

            list.add(model);
        }

        return list;
    }
}
