package com.windsoft.means.login;

import android.app.Activity;
import android.util.Log;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;
import com.windsoft.means.Global;
import com.windsoft.means.activity.LoginActivity;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by dongkyu on 2015-06-17.
 */
public class NaverLogin {

    private static final String TAG = "NaverLogin";

    private static final String ID = "6RjDIuTT6fz3XWQ0GKy3";

    private static final String KEY = "biCQAy9sQv";

    private static final String INTENT = "com.windsoft.means.action.MAIN";

    private Activity activity;

    private OAuthLogin naverModule;

    private OAuthLoginHandler handler;

    private OAuthLoginButton loginButton;

    private String refreshToken = null;

    private String accessToken = null;

    public NaverLogin(Activity activity) {
        this.activity = activity;

        init();
    }

    public NaverLogin(Activity activity, OAuthLoginButton loginButton) {
        this.activity = activity;
        this.loginButton = loginButton;

        init();
    }

    private void init() {
        naverModule = OAuthLogin.getInstance();
        naverModule.init(activity, ID, KEY, "Muzik", INTENT);
        naverModule.refreshAccessToken(activity);

        handler = new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if (success) {
                    accessToken = naverModule.getAccessToken(activity);
                    refreshToken = naverModule.getRefreshToken(activity);

                    Log.d(TAG, "accessToken = " + accessToken);
                    Log.d(TAG, "refreshToken = " + refreshToken);

                    getData();
                } else {
                    String errorCode = naverModule.getLastErrorCode(activity).getCode();
                    Log.d(TAG, "에러코드 = " + errorCode);
                }
            }
        };
    }


    public void login() {
        if (loginButton != null) {
            loginButton.setOAuthLoginHandler(handler);
        }
    }


    public void logout() {
        naverModule.logoutAndDeleteToken(activity);
    }


    public void getData() {
        new Thread (new Runnable()  {
            @Override
            public void run() {
                if (accessToken != null) {
                    String url = "https://apis.naver.com/nidlogin/nid/getUserProfile.xml";
                    String data = naverModule.requestApi(activity, accessToken, url);

                    Log.d(TAG, "data = " + data);

                    try {
                        SAXParserFactory factory = SAXParserFactory.newInstance();
                        SAXParser parser = factory.newSAXParser();
                        XMLReader reader = parser.getXMLReader();
                        SaxHandler handler = new SaxHandler();
                        reader.setContentHandler(handler);

                        InputStream is = new ByteArrayInputStream(data.getBytes("utf-8"));
                        reader.parse(new InputSource(is));

                        String email = handler.email.toString();

                        Global.editor.putString(Global.LOGIN_ID_KEY, email);
                        Global.editor.commit();

                        Log.d(TAG, "email = " + email);

                        ((LoginActivity) activity).login(email);

                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }


    private class SaxHandler extends DefaultHandler {
        boolean initem = false;
        StringBuilder email = new StringBuilder();

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
        }


        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }


        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (localName.equals("email")) {
                initem = true;
            }

            super.startElement(uri, localName, qName, attributes);
        }


        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
        }


        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (initem) {
                email.append(ch, start, length);
                initem = false;
            }

            super.characters(ch, start, length);
        }
    }

}
