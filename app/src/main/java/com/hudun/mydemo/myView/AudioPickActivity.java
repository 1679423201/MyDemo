package com.hudun.mydemo.myView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hudun.mydemo.R;
import com.hudun.mydemo.untils.AudioUntil;

import java.util.List;

public class AudioPickActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Button button;
    Toolbar toolbar;
    List<AudioItem> audioItemList;
    MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_audio_pick);
        LinearLayout layout = findViewById(R.id.include_toolbar);
        toolbar = layout.findViewById(R.id.tb_pick);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycler_audio);

        getAudioList();
        player = new MediaPlayer();
        MyRecycleAdapter adapter = new MyRecycleAdapter(audioItemList, player);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    void getAudioList(){
        audioItemList = AudioUntil.seekAudios();
    }

    @Override
    protected void onStop() {
        player.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        player.release();
        super.onDestroy();
    }
}