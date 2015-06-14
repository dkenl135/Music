package com.windsoft.music;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String xml = getSource(Global.OST + 1);
    }

    private String getSource(final String urlStr) {
        String xml = null;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlStr);
                    InputStream is = url.openStream();

                    Source source = new Source(new InputStreamReader(is, "utf-8"));
                    source.fullSequentialParse();

                    List<Element> list = source.getAllElements(HTMLElementName.INPUT);

                    Log.e(TAG, "list = " + list);
                    for (Element input : list) {
                        String type = input.getAttributeValue("type");
                        String className = input.getAttributeValue("class");
                        String title = input.getAttributeValue("title");

                        Log.e(TAG, "type = " + type);
                        Log.e(TAG, "class = " + className);
                        Log.e(TAG, "title = " + title);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "에러 = " + e.getMessage());
                }
            }
        }).start();

        return xml;
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
