package com.hudun.mydemo.myView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
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
    private Context context;
    private List<AudioItem> audioItemList;
    MediaPlayer player = null;
    private int currentAudio = -1;
    private int frag = -1;
    private CheckBox checkedBox = null;
    public MyRecycleAdapter(List<AudioItem> list,MediaPlayer player){

        audioItemList = list;
        this.player = player;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_pick_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.audioName.setText(audioItemList.get(position).getName());
        int data = position;
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /***
                 *设置为单选框，采用frag标记是否有其他已选择的按钮
                 * 若没有则为-1，有则为其对应的position
                 */
                if(isChecked){
                    if (frag != -1) {
                        checkedBox.setChecked(false);
                        checkedBox.setClickable(true);
                    }
                    checkedBox = (CheckBox) buttonView;
                    buttonView.setClickable(false);
                    frag = data;
                }
            }
        });

        holder.turnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentAudio == data){
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
                        player.setDataSource(audioItemList.get(data).getPath());
                        player.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    player.start();
                    currentAudio = data;
                }
                //播放
            }
        });
    }

    public int getFrag() {
        return frag;
    }

    @Override
    public int getItemCount() {
        return audioItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView audioName;
        Button turnPlay;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            audioName = itemView.findViewById(R.id.tv_audio_name);
            turnPlay = itemView.findViewById(R.id.bt_turn_play);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
