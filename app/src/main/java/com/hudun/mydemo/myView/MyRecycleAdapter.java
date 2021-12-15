package com.hudun.mydemo.myView;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hudun.mydemo.R;

import java.io.IOException;
import java.util.List;

/**
 * <pre>
 *      @ClassName MyRecycleAdapter
 *      @Author  :YMD
 *      @E-mail  :1679423201@qq.com
 *      @Date 2021/12/15 14:01
 *      @Desc    :
 *      @Version :1.0
 * </pre>
 */
public class MyRecycleAdapter extends RecyclerView.Adapter<MyRecycleAdapter.ViewHolder> {
    private static final String TAG = "AUDIO_TEXT";
    private View view;
    private List<AudioItem> audioItemList;
    MediaPlayer player = null;
    private int currentAudio = -1;
    public MyRecycleAdapter(List<AudioItem> list,MediaPlayer player){
        audioItemList = list;
        this.player = player;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_pick_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.audioName.setText(audioItemList.get(position).getName());
        holder.turnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentAudio == position){
                    if(player.isPlaying()){
                        player.pause();
                    }
                    else {
                        player.start();
                    }
                }
                else {
                    try {
                        player.reset();
                        player.setDataSource(audioItemList.get(position).getPath());
                        player.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    player.start();
                    currentAudio = position;
                }
                //播放
            }
        });
    }

    @Override
    public int getItemCount() {
        return audioItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView audioName;
        Button turnPlay;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            audioName = itemView.findViewById(R.id.tv_audio_name);
            turnPlay = itemView.findViewById(R.id.bt_turn_play);
        }
    }
}
