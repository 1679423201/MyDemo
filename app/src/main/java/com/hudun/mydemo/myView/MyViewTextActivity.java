package com.hudun.mydemo.myView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.hudun.mydemo.R;
import com.hudun.mydemo.app.MyApplication;
import com.hudun.mydemo.untils.AudioUntil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MyViewTextActivity extends AppCompatActivity {

    private AudioCropView audioCropView;
    private static final String TAG = "VIEW_TEXT";
    private SeekBar seekBar;
    AudioItem item;
    private ArrayList<AudioItem> itemArrayList = null;
    private MediaPlayer player;
    private Timer timer;
    boolean frag;
    private int currentPosition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_view_test);
        item = getIntent().getParcelableExtra(AudioUntil.MUSIC_FRAG);
        timer = new Timer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                player = new MediaPlayer();
                player.reset();
                try {
                    player.setDataSource(item.getPath());
                    player.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        audioCropView = findViewById(R.id.mAudioView);
        seekBar = findViewById(R.id.sb_control);
        
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onPause: ");
        audioCropView.setTotal(item.getDuration());
        Log.d(TAG, "onPause: ==" + item.getDuration());
        seekBar.setMax(audioCropView.getTotal());
        setSB();
        player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                Log.d(TAG, "onSeekComplete: ");
            }
        });
        super.onResume();
    }

    void setSB(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged: ");
                currentPosition = progress;
                audioCropView.setPlayingTime(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                frag = false;
                Log.d(TAG, "onStartTrackingTouch: ");
                player.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startPlaying();
                Log.d(TAG, "onStopTrackingTouch: ");
            }
        });
    }



    public void startPlay(View view) {
        startPlaying();
        Log.d(TAG, "startPlay: ");
    }

    public void stopPlay(View view) {
        frag = false;
        timer.purge();
        player.pause();
        Log.d(TAG, "stopPlay: ");
    }

    void startPlaying(){
        frag = true;
        player.start();
        player.seekTo(currentPosition);
        Log.d(TAG, "startPlaying: " + currentPosition);
        player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                player.stop();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(frag){
//                    Log.d(TAG, "startPlaying: +" + player.getCurrentPosition());
                            seekBar.setProgress(player.getCurrentPosition());
                        }
                    }
                }, 0, 50);
            }
        });

    }



    Thread thread = new Thread(new Runnable() {

        @Override
        public void run() {

        }
    });




    @Override
    protected void onDestroy() {
        timer.cancel();
        player.release();
        super.onDestroy();
    }
}
