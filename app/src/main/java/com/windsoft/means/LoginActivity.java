package com.windsoft.means;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;


public class LoginActivity extends ActionBarActivity {

    private final String TAG = "LoginActivity";

    private LoginButton loginButton;

    private LinearLayout layout;

    private CallbackManager callback;

    private AccessTokenTracker tracker;

    private boolean isServerConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        connectServer();

        facebookLogin();
    }


    /*
    * TODO: 서버연결
    * */
    private void connectServer() {
        Intent intent = new Intent(LoginActivity.this, MusicService.class);
        intent.putExtra(Global.COMMAND_KEY, Global.CONNECT_SERVER);
        startService(intent);
    }


    /*
    * TODO: 페이스북 로그인
    * */
    private void facebookLogin() {
        FacebookSdk.sdkInitialize(this);
        layout = (LinearLayout) findViewById(R.id.activity_login_container);
        loginButton = new LoginButton(this);
        loginButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isServerConnected) {
                        Toast.makeText(getApplicationContext(), "서버 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        layout.addView(loginButton);
        loginButton.setReadPermissions("public_profile");

        callback = CallbackManager.Factory.create();
        tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                if (oldToken != null) {     // 로그아웃
                    Log.d(TAG, "oldToken.token = " + oldToken.getToken());
                    Log.d(TAG, "oldToken.id = " + oldToken.getUserId());
                }

                if (newToken != null) {     // 로그인
                    Log.d(TAG, "new.token = " + newToken.getToken());
                    Log.d(TAG, "new.id = " + newToken.getUserId());

                    signUp(newToken.getUserId());
                }
            }
        };
        loginButton.registerCallback(callback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), "성공하였습니다!", Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent(LoginActivity.this, MusicService.class);
//                intent.putExtra(Global.COMMAND_KEY, Global.sign)
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplicationContext(), "에러!", Toast.LENGTH_SHORT).show();
            }
        });

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }


    /*
    * TODO: 회원가입 정보 서버 전송 메소드
    * @param: token = 고유 회원 코드
    * */
    private void signUp(String id) {
        Intent intent = new Intent(LoginActivity.this, MusicService.class);
        intent.putExtra(Global.COMMAND_KEY, Global.LOGIN_KEY);        // 회원 가입
        intent.putExtra(Global.LOGIN_ID_KEY, id);           // 아이디
        startService(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callback.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onResume() {
        AppEventsLogger.activateApp(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        AppEventsLogger.deactivateApp(this);
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        tracker.stopTracking();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            String command = intent.getStringExtra(Global.COMMAND_KEY);

            if (command != null) {
                if (command.equals(Global.CONNECT_SERVER)) {
                    processConnectServerCommand(intent);
                } else if (command.equals(Global.LOGIN_KEY)) {
                    processLogin(intent);
                }
            }
        }

        super.onNewIntent(intent);
    }


    /*
    * TODO: 로그인 연결 RES
    * @param : intent = 에러 정보
    * */
    private void processLogin(Intent intent) {
        String id = intent.getStringExtra(Global.USER_ID);
        int cond = intent.getIntExtra(Global.COND, 0);

        if (cond == Global.ERROR) {
            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
        } else if (cond == Global.SUCCESS) {
            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();

            Global.ID = id;
        }
    }


    /*
    * TODO: 서버 연결 RES
    * @param: intent = 에러 정보
    * */
    private void processConnectServerCommand(Intent intent) {
        int cond = intent.getIntExtra(Global.COND, Global.ERROR);

        if (cond == Global.ERROR) {
            Log.d(TAG, "서버 연결 실패");
            Toast.makeText(getApplicationContext(), "서버 연결 실패!", Toast.LENGTH_SHORT).show();
            isServerConnected = false;
        } else if (cond == Global.SUCCESS) {
            Log.d(TAG, "서버 연결 성공");
            Toast.makeText(getApplicationContext(), "서버 연결 성공!", Toast.LENGTH_SHORT).show();
            isServerConnected = true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
