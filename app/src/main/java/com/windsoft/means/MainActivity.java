package com.windsoft.means;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private final String TAG = "MainActivity";

    private ArrayList<MusicModel> musicList = new ArrayList<>();    // 모든 음악 데이터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(Global.COMMAND_KEY, Global.CHECK_DB);
        startService(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (intent != null) {
            String command = intent.getStringExtra(Global.COMMAND_KEY);

            if (command != null) {
                processCommand(command, intent);
            }
        }

        super.onNewIntent(intent);
    }

    private void processCommand(String command, Intent intent) {
        if (command.equals(Global.RES_DB)) {    // DB 새로고침 응답
            musicList = Global.manager.findAll();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
