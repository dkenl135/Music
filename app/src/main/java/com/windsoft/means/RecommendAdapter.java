package com.windsoft.means;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dongkyu on 2015-07-04.
 */
public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.ViewHolder> {

    private ArrayList<String> songTitleList = new ArrayList<>();


    public RecommendAdapter(ArrayList<String> songTitleList) {
        this.songTitleList = songTitleList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_recommend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.songTitle.setText(songTitleList.get(position));
    }

    @Override
    public int getItemCount() {
        return songTitleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView songTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            songTitle = (TextView) itemView.findViewById(R.id.item_recommend_song_title);
        }
    }
}
