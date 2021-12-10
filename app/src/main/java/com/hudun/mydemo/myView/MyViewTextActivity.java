package com.hudun.mydemo.myView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaDescription;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.hudun.mydemo.R;

import java.util.ArrayList;
import java.util.Timer;

public class MyViewTextActivity extends AppCompatActivity {

    private AudioCropView audioCropView;
    private static final String TAG = "VIEW_TEXT";
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private ArrayList<MediaItem> items = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_view_test);

        audioCropView = findViewById(R.id.mAudioView);
        seekBar = findViewById(R.id.sb_control);
        seekBar.setMax(audioCropView.getTotal());
        setSB();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
    }

    void setSB(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioCropView.setPlayingTime(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }



    public void startPlay(View view) {
    }


    public void stopPlay(View view) {
    }

    private void getDataForLocal(){
        items = new ArrayList<MediaItem>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] obj = {
                        MediaStore.Audio.Media.DISPLAY_NAME, //音频文件在SD卡中的名称
                        MediaStore.Audio.Media.DURATION,    //音频总时长
                        MediaStore.Audio.Media.SIZE,        //音频的文件大小
                        MediaStore.Audio.Media.DATA,        //音频的绝对地址
                        MediaStore.Audio.Media.ARTIST,      //歌曲的演唱者
                };
                Cursor cursor = resolver.query(uri, obj, null, null, null);
                if(cursor != null){
                    while (cursor.moveToNext()){
                        MediaItem item = new MediaItem();
                        item.setName(cursor.getString(0));
                        item.setDuration(cursor.getLong(1));
                        item.setSize(cursor.getLong(2));
                        item.setData(cursor.getString(3));
                        item.setArtist(cursor.getString(4));
                    }
                    cursor.close();
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                if (items != null && items.size() > 0) {
                    //进行相关操作
                } else {
                    Toast.makeText(MyViewTextActivity.this, "没有找到视频", Toast.LENGTH_SHORT);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
