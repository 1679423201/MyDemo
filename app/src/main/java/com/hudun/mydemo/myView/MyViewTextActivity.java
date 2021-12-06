package com.hudun.mydemo.myView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.hudun.mydemo.R;

import java.util.Timer;
import java.util.TimerTask;

public class MyViewTextActivity extends AppCompatActivity {

    private AudioCropView audioCropView;
    private static final String TAG = "VIEW_TEXT";
    float time = 0;
    private Timer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_view_text);
        audioCropView = findViewById(R.id.mAudioView);
        if(audioCropView == null){
            Log.d(TAG, "onCreate:  View为空");
        }else {
            Log.d(TAG, "onCreate: View 不是空");
        }

    }

    private boolean frag = false;
    Object object = new Object();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            synchronized (object){
                Log.d(TAG, "run: ++"+time);
                if(frag){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    audioCropView.startPlaying(time);
                    time += 0.1f;
                    Log.d(TAG, "run: " + time);
                }
            }
        }
    };
    public void startPlay(View view) {
        if(timer == null){
            timer = new Timer("播放定时");
            timer.scheduleAtFixedRate(timerTask, 100,100);
        }else {
            synchronized (object){
                frag = false;
                object.notifyAll();
            }
        }

    }


    public void stopPlay(View view) {
        frag = true;
    }
    @Override
    protected void onDestroy() {
        if(timer != null){
            timer.cancel();
        }
        super.onDestroy();
    }

}