package com.windsoft.means.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;
import com.windsoft.means.Global;
import com.windsoft.means.MusicService;
import com.windsoft.means.R;
import com.windsoft.means.login.FacebookLogin;
import com.windsoft.means.login.GoogleLogin;
import com.windsoft.means.login.NaverLogin;


public class LoginActivity extends Activity {

    private final String TAG = "LoginActivity";

    private LinearLayout layout;

    private boolean isServerConnected = false;

    /*
    * TODO: 앱 실행 횟수 카운트
    * 0 = 처음, showcaseView 보여짐
    * */
    private int count;

    private LoginButton facebookLoginButton;

    private SignInButton googleLoginBtn;

    private OAuthLoginButton naverLoginBtn;

    private NaverLogin naverLogin;

    private FacebookLogin facebookLogin;

    private GoogleLogin googleLogin;

    private String id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(this);

        createInstance();

        naverLogin = new NaverLogin(LoginActivity.this, naverLoginBtn);
        naverLogin.login();

        googleLogin = new GoogleLogin(LoginActivity.this, googleLoginBtn);

        facebookLogin = new FacebookLogin(LoginActivity.this, facebookLoginButton);

        connectServer();

        getLoginInfo();
    }


    private void getLoginInfo() {
        id = Global.pref.getString(Global.LOGIN_ID_KEY, null);

        if (id != null) {
            login(id);
        }
    }


    private void createInstance() {
        layout = (LinearLayout) findViewById(R.id.activity_login_container);

        naverLoginBtn = new OAuthLoginButton(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                120
        );
        params.setMargins(10, 50, 10, 0);

        facebookLoginButton = new LoginButton(this);
        facebookLoginButton.setLayoutParams(params);
        layout.addView(facebookLoginButton);

        naverLoginBtn.setLayoutParams(params);

        layout.addView(naverLoginBtn);


        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                130
        );
        params.setMargins(0, 50, 0, 0);

        googleLoginBtn = new SignInButton(this);
        googleLoginBtn.setLayoutParams(params);
        layout.addView(googleLoginBtn);


        Global.pref = getSharedPreferences(Global.PREF_KEY, MODE_PRIVATE);

        count = Global.pref.getInt(Global.COUNT_KEY, 0);

        Global.editor = Global.pref.edit();
    }


    private void addCount() {
        Global.editor.putInt(Global.COUNT_KEY, ++count);
        Global.editor.commit();
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
    * TODO: 회원가입 정보 서버 전송 메소드
    * @param: token = 고유 회원 코드
    * */
    public void login(String id) {
        Intent intent = new Intent(LoginActivity.this, MusicService.class);
        intent.putExtra(Global.COMMAND_KEY, Global.LOGIN_KEY);        // 회원 가입
        intent.putExtra(Global.LOGIN_ID_KEY, id);                   // 아이디
        startService(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookLogin.activityCallback(requestCode, resultCode, data);

        if (requestCode == ConnectionResult.SIGN_IN_REQUIRED) {
            Log.d(TAG, "구글 재접속...");
            googleLogin.clientReconnect();
        }
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
        facebookLogin.stopTracking();
        googleLogin.clientDisconnect();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            String command = intent.getStringExtra(Global.COMMAND_KEY);

            if (command != null) {
                if (command.equals(Global.CONNECT_SERVER)) {                    // 서버 접속 완료
                    processConnectServerCommand();
                } else if (command.equals(Global.LOGIN_KEY)) {                  // 로그인 완료
                    String id = intent.getStringExtra(Global.ID_KEY);
                    int cond = intent.getIntExtra(Global.COND, 0);

                    processLogin(cond, id);
                }
            }
        }

        super.onNewIntent(intent);
    }


    /*
    * TODO: 로그인 연결 RES
    * @param : intent = 에러 정보
    * */
    private void processLogin(int cond, String id) {
        Log.d(TAG, "processLogin()");


        if (cond == Global.ERROR) {
            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
        } else if (cond == Global.SUCCESS) {
            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();

            Global.ID = id;

            Intent intent;
            if (count == 0) {                   // 앱을 처음 실행
                intent = new Intent(LoginActivity.this, ShowcaseActivity.class);                    // 쇼케이스 뷰
            } else {
                intent = new Intent(LoginActivity.this, MainActivity.class);                        // 메인 액티비티
            }

            addCount();                     // 앱 실행 횟수 증가
            startActivity(intent);
            finish();
        }
    }


    /*
    * TODO: 서버 연결 RES
    * @param: intent = 에러 정보
    * */
    private void processConnectServerCommand() {
        Log.d(TAG, "processConnectServerCommand()");
        Toast.makeText(getApplicationContext(), "서버 연결 성공!", Toast.LENGTH_SHORT).show();
        isServerConnected = true;
    }
}
