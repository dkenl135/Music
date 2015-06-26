package com.windsoft.means.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.windsoft.means.Global;
import com.windsoft.means.R;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Random;

/**
 * Created by dongkyu on 2015-06-21.
 */
public class AppraisalFragment extends Fragment {

    private static final String TAG = "AppraisalFragment";

    private TextView title;
    private ImageView image;

    private int size;
    private String src;

    private Handler handler = new Handler();

    public static AppraisalFragment newInstance(int size) {

        AppraisalFragment fragment = new AppraisalFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("size", size);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        size = getArguments().getInt("size");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appraisal, container, false);
        Log.d(TAG, "onCreateView()");


        Random random = new Random();
        int index = random.nextInt(size);
        String titleStr = ((MainActivity) getActivity()).getSong(index);
        String artist = ((MainActivity) getActivity()).getArtist(index);

        if (titleStr.length() > 15) {                           // 10글자 넘어가면 ...으로 자르기
            titleStr = titleStr.substring(0, 15) + "...";
        }

        title = (TextView) view.findViewById(R.id.fragment_appraisal_title);
        title.setText(titleStr);
        Log.i(TAG, "title = " + titleStr);

        image = (ImageView) view.findViewById(R.id.fragment_appraisal_image);
        getPhotoSrc(titleStr, artist);

        return view;
    }


    private void getPhotoSrc(String titleStr, String artist) {
        final String query = (Global.QUERY_GENIE_MUSIC + artist + " " + titleStr).replaceAll(" ", "%20");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "query URL = " + query);
                    URL url = new URL(query);
                    InputStream is = url.openStream();
                    Source source = new Source(new InputStreamReader(is, "utf-8"));
                    source.fullSequentialParse();

                    List<Element> divList = source.getAllElements(HTMLElementName.DIV);

                    for (Element div : divList) {
                        String songId = div.getAttributeValue("songid");

                        if (songId == null) continue;

                        List<Element> spanList = div.getAllElements(HTMLElementName.SPAN);
                        if ((spanList.toString().equals("[]"))) continue;

                        for (Element span : spanList) {
                            List<Element> aList = span.getAllElements(HTMLElementName.A);
                            if ((aList.toString().equals("[]"))) continue;

                            for (Element a : aList) {
                                List<Element> imgList = a.getAllElements(HTMLElementName.IMG);
                                if ((imgList.toString().equals("[]"))) continue;

                                for (Element img : imgList) {
                                    Log.d(TAG, "img = " + img);

                                    src = "http:" +  img.getAttributeValue("src");
                                    Log.d(TAG, "src = " + src);

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Picasso.with(getActivity())
                                                    .load(src)
                                                    .resize(500, 500)
                                                    .placeholder(R.mipmap.ic_launcher)
                                                    .into(image);
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    }

                    is.close();
                    source.clearCache();
                } catch (Exception e) {
                    Log.e(TAG, "getPhotoSrc() 에러 = " + e.getMessage());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            image.setImageResource(R.mipmap.ic_launcher);
                        }
                    });
                }
            }
        }).start();
    }

}
