package com.windsoft.means.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.windsoft.means.Global;
import com.windsoft.means.R;


public class ShowcaseActivity extends Activity {

    private RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase);

        Intent intent = getIntent();
        final String id = intent.getStringExtra(Global.ID_KEY);
        final String[] nameList = intent.getStringArrayExtra(Global.SONG_NAME_KEY);
        final int[] scoreList = intent.getIntArrayExtra(Global.SONG_SCORE_KEY);

        container = (RelativeLayout) findViewById(R.id.activity_showcase_container);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowcaseActivity.this, MainActivity.class);
                intent.putExtra(Global.ID_KEY, id);
                intent.putExtra(Global.SONG_NAME_KEY, nameList);
                intent.putExtra(Global.SONG_SCORE_KEY, scoreList);
                startActivity(intent);
                finish();
            }
        });
    }
}
