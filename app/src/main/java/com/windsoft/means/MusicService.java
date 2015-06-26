package com.windsoft.means;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.windsoft.means.activity.MainActivity;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MusicService extends Service {

    private final String TAG = "MusicService";

    private MusicSockt socket;

    private boolean getSong = false;

    public MusicService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String command = intent.getStringExtra(Global.COMMAND_KEY);

            if (command != null) {
                if (command.equals(Global.CHECK_DB)) {      // DB 체크
                    commandCheckDB();
                } else if (command.equals(Global.REQ_DB)) {     // DB 새로고침 요청
                    int i = Global.pref.getInt(Global.SONG_INDEX, 0);
                    int j = Global.pref.getInt(Global.PAGE_INDEX, 1);
                    commandReqDB(i, j);
                    if (++i == Global.SONG_LIST.length) i = 0;
                    if (++j == 6) i = 1;
                    Global.editor.putInt(Global.SONG_INDEX, i);
                    Global.editor.putInt(Global.PAGE_INDEX, j);
                } else if (command.equals(Global.LOGIN_KEY)) {
                    commandLoginKey(intent);
                } else if (command.equals(Global.CONNECT_SERVER)) {
                    commandConnectServer();
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }


    /*
    * TODO: 서버 연결
    * */
    private void commandConnectServer() {
        try {
            Log.d(TAG, "commandConnectServer()");
            Log.d(TAG, Global.SERVER_URL);

            Socket curSocket = IO.socket(Global.SERVER_URL);;

            socket = new MusicSockt(this, curSocket);
        } catch (Exception e) {
            Log.e(TAG, "commandConnectServer() 에러 = " + e.getMessage());
        }
    }


    /*
    * TODO: 로그인 or 회원가입 요청 서버 전송
    * @param: intent = userId값
    * */
    private void commandLoginKey(final Intent intent) {
        Log.d(TAG, "commandLoginKey()");
        String id = intent.getStringExtra(Global.LOGIN_ID_KEY);
        socket.login(id);
    }


    /*
    * TODO: 장르별 노래 데이터 받아오기
    * */
    private void commandReqDB(final int i, final int j) {
        Log.d(TAG, "commandReqDB()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                getSong = false;

                    /*
                    * TODO:Genie 사이트에서 장르별 노래 데이터 파싱
                    * @param: Global.SONG_LIST[i] = 장르 데이터, j = page
                    * */
                getSongChart(Global.SONG_LIST[i] + j);

                while (!getSong) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {

                    }
                }

                Intent intent = new Intent(MusicService.this, MainActivity.class);
                intent.putExtra(Global.COMMAND_KEY, Global.RES_DB);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                Log.d(TAG, "Intent ResDB");
            }
        }).start();
    }


    private void getSongChart(final String urlStr) {
        try {
            ArrayList<String> songs = new ArrayList<>();

            Log.d(TAG, "url = " + urlStr);
            URL url = new URL(urlStr);
            InputStream is = url.openStream();
            Source source = new Source(new InputStreamReader(is, "utf-8"));
            source.fullSequentialParse();

            List<Element> list = source.getAllElements(HTMLElementName.INPUT);

            for (Element input : list) {
                String title = input.getAttributeValue("title");

                if (title != null) {
                    title = title.replace("'", "\"");
                    songs.add(title);
                }
            }

            ArrayList<String> artists = new ArrayList<>();

            Log.d(TAG, "url = " + urlStr);
            url = new URL(urlStr);
            is = url.openStream();
            source = new Source(new InputStreamReader(is, "utf-8"));
            source.fullSequentialParse();

            list = source.getAllElements(HTMLElementName.SPAN);

            for (Element span : list) {
                String curClass = span.getAttributeValue("class");

                if (curClass != null && curClass.equals("meta")) {
                    List<Element> aList = span.getAllElements(HTMLElementName.A);

                    for (Element a : aList) {
                        String artist = (a.getContent().toString()).replaceAll("'", "\"");
                        artists.add(artist);
                        break;
                    }
                }
            }

            Log.d(TAG, "노래 사이즈 = " + songs.size());
            Log.d(TAG, "아티스트 사이즈 = " + artists.size());

            for (int i = 0; i < songs.size(); i++) {
                if (Global.manager.find(songs.get(i), artists.get(i)) == null) {
                    Global.manager.insert(songs.get(i), artists.get(i));
                }
            }

            getSong = true;

            is.close();
            source.clearCache();
        } catch (Exception e) {
            Log.e(TAG, "getSongChart() 에러 = " + e.getMessage());
        }
    }


    private void commandCheckDB() {
        Log.d(TAG, "commandCheckDB()");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Global.manager = new DBManager(getApplicationContext(), Global.DB_NAME, null, 1);

                    // 없다면
                    if (!isCheckDB()) {
                        Log.d(TAG, "DB 없음");

                        long now = System.currentTimeMillis();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(now);
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);

                        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        Intent intent = new Intent(MusicService.this, MusicService.class);
                        intent.putExtra(Global.COMMAND_KEY, Global.REQ_DB);
                        PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
                        manager.cancel(pIntent);
                        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pIntent);

                        startService(intent);
                    } else {
                        Log.d(TAG, "DB 있음");
                        Intent intent = new Intent(MusicService.this, MainActivity.class);
                        intent.putExtra(Global.COMMAND_KEY, Global.RES_DB);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "commandCheckDB() 에러 = " + e.getMessage());
                }

            }
        }).start();
    }

    private boolean isCheckDB() {
        Log.d(TAG, "패키지네임 = " + getPackageName());
        String filePath = "/data/data/" + getPackageName() + "/databases/" + Global.DB_NAME;

        File file = new File(filePath);
        if (file.exists())
            return true;
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
