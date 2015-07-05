package com.windsoft.means;

import android.app.Activity;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dongkyu on 2015-06-17.
 */
public class MP3Explorer {

    private static final String TAG = "MP3Explorer";

    private String path = "/sdcard/";

    private static ArrayList<String> songList = new ArrayList<>();
    private static ArrayList<String> pathList = new ArrayList<>();

    private Activity activity;

    public MP3Explorer(Activity activity) {
        this.activity = activity;
        init();
    }


    public ArrayList<String> getMP3List() {
        return songList;
    }


    public ArrayList<String> getPathList() {
        return pathList;
    }


    private void init() {
        Log.v(TAG, "init()");

        //sd카드 여부
        String sdcardState = Environment.getExternalStorageState();

        // sd카드 있을 때
        if (sdcardState.contentEquals(Environment.MEDIA_MOUNTED)) {
            // dirName = sd카드 기본 경로
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        // sd카드 내부의 파일 및 폴더
        File dir = new File(path);

        // sd카드 내부의 파일 및 폴더 배열화
        File dirList[] = dir.listFiles();

        // sd카드 내부의 mp3 파일 경로 저장
        String [] fileList = dir.list(new MP3Filter());

        // sd카드 내부의 mp3 파일 수 만큼 반복
        for (int i = 0; i < fileList.length; i++) {
            // 경로 저장
            songList.add(fileList[i]);
            pathList.add(path + "/" + fileList[i]);
        }

        // sd카드 내부의 파일 수 만큼 반복
        for (int i = 0; i < dirList.length; i++) {
            // 폴더 내부 검색
            // try - 파일의 경우 내부 검색 불가능 (예외처리)
            try {
                // sd 카드 내부의 폴더의 파일 중 mp3를 검색
                for (String file : dirList[i].list(new MP3Filter())) {
                    // 경로 저장
                    songList.add(file);
                    pathList.add(dirList[i] + "/" + file);
                }
            } catch (Exception e) {

            }
        }
    }


    private class MP3Filter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String filename) {
            return (filename.endsWith(".mp3"));
        }
    }


    public ArrayList<String> getMP3DataList() {

        ArrayList<MP3Model> models = new ArrayList<>();

        Cursor c = activity.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION},
                null,
                null,
                null);

        if (c == null) {
            Log.d(TAG, "커서 null");
            return null;
        }

        if (c.moveToFirst()) {
            int titleIndex = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistIndex = c.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int dataIndex = c.getColumnIndex(MediaStore.Audio.Media.DATA);
            int countIndex = c.getColumnIndex(MediaStore.Audio.Media.DURATION);
            do {
                String title = c.getString(titleIndex);
                String artist = c.getString(artistIndex);
                String data = c.getString(dataIndex);
                int count = c.getInt(countIndex);
                if (data.indexOf(".mp3") != -1) {
                    /**
                     * 재생시간 긴 순으로 입력
                     * */
                    if (models.size() == 0) {
                        models.add(new MP3Model(title, artist, count));
                    } else {
                        for (int i = 0; i < models.size(); i++) {
                            if (models.get(i).getPlayCount() < count) {                 // 재생시간이 더 길다면
                                models.add(i, new MP3Model(title, artist, count));
                                break;
                            }
                        }
                    }
                }
            } while (c.moveToNext());
        }

        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < models.size(); i++) {
            list.add(models.get(i).getTitle());
        }

        c.close();
        return list;
    }


    public static class MP3Model implements Serializable {
        private String title;
        private String artist;
        private int playCount;

        public MP3Model(String title) {
            this.title = title;
        }

        public MP3Model(String title, String artist, int playCount) {
            this.title = title;
            this.artist = artist;
            this.playCount = playCount;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public int getPlayCount() {
            return playCount;
        }

        public void setPlayCount(int playCount) {
            this.playCount = playCount;
        }
    }
}
