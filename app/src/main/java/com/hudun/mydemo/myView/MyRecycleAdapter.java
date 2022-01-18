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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.hudun.mydemo.R;

import java.io.IOException;
import java.util.HashMap;
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
    private HashMap<String, Integer> map;
    public MyRecycleAdapter(List<AudioItem> list,MediaPlayer player){
        audioItemList = list;
        map = new HashMap();
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
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkBox.setChecked(true);
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
        RelativeLayout relativeLayout;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            audioName = itemView.findViewById(R.id.tv_audio_name);
            checkBox = itemView.findViewById(R.id.checkbox);
            relativeLayout = itemView.findViewById(R.id.re_audio_pick);
        }
    }
}
