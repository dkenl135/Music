package com.windsoft.means.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.windsoft.means.R;

/**
 * Created by dongkyu on 2015-07-04.
 */
public class RecommendFragment extends Fragment {

    private String title;

    public RecommendFragment() {
    }


    public static RecommendFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);

        RecommendFragment fragment = new RecommendFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        title = bundle.getString("title");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recommend, container, false);

        TextView title = (TextView) rootView.findViewById(R.id.item_recommend_song_title);
        title.setText(this.title);

        return rootView;
    }
}
