package com.windsoft.means.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.windsoft.means.Global;
import com.windsoft.means.MusicModel;
import com.windsoft.means.MusicService;
import com.windsoft.means.R;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private final String TAG = "MainActivity";

    private ArrayList<MusicModel> musicList = new ArrayList<>();    // 모든 음악 데이터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        final String id = intent.getStringExtra(Global.ID_KEY);
        final String[] nameList = intent.getStringArrayExtra(Global.SONG_NAME_KEY);
        final int[] scoreList = intent.getIntArrayExtra(Global.SONG_SCORE_KEY);

        intent = new Intent(this, MusicService.class);
        intent.putExtra(Global.COMMAND_KEY, Global.CHECK_DB);
        startService(intent);
    }


    @Override
    protected void onNewIntent(Intent intent) {

        if (intent != null) {
            String command = intent.getStringExtra(Global.COMMAND_KEY);

            if (command != null) {
                if (command.equals(Global.RES_DB)) {    // DB 새로고침 응답
                    commandResDB();
                }
            }
        }

        super.onNewIntent(intent);
    }


    private void commandResDB() {
        Log.d(TAG, "commandResDB()");
        musicList = Global.manager.findAll();

        for (int i = 0; i < musicList.size(); i++) {
            Log.e(TAG, "name = " + musicList.get(i).getName());
        }

//        Intent intent = new Intent(MainActivity.this, MusicService.class);
//        intent.putExtra(Global.COMMAND_KEY, Global.GET_PHOTO_KEY);
//        intent.putExtra(Global.PHOTO_NAME_KEY, );
//        startService(intent);
    }
}
