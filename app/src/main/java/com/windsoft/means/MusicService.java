package com.windsoft.means;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MusicService extends Service {

    private final String TAG = "MusicService";

    private Socket socket;

    public MusicService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String command = intent.getStringExtra(Global.COMMAND_KEY);

            if (command != null) {
                processCommand(command, intent);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void processCommand(String command, Intent intent) {
        if (command.equals(Global.CHECK_DB)) {      // DB 체크
            commandCheckDB();
        } else if (command.equals(Global.REQ_DB)) {     // DB 새로고침 요청
            commandReqDB();
        } else if (command.equals(Global.LOGIN_KEY)) {
            commandLoginKey(intent);
        } else if (command.equals(Global.CONNECT_SERVER)) {
            commandConnectServer();
        }
    }

    /*
    * TODO: 서버 연결
    * */
    private void commandConnectServer() {
        try {
            socket = IO.socket(Global.SERVER_URL);

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "연결");
                    int cond = (int) args[0];

                    Intent intent = new Intent(MusicService.this, LoginActivity.class);
                    intent.putExtra(Global.COMMAND_KEY, Global.CONNECT_SERVER);
                    intent.putExtra(Global.COND, cond);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

            socket.open();
            socket.connect();

        } catch (Exception e) {
            Log.e(TAG, "commandConnectServer() 에러 = " + e.getMessage());
        }
    }


    /*
    * TODO: 로그인 or 회원가입 요청 서버 전송
    * @param: intent = userId값
    * */
    private void commandLoginKey(final Intent intent) {
        try {
            String id = intent.getStringExtra(Global.LOGIN_ID_KEY);

            JSONObject obj = new JSONObject();
            obj.put(Global.USER_ID, id);

            socket.emit(Global.LOGIN_KEY, obj);
            socket.on(Global.LOGIN_KEY, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String id = (String) args[0];
                    int cond = (int) args[1];

                    Intent intent1 = new Intent(MusicService.this, LoginActivity.class);
                    intent1.putExtra(Global.USER_ID, id);
                    intent1.putExtra(Global.COND, cond);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "commandLoginKey() 에러 = " + e.getMessage());
        }
    }


    /*
    * TODO: 장르별 노래 데이터 받아오기
    * */
    private void commandReqDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < Global.SONG_LIST.length; i++) {
                    for (int j = 1; j <= 5; j++) {

                        /*
                        * TODO:Genie 사이트에서 장르별 노래 데이터 파싱
                        * @param: Global.SONG_LIST[i] = 장르 데이터, j = page
                        * */
                        ArrayList<String> musicList = getSource(Global.SONG_LIST[i] + j);

                        for (int k = 0; k < musicList.size(); k++) {
                            Global.manager.insert(Global.SONG_LIST[i], musicList.get(k));   // DB 입력
                        }
                    }
                }

                Intent intent = new Intent(MusicService.this, MainActivity.class);
                intent.putExtra(Global.COMMAND_KEY, Global.RES_DB);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).start();
    }

    private ArrayList<String> getSource(final String urlStr) {
        final ArrayList<String> musicList = new ArrayList<>();

        try {
            URL url = new URL(urlStr);
            InputStream is = url.openStream();

            Source source = new Source(new InputStreamReader(is, "utf-8"));
            source.fullSequentialParse();

            List<Element> list = source.getAllElements(HTMLElementName.INPUT);

            Log.e(TAG, "list = " + list);
            for (Element input : list) {
                String title = input.getAttributeValue("title");

                if (title != null) {
                    musicList.add(title);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "에러 = " + e.getMessage());
        }

        return musicList;
    }

    private void commandCheckDB() {
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
                        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24 * 7, pIntent);

                        startService(intent);
                    } else {
                        Log.d(TAG, "DB 있음");
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
